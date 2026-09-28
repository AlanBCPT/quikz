package com.example.quikz.game;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DirectionTest {
    @Test
    void stationaryMeansNoDelta() {
        assertThat(Direction.STATIONARY.isStationary()).isTrue();
        assertThat(Direction.of(1, 0).isStationary()).isFalse();
    }

    @Test
    void fromMovesACellByThisDirection() {
        assertThat(Direction.LEFT.from(new Cell(5, 2))).isEqualTo(new Cell(4, 2));
        assertThat(Direction.UP.from(new Cell(5, 2))).isEqualTo(new Cell(5, 1));
    }

    @Test
    void ofPreservesArbitraryDeltasFromTheClient() {
        assertThat(Direction.of(2, -3)).isEqualTo(new Direction(2, -3));
    }
}
