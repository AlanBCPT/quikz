package com.example.quikz.game;

import java.util.List;

public record TickResult(List<Integer> newlyClaimedIndices, GameEvent event) {
    public TickResult {
        newlyClaimedIndices = List.copyOf(newlyClaimedIndices == null ? List.of() : newlyClaimedIndices);
        if (event == null) {
            event = GameEvent.NONE;
        }
    }

    public static TickResult nothingHappened() {
        return new TickResult(List.of(), GameEvent.NONE);
    }

    public static TickResult claimed(List<Integer> indices) {
        return new TickResult(indices, GameEvent.NONE);
    }

    public static TickResult levelUp(List<Integer> indices) {
        return new TickResult(indices, GameEvent.LEVEL_UP);
    }

    public static TickResult death() {
        return new TickResult(List.of(), GameEvent.DEATH);
    }

    public static TickResult gameOver() {
        return new TickResult(List.of(), GameEvent.GAME_OVER);
    }

    public boolean closedALoop() {
        return !newlyClaimedIndices.isEmpty();
    }

    public String wireEvent() {
        return event.wireName();
    }
}
