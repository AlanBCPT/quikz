package com.example.quikz.ws;

import com.example.quikz.game.ControllableRandom;
import com.example.quikz.game.GameEngine;
import com.example.quikz.game.GameRules;
import com.example.quikz.protocol.GameProtocol;
import com.example.quikz.session.AdjustableGameClock;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GameWebSocketHandlerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final List<String> sent = new ArrayList<>();
    private final AtomicReference<Runnable> loopBody = new AtomicReference<>();
    private AdjustableGameClock clock;
    private WebSocketSession socket;
    private ScheduledFuture<?> future;
    private GameWebSocketHandler handler;

    @BeforeEach
    void connectAClient() throws Exception {
        ControllableRandom random = new ControllableRandom();
        random.neverOccur();
        clock = new AdjustableGameClock();
        clock.set(1_000);
        future = mock(ScheduledFuture.class);
        ScheduledExecutorService scheduler = mock(ScheduledExecutorService.class);
        when(scheduler.scheduleAtFixedRate(any(), eq(0L), eq(GameRules.TICK_PERIOD_MS), eq(TimeUnit.MILLISECONDS)))
                .thenAnswer(invocation -> {
                    loopBody.set(invocation.getArgument(0));
                    return future;
                });
        socket = mock(WebSocketSession.class);
        when(socket.getId()).thenReturn("session-1");
        when(socket.isOpen()).thenReturn(true);
        doAnswer(invocation -> {
            TextMessage message = invocation.getArgument(0);
            sent.add(message.getPayload());
            return null;
        }).when(socket).sendMessage(any());
        handler = new GameWebSocketHandler(
                new GameEngine(random),
                new GameProtocol(mapper),
                scheduler,
                clock);
        handler.afterConnectionEstablished(socket);
    }

    @Test
    void startSendsInitAndOpensTheTickLoop() throws Exception {
        handler.handleTextMessage(socket, text("{\"type\":\"start\"}"));
        assertThat(sent).hasSize(1);
        JsonNode init = mapper.readTree(sent.get(0));
        assertThat(init.get("type").asText()).isEqualTo("init");
        assertThat(loopBody.get()).isNotNull();
    }

    @Test
    void retryDoesNotStartASecondLoop() throws Exception {
        handler.handleTextMessage(socket, text("{\"type\":\"start\"}"));
        handler.handleTextMessage(socket, text("{\"type\":\"retry\"}"));
        assertThat(sent).hasSize(2);
        assertThat(mapper.readTree(sent.get(1)).get("type").asText()).isEqualTo("init");
    }

    @Test
    void directionPauseAndUnknownTypesDoNotSendInit() throws Exception {
        handler.handleTextMessage(socket, text("{\"type\":\"start\"}"));
        sent.clear();
        handler.handleTextMessage(socket, text("{\"type\":\"dir\",\"dx\":1,\"dy\":0}"));
        handler.handleTextMessage(socket, text("{\"type\":\"pause\"}"));
        handler.handleTextMessage(socket, text("{\"type\":\"nope\"}"));
        assertThat(sent).isEmpty();
    }

    @Test
    void nextSendsAFreshInitWithoutStartingAnotherLoop() throws Exception {
        handler.handleTextMessage(socket, text("{\"type\":\"start\"}"));
        sent.clear();
        handler.handleTextMessage(socket, text("{\"type\":\"next\"}"));
        assertThat(sent).hasSize(1);
        assertThat(mapper.readTree(sent.get(0)).get("type").asText()).isEqualTo("init");
    }

    @Test
    void aTickPushesStateAndCapsElapsedTime() throws Exception {
        handler.handleTextMessage(socket, text("{\"type\":\"start\"}"));
        sent.clear();
        clock.advance(200);
        loopBody.get().run();
        JsonNode state = mapper.readTree(sent.get(0));
        assertThat(state.get("type").asText()).isEqualTo("state");
        assertThat(state.get("phase").asText()).isEqualTo("playing");
    }

    @Test
    void aClosedSocketSkipsTheTick() throws Exception {
        handler.handleTextMessage(socket, text("{\"type\":\"start\"}"));
        sent.clear();
        when(socket.isOpen()).thenReturn(false);
        loopBody.get().run();
        assertThat(sent).isEmpty();
    }

    @Test
    void aSendFailureCancelsTheLoop() throws Exception {
        handler.handleTextMessage(socket, text("{\"type\":\"start\"}"));
        doThrow(new IOException("gone")).when(socket).sendMessage(any());
        loopBody.get().run();
        verify(future).cancel(true);
    }

    @Test
    void closingTheConnectionStopsTheLoop() throws Exception {
        handler.handleTextMessage(socket, text("{\"type\":\"start\"}"));
        handler.afterConnectionClosed(socket, CloseStatus.NORMAL);
        verify(future).cancel(true);
        sent.clear();
        loopBody.get().run();
        assertThat(sent).isEmpty();
    }

    @Test
    void messagesForUnknownSessionsAreIgnored() throws Exception {
        WebSocketSession stranger = mock(WebSocketSession.class);
        when(stranger.getId()).thenReturn("missing");
        handler.handleTextMessage(stranger, text("{\"type\":\"start\"}"));
        verify(stranger, never()).sendMessage(any());
    }

    private static TextMessage text(String json) {
        return new TextMessage(json);
    }
}
