package com.example.quikz.session;

import com.example.quikz.game.GameEngine;
import com.example.quikz.game.GameState;
import com.example.quikz.game.Phase;
import com.example.quikz.game.TickResult;
import com.example.quikz.protocol.IncomingMessage;

public final class GameSession {
    private final GameState state = new GameState();
    private final GameEngine engine;

    public GameSession(GameEngine engine) {
        this.engine = engine;
    }

    public synchronized boolean rebuiltTheField(IncomingMessage message) {
        return switch (message.command()) {
            case START, RETRY -> {
                startNewGame();
                yield true;
            }
            case CHANGE_DIRECTION -> {
                state.player().request(message.direction());
                yield false;
            }
            case TOGGLE_PAUSE -> {
                state.togglePause();
                yield false;
            }
            case NEXT_LEVEL -> {
                startNextLevel();
                yield true;
            }
            case IGNORE -> false;
        };
    }

    public synchronized TickResult advanceTime(double elapsedMilliseconds) {
        return engine.tick(state, elapsedMilliseconds);
    }

    public GameState state() {
        return state;
    }

    private void startNewGame() {
        state.beginFromLevelOne();
        engine.initializeField(state);
        state.setPhase(Phase.PLAYING);
    }

    private void startNextLevel() {
        state.incrementLevel();
        engine.initializeField(state);
        state.setPhase(Phase.PLAYING);
    }
}
