package com.example.quikz.config;

import com.example.quikz.game.RandomSource;
import com.example.quikz.session.GameClock;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ScheduledExecutorService;

import static org.assertj.core.api.Assertions.assertThat;

class GameConfigurationTest {
    @Test
    void providesRandomClockAndScheduler() {
        GameConfiguration configuration = new GameConfiguration();
        RandomSource random = configuration.randomSource();
        GameClock clock = configuration.gameClock();
        ScheduledExecutorService scheduler = configuration.gameLoopScheduler();
        try {
            assertThat(random.inRange(0, 1)).isBetween(0.0, 1.0);
            assertThat(random.occurs(0)).isFalse();
            assertThat(clock.milliseconds()).isPositive();
            assertThat(scheduler).isNotNull();
        } finally {
            scheduler.shutdownNow();
        }
    }
}
