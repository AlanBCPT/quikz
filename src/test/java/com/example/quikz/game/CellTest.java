package com.example.quikz.game;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CellTest {
    @Test
    void bottomCenterSitsOnTheMiddleOfTheSouthernBorder() {
        assertThat(Cell.bottomCenter()).isEqualTo(new Cell(GameRules.COLUMNS / 2, GameRules.ROWS - 1));
    }

    @Test
    void translatedMovesByTheGivenDeltas() {
        assertThat(new Cell(4, 5).translated(-1, 2)).isEqualTo(new Cell(3, 7));
    }

    @Test
    void chebyshevDistanceIncludesDiagonalsAsRangeOne() {
        Cell origin = new Cell(10, 10);
        assertThat(origin.isWithinChebyshevDistance(new Cell(11, 11), 1)).isTrue();
        assertThat(origin.isWithinChebyshevDistance(new Cell(12, 10), 1)).isFalse();
    }

    @Test
    void manhattanDistanceSumsAbsoluteColumnAndRowGaps() {
        assertThat(new Cell(4, 7).manhattanDistanceTo(new Cell(1, 10))).isEqualTo(6);
    }

    @Test
    void asPairKeepsColumnThenRow() {
        assertThat(new Cell(3, 8).asPair()).containsExactly(3, 8);
    }
}
