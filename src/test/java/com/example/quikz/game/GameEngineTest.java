package com.example.quikz.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class GameEngineTest {
    private ControllableRandom random;
    private GameEngine engine;
    private GameState state;

    @BeforeEach
    void startAPlayingField() {
        random = new ControllableRandom();
        random.neverOccur();
        engine = new GameEngine(random);
        state = new GameState();
        engine.initializeField(state);
        state.setPhase(Phase.PLAYING);
    }

    @Test
    void initializeFieldClaimsTheBorderAndParksThePlayerOnTheSouthernEdge() {
        assertThat(state.grid().isClaimed(new Cell(0, 0))).isTrue();
        assertThat(state.grid().isEmpty(new Cell(1, 1))).isTrue();
        assertThat(state.player().position()).isEqualTo(Cell.bottomCenter());
        assertThat(state.player().isInvulnerable()).isTrue();
        assertThat(state.patrollers()).hasSize(2);
        assertThat(state.patrollers().get(0).perimeterIndex()).isZero();
        assertThat(state.patrollers().get(1).perimeterIndex()).isEqualTo(state.perimeter().size() / 2);
        assertThat(state.patrollers().get(1).stepDirection()).isEqualTo(GameRules.COUNTERCLOCKWISE);
        assertThat(state.qix().xPixels()).isEqualTo((GameRules.COLUMNS * GameRules.CELL_PIXELS) / 2.0);
        assertThat(state.theme()).isEqualTo(FieldTheme.BLUE);
    }

    @Test
    void aNewFieldUsesADifferentThemeFromTheLast() {
        assertThat(state.theme()).isEqualTo(FieldTheme.BLUE);
        engine.initializeField(state);
        assertThat(state.theme()).isEqualTo(FieldTheme.ORANGE);
    }

    @Test
    void laterLevelsSpawnFasterPatrollers() {
        state.incrementLevel();
        state.incrementLevel();
        engine.initializeField(state);
        assertThat(state.patrollers().get(0).cellsPerSecond()).isEqualTo(
                GameRules.SPARX_BASE_CELLS_PER_SECOND + 2 * GameRules.SPARX_SPEED_INCREASE_PER_LEVEL);
    }

    @Test
    void tickDoesNothingWhileTheGameIsNotPlaying() {
        Cell start = state.player().position();
        state.setPhase(Phase.PAUSED);
        state.player().request(Direction.LEFT);
        TickResult result = engine.tick(state, 200);
        assertThat(result.closedALoop()).isFalse();
        assertThat(state.player().position()).isEqualTo(start);
    }

    @Test
    void thePlayerWalksAlongClaimedBorderWithoutDrawing() {
        state.player().request(Direction.LEFT);
        engine.tick(state, GameRules.MILLISECONDS_PER_PLAYER_STEP);
        assertThat(state.player().position()).isEqualTo(Cell.bottomCenter().translated(-1, 0));
        assertThat(state.player().isDrawing()).isFalse();
        assertThat(state.player().previousColumn()).isEqualTo(Cell.bottomCenter().column());
    }

    @Test
    void thePlayerCannotWalkOffTheField() {
        state.player().request(Direction.DOWN);
        engine.tick(state, GameRules.MILLISECONDS_PER_PLAYER_STEP);
        assertThat(state.player().position()).isEqualTo(Cell.bottomCenter());
    }

    @Test
    void standingStillDoesNotMoveThePlayer() {
        state.player().request(Direction.STATIONARY);
        engine.tick(state, GameRules.MILLISECONDS_PER_PLAYER_STEP);
        assertThat(state.player().position()).isEqualTo(Cell.bottomCenter());
    }

    @Test
    void steppingIntoEmptySpaceStartsATrail() {
        state.player().request(Direction.UP);
        engine.tick(state, GameRules.MILLISECONDS_PER_PLAYER_STEP);
        Cell trailCell = Cell.bottomCenter().translated(0, -1);
        assertThat(state.player().isDrawing()).isTrue();
        assertThat(state.player().position()).isEqualTo(trailCell);
        assertThat(state.grid().isTrail(trailCell)).isTrue();
        assertThat(state.player().trail()).containsExactly(Cell.bottomCenter(), trailCell);
    }

    @Test
    void returningToTheBorderClosesTheTrailAndScoresTheCells() {
        state.player().request(Direction.UP);
        engine.tick(state, GameRules.MILLISECONDS_PER_PLAYER_STEP);
        state.player().request(Direction.DOWN);
        TickResult result = engine.tick(state, GameRules.MILLISECONDS_PER_PLAYER_STEP);
        assertThat(state.player().isDrawing()).isFalse();
        assertThat(state.player().position()).isEqualTo(Cell.bottomCenter());
        assertThat(result.closedALoop()).isTrue();
        assertThat(state.score()).isEqualTo(2 * GameRules.POINTS_PER_CLAIMED_CELL);
        assertThat(state.grid().isClaimed(Cell.bottomCenter().translated(0, -1))).isTrue();
        assertThat(result.event()).isEqualTo(GameEvent.NONE);
        assertThat(state.perimeter().cells()).doesNotContain(Cell.bottomCenter().translated(0, -1));
    }

    @Test
    void sparxStayOnTheOuterRectangleUntilTheThirdField() {
        closeAOneCellDip();
        assertThat(state.perimeter().cells()).isEqualTo(
                Perimeter.clockwiseBorder(GameRules.COLUMNS, GameRules.ROWS).cells());
    }

    @Test
    void sparxFollowTheClaimedCoastlineFromTheThirdField() {
        state.incrementLevel();
        state.incrementLevel();
        engine.initializeField(state);
        state.setPhase(Phase.PLAYING);
        closeAOneCellDip();
        Cell dip = Cell.bottomCenter().translated(0, -1);
        assertThat(state.grid().isClaimed(dip)).isTrue();
        assertThat(state.perimeter().cells()).contains(dip);
        for (Sparx sparx : state.patrollers()) {
            assertThat(sparx.perimeterIndex()).isBetween(0, state.perimeter().size() - 1);
        }
    }

    @Test
    void thePlayerCannotCrossTheirOwnTrail() {
        state.player().request(Direction.UP);
        engine.tick(state, GameRules.MILLISECONDS_PER_PLAYER_STEP);
        state.player().request(Direction.LEFT);
        engine.tick(state, GameRules.MILLISECONDS_PER_PLAYER_STEP);
        Cell blockedFrom = state.player().position();
        state.player().request(Direction.RIGHT);
        engine.tick(state, GameRules.MILLISECONDS_PER_PLAYER_STEP);
        assertThat(state.player().position()).isEqualTo(blockedFrom);
    }

    @Test
    void closingALoopThatReachesTheClaimTargetAdvancesTheLevel() {
        claimEveryInteriorCellExcept(Cell.bottomCenter().translated(0, -1));
        state.player().request(Direction.UP);
        TickResult result = engine.tick(state, GameRules.MILLISECONDS_PER_PLAYER_STEP * 2);
        assertThat(state.phase()).isEqualTo(Phase.LEVEL_UP);
        assertThat(result.event()).isEqualTo(GameEvent.LEVEL_UP);
    }

    @Test
    void invulnerabilityExpiresAcrossTicks() {
        assertThat(state.player().isInvulnerable()).isTrue();
        engine.tick(state, GameRules.FIELD_START_INVULNERABILITY_MS);
        assertThat(state.player().isInvulnerable()).isFalse();
    }

    @Test
    void aQixTouchingTheTrailKillsAVulnerableDrawingPlayer() {
        state.player().becomeInvulnerableFor(0);
        state.player().request(Direction.UP);
        engine.tick(state, GameRules.MILLISECONDS_PER_PLAYER_STEP);
        Cell trailCell = state.player().position();
        state.qix().placeAt(trailCell.column() * GameRules.CELL_PIXELS, trailCell.row() * GameRules.CELL_PIXELS);
        TickResult result = engine.tick(state, 16);
        assertThat(result.event()).isEqualTo(GameEvent.DEATH);
        assertThat(state.lives()).isEqualTo(2);
        assertThat(state.player().position()).isEqualTo(Cell.bottomCenter());
        assertThat(state.player().isDrawing()).isFalse();
        assertThat(state.player().isInvulnerable()).isTrue();
    }

    @Test
    void anInvulnerablePlayerSurvivesAQixTouchOnTheTrail() {
        state.player().request(Direction.UP);
        engine.tick(state, GameRules.MILLISECONDS_PER_PLAYER_STEP);
        Cell trailCell = state.player().position();
        state.qix().placeAt(trailCell.column() * GameRules.CELL_PIXELS, trailCell.row() * GameRules.CELL_PIXELS);
        TickResult result = engine.tick(state, 16);
        assertThat(result.event()).isEqualTo(GameEvent.NONE);
        assertThat(state.lives()).isEqualTo(GameRules.STARTING_LIVES);
        assertThat(state.player().isDrawing()).isTrue();
    }

    @Test
    void aSparxOnThePlayerCellKillsWhenNotDrawing() {
        state.player().becomeInvulnerableFor(0);
        state.player().placeAt(new Cell(0, 0));
        state.patrollers().get(0).placeAt(0);
        TickResult result = engine.tick(state, 16);
        assertThat(result.event()).isEqualTo(GameEvent.DEATH);
        assertThat(state.lives()).isEqualTo(2);
    }

    @Test
    void losingTheLastLifeEndsTheGame() {
        state.player().becomeInvulnerableFor(0);
        state.loseLife();
        state.loseLife();
        state.player().placeAt(new Cell(0, 0));
        state.patrollers().get(0).placeAt(0);
        TickResult result = engine.tick(state, 16);
        assertThat(result.event()).isEqualTo(GameEvent.GAME_OVER);
        assertThat(state.phase()).isEqualTo(Phase.OVER);
        assertThat(state.lives()).isZero();
    }

    @Test
    void theQixBouncesWhenTheNextCellIsClaimed() {
        state.qix().placeAt(15, 220);
        state.qix().setVelocity(-400, 0);
        state.qix().setMillisecondsUntilDirectionChange(10_000);
        engine.tick(state, 50);
        assertThat(state.qix().velocityX()).isPositive();
    }

    @Test
    void theQixBouncesVerticallyWhenTheNextCellIsClaimed() {
        state.qix().placeAt(320, 15);
        state.qix().setVelocity(0, -400);
        state.qix().setMillisecondsUntilDirectionChange(10_000);
        engine.tick(state, 50);
        assertThat(state.qix().velocityY()).isPositive();
    }

    @Test
    void theQixChangesHeadingWhenItsTimerExpires() {
        state.qix().setVelocity(12, 12);
        state.qix().setMillisecondsUntilDirectionChange(1);
        engine.tick(state, 16);
        assertThat(state.qix().velocityX()).isCloseTo(-GameRules.QIX_BASE_SPEED_PIXELS_PER_SECOND, within(0.0001));
        assertThat(state.qix().velocityY()).isCloseTo(0.0, within(0.0001));
    }

    @Test
    void sparxAdvanceAlongThePerimeter() {
        int start = state.patrollers().get(0).perimeterIndex();
        engine.tick(state, GameRules.MILLISECONDS_PER_SECOND / state.patrollers().get(0).cellsPerSecond());
        assertThat(state.patrollers().get(0).perimeterIndex()).isEqualTo(start + 1);
    }

    @Test
    void sparxReverseWhenChanceFires() {
        random.alwaysOccur();
        int direction = state.patrollers().get(0).stepDirection();
        engine.tick(state, GameRules.MILLISECONDS_PER_SECOND / state.patrollers().get(0).cellsPerSecond());
        assertThat(state.patrollers().get(0).stepDirection()).isEqualTo(-direction);
    }

    @Test
    void aClosedLoopStopsFurtherMovementAndEnemyUpdatesThatTick() {
        Cell returnCell = Cell.bottomCenter().translated(0, -2);
        state.grid().claim(returnCell);
        state.player().request(Direction.UP);
        state.qix().setMillisecondsUntilDirectionChange(10_000);
        double qixX = state.qix().xPixels();
        TickResult result = engine.tick(state, GameRules.MILLISECONDS_PER_PLAYER_STEP * 2);
        assertThat(result.closedALoop()).isTrue();
        assertThat(state.player().position()).isEqualTo(returnCell);
        assertThat(state.qix().xPixels()).isEqualTo(qixX);
    }

    @Test
    void idleNewStateHasNoScoreAndThreeLives() {
        GameState fresh = new GameState();
        assertThat(fresh.phase()).isEqualTo(Phase.IDLE);
        assertThat(fresh.score()).isZero();
        assertThat(fresh.lives()).isEqualTo(GameRules.STARTING_LIVES);
        assertThat(fresh.level()).isEqualTo(1);
        assertThat(fresh.hasLivesRemaining()).isTrue();
    }

    @Test
    void beginFromLevelOneResetsTheRun() {
        state.addScore(50);
        state.loseLife();
        state.incrementLevel();
        state.beginFromLevelOne();
        assertThat(state.score()).isZero();
        assertThat(state.lives()).isEqualTo(GameRules.STARTING_LIVES);
        assertThat(state.level()).isEqualTo(GameRules.STARTING_LEVEL);
    }

    @Test
    void togglePauseOnlySwitchesPlayingAndPaused() {
        state.togglePause();
        assertThat(state.phase()).isEqualTo(Phase.PAUSED);
        state.togglePause();
        assertThat(state.phase()).isEqualTo(Phase.PLAYING);
        state.setPhase(Phase.OVER);
        state.togglePause();
        assertThat(state.phase()).isEqualTo(Phase.OVER);
        state.setPhase(Phase.LEVEL_UP);
        state.togglePause();
        assertThat(state.phase()).isEqualTo(Phase.LEVEL_UP);
    }

    private void closeAOneCellDip() {
        state.player().request(Direction.UP);
        engine.tick(state, GameRules.MILLISECONDS_PER_PLAYER_STEP);
        state.player().request(Direction.DOWN);
        engine.tick(state, GameRules.MILLISECONDS_PER_PLAYER_STEP);
    }

    private void claimEveryInteriorCellExcept(Cell keepEmpty) {
        Grid grid = state.grid();
        for (int row = 1; row < grid.rows() - 1; row++) {
            for (int column = 1; column < grid.columns() - 1; column++) {
                Cell cell = new Cell(column, row);
                if (!cell.equals(keepEmpty)) {
                    grid.claim(cell);
                }
            }
        }
    }
}
