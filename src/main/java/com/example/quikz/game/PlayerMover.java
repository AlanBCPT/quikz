package com.example.quikz.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class PlayerMover {
    Optional<TickResult> advance(GameState state, double elapsedMilliseconds) {
        Player player = state.player();
        player.addElapsedTime(elapsedMilliseconds);
        while (player.consumeStepIfReady()) {
            Optional<TickResult> closedLoop = attemptStep(state);
            if (closedLoop.isPresent()) {
                return closedLoop;
            }
        }
        return Optional.empty();
    }

    private Optional<TickResult> attemptStep(GameState state) {
        Player player = state.player();
        Direction direction = player.requestedDirection();
        if (direction.isStationary()) {
            return Optional.empty();
        }
        Cell destination = direction.from(player.position());
        Grid grid = state.grid();
        if (!grid.contains(destination) || grid.isTrail(destination)) {
            return Optional.empty();
        }
        if (player.isDrawing()) {
            return continueDrawing(state, destination);
        }
        walkOrBeginDrawing(state, destination);
        return Optional.empty();
    }

    private Optional<TickResult> continueDrawing(GameState state, Cell destination) {
        Grid grid = state.grid();
        Player player = state.player();
        if (grid.isClaimed(destination)) {
            player.moveTo(destination);
            return Optional.of(closeLoop(state));
        }
        player.extendTrailTo(destination, grid);
        return Optional.empty();
    }

    private void walkOrBeginDrawing(GameState state, Cell destination) {
        Grid grid = state.grid();
        Player player = state.player();
        if (grid.isClaimed(destination)) {
            player.moveTo(destination);
            return;
        }
        player.beginDrawingToward(destination, grid);
    }

    private TickResult closeLoop(GameState state) {
        Player player = state.player();
        Grid grid = state.grid();
        int trailLength = player.trail().size();
        List<Integer> newlyClaimed = new ArrayList<>();
        for (Cell cell : player.trail()) {
            grid.claim(cell);
            newlyClaimed.add(grid.index(cell));
        }
        player.finishDrawing();

        List<Integer> flooded = FloodFill.claimEmptyRegionsNotContaining(grid, state.qix().nearestCell());
        newlyClaimed.addAll(flooded);
        state.addScore((flooded.size() + trailLength) * GameRules.POINTS_PER_CLAIMED_CELL);
        ClaimedCoast.updatePatrol(state);

        if (state.claimedPercent() >= GameRules.TARGET_CLAIM_PERCENT) {
            state.setPhase(Phase.LEVEL_UP);
            return TickResult.levelUp(newlyClaimed);
        }
        return TickResult.claimed(newlyClaimed);
    }
}
