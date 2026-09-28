package com.example.quikz.game;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

class PerimeterTest {
    @Test
    void clockwiseBorderStartsAtTheNorthwestCornerAndSkipsDuplicateCorners() {
        Perimeter perimeter = Perimeter.clockwiseBorder(4, 3);
        assertThat(perimeter.at(0)).isEqualTo(new Cell(0, 0));
        assertThat(perimeter.at(1)).isEqualTo(new Cell(1, 0));
        assertThat(perimeter.at(3)).isEqualTo(new Cell(3, 0));
        assertThat(perimeter.at(4)).isEqualTo(new Cell(3, 1));
        assertThat(perimeter.size()).isEqualTo(10);
        assertThat(new HashSet<>(perimeter.cells())).hasSize(perimeter.size());
    }

    @Test
    void wrappedIndexWalksOffEitherEndOfTheLoop() {
        Perimeter perimeter = Perimeter.clockwiseBorder(4, 3);
        assertThat(perimeter.wrappedIndex(-1)).isEqualTo(perimeter.size() - 1);
        assertThat(perimeter.wrappedIndex(perimeter.size())).isZero();
    }

    @Test
    void theWesternBottomCellSitsWhereTheClockwiseWalkReturnsNorth() {
        Perimeter perimeter = Perimeter.clockwiseBorder(4, 3);
        assertThat(perimeter.at(8)).isEqualTo(new Cell(0, 2));
    }

    @Test
    void indexNearestToPrefersAnExactMatch() {
        Perimeter perimeter = Perimeter.clockwiseBorder(4, 3);
        assertThat(perimeter.indexNearestTo(new Cell(3, 1))).isEqualTo(4);
        assertThat(perimeter.indexNearestTo(new Cell(2, 1))).isBetween(0, perimeter.size() - 1);
    }

    @Test
    void continueOntoKeepsACellThatStillExists() {
        Perimeter previous = Perimeter.clockwiseBorder(5, 5);
        Perimeter next = Perimeter.clockwiseBorder(5, 5);
        int index = previous.indexOf(new Cell(2, 4));
        assertThat(previous.continueOnto(next, index, GameRules.CLOCKWISE)).isEqualTo(index);
    }

    @Test
    void continueOntoWalksForwardInsteadOfJumpingToTheNearestNewWall() {
        Grid grid = Grid.withClaimedBorder(5, 5);
        grid.claim(new Cell(1, 3));
        grid.claim(new Cell(2, 3));
        grid.claim(new Cell(3, 3));
        Perimeter previous = Perimeter.clockwiseBorder(5, 5);
        Perimeter next = ClaimedCoast.trace(grid);
        int from = previous.indexOf(new Cell(2, 4));
        Cell after = next.at(previous.continueOnto(next, from, GameRules.CLOCKWISE));
        assertThat(after).isNotEqualTo(new Cell(2, 3));
        assertThat(next.cells()).contains(after);
    }
}
