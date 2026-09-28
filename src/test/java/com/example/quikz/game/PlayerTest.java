package com.example.quikz.game;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerTest {
    @Test
    void aStepIsNotConsumedUntilAFullCellIntervalHasElapsed() {
        Player player = new Player();
        player.addElapsedTime(GameRules.MILLISECONDS_PER_PLAYER_STEP - 1);
        assertThat(player.consumeStepIfReady()).isFalse();
        player.addElapsedTime(1);
        assertThat(player.consumeStepIfReady()).isTrue();
        assertThat(player.consumeStepIfReady()).isFalse();
    }

    @Test
    void invulnerabilityDecaysToZeroAndNotBelow() {
        Player player = new Player();
        player.becomeInvulnerableFor(100);
        assertThat(player.isInvulnerable()).isTrue();
        player.reduceInvulnerability(40);
        assertThat(player.isInvulnerable()).isTrue();
        player.reduceInvulnerability(80);
        assertThat(player.isInvulnerable()).isFalse();
        player.reduceInvulnerability(10);
        assertThat(player.isInvulnerable()).isFalse();
    }

    @Test
    void placeAtClearsDrawingAndTheTrailClock() {
        Player player = new Player();
        Grid grid = new Grid(4, 4);
        player.placeAt(new Cell(1, 3));
        player.beginDrawingToward(new Cell(1, 2), grid);
        player.addElapsedTime(50);
        player.placeAt(new Cell(2, 3));
        assertThat(player.isDrawing()).isFalse();
        assertThat(player.trail()).isEmpty();
        assertThat(player.position()).isEqualTo(new Cell(2, 3));
        assertThat(player.previousColumn()).isEqualTo(2);
        assertThat(player.consumeStepIfReady()).isFalse();
    }

    @Test
    void eraseTrailFromRestoresTrailCellsToEmpty() {
        Player player = new Player();
        Grid grid = Grid.withClaimedBorder(4, 4);
        player.placeAt(new Cell(1, 3));
        player.beginDrawingToward(new Cell(1, 2), grid);
        player.eraseTrailFrom(grid);
        assertThat(grid.isEmpty(new Cell(1, 2))).isTrue();
        assertThat(player.trail()).isEmpty();
    }

    @Test
    void finishDrawingStopsTheLineWithoutErasingAlreadyClaimedCells() {
        Player player = new Player();
        Grid grid = Grid.withClaimedBorder(4, 4);
        player.placeAt(new Cell(1, 3));
        player.beginDrawingToward(new Cell(1, 2), grid);
        grid.claim(new Cell(1, 2));
        player.finishDrawing();
        assertThat(player.isDrawing()).isFalse();
        assertThat(grid.isClaimed(new Cell(1, 2))).isTrue();
    }

    @Test
    void requestRemembersTheLastClientDirection() {
        Player player = new Player();
        player.request(Direction.RIGHT);
        assertThat(player.requestedDirection()).isEqualTo(Direction.RIGHT);
    }

    @Test
    void zeroInvulnerabilityIsNotProtection() {
        Player player = new Player();
        player.becomeInvulnerableFor(800);
        player.becomeInvulnerableFor(0);
        assertThat(player.isInvulnerable()).isFalse();
    }

    @Test
    void respawnOnSouthernBorderClearsTheTrailAndRestoresProtection() {
        Player player = new Player();
        Grid grid = Grid.withClaimedBorder(GameRules.COLUMNS, GameRules.ROWS);
        player.placeAt(new Cell(1, GameRules.ROWS - 1));
        player.beginDrawingToward(new Cell(1, GameRules.ROWS - 2), grid);
        player.respawnOnSouthernBorder(grid, GameRules.RESPAWN_INVULNERABILITY_MS);
        assertThat(player.position()).isEqualTo(Cell.bottomCenter());
        assertThat(player.isDrawing()).isFalse();
        assertThat(player.trail()).isEmpty();
        assertThat(grid.isEmpty(new Cell(1, GameRules.ROWS - 2))).isTrue();
        assertThat(player.isInvulnerable()).isTrue();
    }
}
