package com.example.quikz.game;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClaimedCoastTest {
    @Test
    void aFreshFieldCoastMatchesTheOuterRectangle() {
        Grid grid = Grid.withClaimedBorder(4, 3);
        Perimeter coast = ClaimedCoast.trace(grid);
        assertThat(coast.cells()).isEqualTo(Perimeter.clockwiseBorder(4, 3).cells());
    }

    @Test
    void theCoastFollowsAOneCellBiteOffTheSouthernWall() {
        Grid grid = Grid.withClaimedBorder(5, 5);
        grid.claim(new Cell(2, 3));
        Perimeter coast = ClaimedCoast.trace(grid);
        assertThat(coast.cells()).contains(new Cell(2, 3));
        assertThat(coast.indexNearestTo(new Cell(2, 3))).isGreaterThan(0);
    }

    @Test
    void anAllClaimedGridHasNoCoast() {
        Grid grid = Grid.withClaimedBorder(3, 3);
        grid.claim(new Cell(1, 1));
        assertThat(ClaimedCoast.trace(grid).isEmpty()).isTrue();
    }
}
