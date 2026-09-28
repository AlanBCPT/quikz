package com.example.quikz.game;

final class SparxMover {
    private final RandomSource random;

    SparxMover(RandomSource random) {
        this.random = random;
    }

    void advance(GameState state, double elapsedMilliseconds) {
        Perimeter perimeter = state.perimeter();
        for (Sparx sparx : state.patrollers()) {
            sparx.addElapsedTime(elapsedMilliseconds);
            double millisecondsPerStep = GameRules.MILLISECONDS_PER_SECOND / sparx.cellsPerSecond();
            while (sparx.consumeStepIfReady(millisecondsPerStep)) {
                sparx.advanceOn(perimeter);
                if (random.occurs(GameRules.SPARX_REVERSE_CHANCE_PER_STEP)) {
                    sparx.reverse();
                }
            }
        }
    }
}
