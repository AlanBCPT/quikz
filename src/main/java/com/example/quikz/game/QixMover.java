package com.example.quikz.game;

final class QixMover {
    private final RandomSource random;

    QixMover(RandomSource random) {
        this.random = random;
    }

    void advance(GameState state, double elapsedMilliseconds) {
        Qix qix = state.qix();
        Grid grid = state.grid();
        double elapsedSeconds = elapsedMilliseconds / GameRules.MILLISECONDS_PER_SECOND;

        double nextX = qix.xPixels() + qix.velocityX() * elapsedSeconds;
        if (!grid.isEmpty(Qix.cellContaining(nextX, qix.yPixels()))) {
            qix.reverseHorizontalVelocity();
            nextX = qix.xPixels() + qix.velocityX() * elapsedSeconds;
        }

        double nextY = qix.yPixels() + qix.velocityY() * elapsedSeconds;
        if (!grid.isEmpty(Qix.cellContaining(qix.xPixels(), nextY))) {
            qix.reverseVerticalVelocity();
            nextY = qix.yPixels() + qix.velocityY() * elapsedSeconds;
        }

        qix.moveToClamped(nextX, nextY);
        qix.elapseDirectionChange(elapsedMilliseconds, state.level(), random);
    }
}
