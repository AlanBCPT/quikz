package com.example.quikz.ws;

import com.example.quikz.game.GameEngine;
import com.example.quikz.game.GameRules;
import com.example.quikz.game.TickResult;
import com.example.quikz.protocol.GameProtocol;
import com.example.quikz.protocol.IncomingMessage;
import com.example.quikz.session.GameClock;
import com.example.quikz.session.GameSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {
    private final GameEngine engine;
    private final GameProtocol protocol;
    private final ScheduledExecutorService scheduler;
    private final GameClock clock;
    private final Map<String, GameSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> loops = new ConcurrentHashMap<>();
    private final Map<String, Long> lastTickAt = new ConcurrentHashMap<>();

    public GameWebSocketHandler(
            GameEngine engine,
            GameProtocol protocol,
            ScheduledExecutorService gameLoopScheduler,
            GameClock clock) {
        this.engine = engine;
        this.protocol = protocol;
        this.scheduler = gameLoopScheduler;
        this.clock = clock;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession socket) {
        sessions.put(socket.getId(), new GameSession(engine));
    }

    @Override
    protected void handleTextMessage(WebSocketSession socket, TextMessage message) throws Exception {
        GameSession session = sessions.get(socket.getId());
        if (session == null) {
            return;
        }
        IncomingMessage incoming = protocol.read(message.getPayload());
        synchronized (session) {
            if (session.rebuiltTheField(incoming)) {
                socket.sendMessage(new TextMessage(protocol.initMessage(session.state())));
            }
        }
        if (incoming.startsTickLoop()) {
            startLoop(socket);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession socket, CloseStatus status) {
        sessions.remove(socket.getId());
        stopLoop(socket.getId());
        lastTickAt.remove(socket.getId());
    }

    private void startLoop(WebSocketSession socket) {
        loops.computeIfAbsent(socket.getId(), id -> {
            lastTickAt.put(id, clock.milliseconds());
            return scheduler.scheduleAtFixedRate(
                    () -> runTick(socket),
                    0,
                    GameRules.TICK_PERIOD_MS,
                    TimeUnit.MILLISECONDS);
        });
    }

    private void runTick(WebSocketSession socket) {
        GameSession session = sessions.get(socket.getId());
        if (session == null || !socket.isOpen()) {
            return;
        }
        long current = clock.milliseconds();
        long previous = lastTickAt.getOrDefault(socket.getId(), current);
        double elapsedMilliseconds = Math.min(current - previous, GameRules.MAX_TICK_DELTA_MS);
        lastTickAt.put(socket.getId(), current);
        try {
            TickResult result;
            synchronized (session) {
                result = session.advanceTime(elapsedMilliseconds);
                socket.sendMessage(new TextMessage(protocol.stateMessage(session.state(), result)));
            }
        } catch (IOException disconnected) {
            stopLoop(socket.getId());
        }
    }

    private void stopLoop(String sessionId) {
        ScheduledFuture<?> loop = loops.remove(sessionId);
        if (loop != null) {
            loop.cancel(true);
        }
    }
}
