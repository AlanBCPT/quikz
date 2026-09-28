package com.example.quikz.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FloodFillTest {
    @Test
    void theRegionThatDoesNotHoldTheQixIsClaimed() {
        Grid grid = Grid.withClaimedBorder(5, 5);
        for (int row = 1; row <= 3; row++) {
            grid.markTrail(new Cell(2, row));
        }
        List<Integer> claimed = FloodFill.claimEmptyRegionsNotContaining(grid, new Cell(1, 2));
        assertThat(grid.isClaimed(new Cell(3, 2))).isTrue();
        assertThat(grid.isEmpty(new Cell(1, 2))).isTrue();
        assertThat(claimed).contains(grid.index(3, 1), grid.index(3, 2), grid.index(3, 3));
        assertThat(claimed).doesNotContain(grid.index(1, 2));
    }

    @Test
    void aSingleEmptyRegionIsLeftAloneWhenItContainsTheQix() {
        Grid grid = Grid.withClaimedBorder(5, 5);
        List<Integer> claimed = FloodFill.claimEmptyRegionsNotContaining(grid, new Cell(2, 2));
        assertThat(claimed).isEmpty();
        assertThat(grid.isEmpty(new Cell(2, 2))).isTrue();
    }

    @Test
    void whenTheQixSitsOnClaimedGroundTheNearestEmptyRegionIsSpared() {
        Grid grid = Grid.withClaimedBorder(5, 5);
        grid.claim(new Cell(2, 1));
        grid.claim(new Cell(2, 2));
        grid.claim(new Cell(2, 3));
        List<Integer> claimed = FloodFill.claimEmptyRegionsNotContaining(grid, new Cell(0, 0));
        assertThat(grid.isEmpty(new Cell(1, 2))).isTrue();
        assertThat(grid.isClaimed(new Cell(3, 2))).isTrue();
        assertThat(claimed).isNotEmpty();
    }

    @Test
    void qixCoordinatesAreClampedOntoTheField() {
        Grid grid = Grid.withClaimedBorder(5, 5);
        List<Integer> claimed = FloodFill.claimEmptyRegionsNotContaining(grid, new Cell(90, -4));
        assertThat(claimed).isEmpty();
    }
}
