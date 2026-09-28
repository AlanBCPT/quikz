package com.example.quikz.game;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class QixTest {
    @Test
    void spawnPlacesTheQixAtTheFieldCenterWithLevelSpeed() {
        ControllableRandom random = new ControllableRandom();
        Qix qix = Qix.spawnAtFieldCenter(1, random);
        assertThat(qix.xPixels()).isEqualTo((GameRules.COLUMNS * GameRules.CELL_PIXELS) / 2.0);
        assertThat(qix.yPixels()).isEqualTo((GameRules.ROWS * GameRules.CELL_PIXELS) / 2.0);
        assertThat(qix.velocityX()).isCloseTo(-GameRules.QIX_BASE_SPEED_PIXELS_PER_SECOND, within(0.0001));
        assertThat(qix.velocityY()).isCloseTo(0.0, within(0.0001));
    }

    @Test
    void laterLevelsMoveFaster() {
        assertThat(Qix.speedForLevel(3)).isEqualTo(
                GameRules.QIX_BASE_SPEED_PIXELS_PER_SECOND + 2 * GameRules.QIX_SPEED_INCREASE_PER_LEVEL);
    }

    @Test
    void theStarGrowsTwoPixelsEveryTwoLevelsUpToTen() {
        assertThat(Qix.growthPixels(1)).isZero();
        assertThat(Qix.growthPixels(2)).isZero();
        assertThat(Qix.growthPixels(3)).isEqualTo(2);
        assertThat(Qix.growthPixels(4)).isEqualTo(2);
        assertThat(Qix.growthPixels(11)).isEqualTo(10);
        assertThat(Qix.growthPixels(99)).isEqualTo(10);
        assertThat(Qix.outerRadiusPixels(11)).isEqualTo(GameRules.QIX_BASE_OUTER_RADIUS_PIXELS + 10);
        assertThat(Qix.innerRadiusPixels(11)).isEqualTo(GameRules.QIX_BASE_INNER_RADIUS_PIXELS + 10);
    }

    @Test
    void nearestCellRoundsWhileContainingCellFloors() {
        Qix qix = new Qix();
        qix.placeAt(25.6, 14.4);
        assertThat(qix.nearestCell()).isEqualTo(new Cell(3, 1));
        assertThat(qix.containingCell()).isEqualTo(new Cell(2, 1));
    }

    @Test
    void moveToClampedStaysInsideThePlayableMargin() {
        Qix qix = new Qix();
        qix.moveToClamped(-40, 4000);
        double min = GameRules.CELL_PIXELS * GameRules.QIX_EDGE_MARGIN_CELLS;
        double maxY = (GameRules.ROWS - GameRules.QIX_EDGE_MARGIN_CELLS) * GameRules.CELL_PIXELS;
        assertThat(qix.xPixels()).isEqualTo(min);
        assertThat(qix.yPixels()).isEqualTo(maxY);
    }

    @Test
    void directionChangeRewritesVelocityWhenTheTimerExpires() {
        ControllableRandom random = new ControllableRandom();
        Qix qix = new Qix();
        qix.setVelocity(10, 10);
        qix.setMillisecondsUntilDirectionChange(5);
        qix.elapseDirectionChange(10, 1, random);
        assertThat(qix.velocityX()).isCloseTo(-GameRules.QIX_BASE_SPEED_PIXELS_PER_SECOND, within(0.0001));
        assertThat(qix.velocityY()).isCloseTo(0.0, within(0.0001));
    }

    @Test
    void directionChangeDoesNothingWhileTheTimerRemains() {
        Qix qix = new Qix();
        qix.setVelocity(10, 10);
        qix.setMillisecondsUntilDirectionChange(100);
        qix.elapseDirectionChange(10, 1, new ControllableRandom());
        assertThat(qix.velocityX()).isEqualTo(10);
        assertThat(qix.velocityY()).isEqualTo(10);
    }

    @Test
    void reversingFlipsEachAxisIndependently() {
        Qix qix = new Qix();
        qix.setVelocity(3, -4);
        qix.reverseHorizontalVelocity();
        qix.reverseVerticalVelocity();
        assertThat(qix.velocityX()).isEqualTo(-3);
        assertThat(qix.velocityY()).isEqualTo(4);
    }
}
