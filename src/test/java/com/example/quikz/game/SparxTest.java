package com.example.quikz.game;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SparxTest {
    @Test
    void aStepIsTakenOnlyAfterTheIntervalForThisSpeed() {
        Sparx sparx = new Sparx(0, GameRules.CLOCKWISE, 4);
        sparx.addElapsedTime(249);
        assertThat(sparx.consumeStepIfReady(250)).isFalse();
        sparx.addElapsedTime(1);
        assertThat(sparx.consumeStepIfReady(250)).isTrue();
    }

    @Test
    void advanceWalksThePerimeterAndWraps() {
        Perimeter perimeter = Perimeter.clockwiseBorder(4, 3);
        Sparx sparx = new Sparx(perimeter.size() - 1, GameRules.CLOCKWISE, 3.6);
        sparx.advanceOn(perimeter);
        assertThat(sparx.perimeterIndex()).isZero();
        assertThat(sparx.cellOn(perimeter)).isEqualTo(new Cell(0, 0));
    }

    @Test
    void reverseFlipsTravelDirection() {
        Sparx sparx = new Sparx(4, GameRules.CLOCKWISE, 3.6);
        sparx.reverse();
        assertThat(sparx.stepDirection()).isEqualTo(GameRules.COUNTERCLOCKWISE);
        sparx.placeAt(2);
        assertThat(sparx.perimeterIndex()).isEqualTo(2);
        assertThat(sparx.cellsPerSecond()).isEqualTo(3.6);
    }
}
