package com.example.quikz.game;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GridTest {
    @Test
    void aNewGridIsEntirelyEmpty() {
        Grid grid = new Grid(3, 2);
        assertThat(grid.size()).isEqualTo(6);
        assertThat(grid.kindAt(0, 0)).isEqualTo(CellKind.EMPTY);
        assertThat(grid.claimedPercent()).isZero();
    }

    @Test
    void claimedBorderMarksEveryEdgeCell() {
        Grid grid = Grid.withClaimedBorder(4, 3);
        assertThat(grid.isClaimed(new Cell(0, 0))).isTrue();
        assertThat(grid.isClaimed(new Cell(3, 2))).isTrue();
        assertThat(grid.isEmpty(new Cell(1, 1))).isTrue();
        assertThat(grid.claimedIndices()).containsExactly(0, 1, 2, 3, 4, 7, 8, 9, 10, 11);
    }

    @Test
    void containsRejectsCellsOutsideTheField() {
        Grid grid = new Grid(4, 3);
        assertThat(grid.contains(new Cell(-1, 0))).isFalse();
        assertThat(grid.contains(new Cell(0, 3))).isFalse();
        assertThat(grid.contains(new Cell(3, 2))).isTrue();
    }

    @Test
    void trailMarksCanBeClearedBackToEmpty() {
        Grid grid = new Grid(2, 2);
        Cell cell = new Cell(1, 1);
        grid.markTrail(cell);
        assertThat(grid.isTrail(cell)).isTrue();
        grid.clearTrailIfPresent(cell);
        assertThat(grid.isEmpty(cell)).isTrue();
    }

    @Test
    void clearingANonTrailCellLeavesItAlone() {
        Grid grid = Grid.withClaimedBorder(3, 3);
        Cell border = new Cell(0, 0);
        grid.clearTrailIfPresent(border);
        assertThat(grid.isClaimed(border)).isTrue();
    }

    @Test
    void indexAndCellAtAreInverses() {
        Grid grid = new Grid(5, 4);
        Cell cell = new Cell(3, 2);
        assertThat(grid.cellAt(grid.index(cell))).isEqualTo(cell);
        assertThat(grid.kindAt(grid.cellAt(grid.index(cell)))).isEqualTo(CellKind.EMPTY);
    }

    @Test
    void claimedPercentCountsClaimedCellsAgainstTheWholeField() {
        Grid grid = new Grid(2, 2);
        grid.claim(new Cell(0, 0));
        assertThat(grid.claimedPercent()).isEqualTo(25.0);
    }
}
