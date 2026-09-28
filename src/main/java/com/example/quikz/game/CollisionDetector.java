package com.example.quikz.game;

final class CollisionDetector {
    TickResult detect(GameState state) {
        Player player = state.player();
        if (player.isInvulnerable()) {
            return TickResult.nothingHappened();
        }
        if (player.isDrawing() && qixTouchesTrail(state)) {
            return killPlayer(state);
        }
        if (!player.isDrawing() && sparxTouchesPlayer(state)) {
            return killPlayer(state);
        }
        return TickResult.nothingHappened();
    }

    private boolean qixTouchesTrail(GameState state) {
        Cell qixCell = state.qix().nearestCell();
        for (Cell trailCell : state.player().trail()) {
            if (trailCell.isWithinChebyshevDistance(qixCell, GameRules.QIX_TRAIL_HIT_RANGE)) {
                return true;
            }
        }
        return false;
    }

    private boolean sparxTouchesPlayer(GameState state) {
        Cell playerCell = state.player().position();
        for (Sparx sparx : state.patrollers()) {
            if (sparx.cellOn(state.perimeter()).equals(playerCell)) {
                return true;
            }
        }
        return false;
    }

    private TickResult killPlayer(GameState state) {
        state.loseLife();
        if (!state.hasLivesRemaining()) {
            state.setPhase(Phase.OVER);
            return TickResult.gameOver();
        }
        state.player().respawnOnSouthernBorder(state.grid(), GameRules.RESPAWN_INVULNERABILITY_MS);
        return TickResult.death();
    }
}
