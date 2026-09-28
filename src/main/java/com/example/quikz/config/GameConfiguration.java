package com.example.quikz.config;

import com.example.quikz.game.GameRules;
import com.example.quikz.game.RandomSource;
import com.example.quikz.game.ThreadLocalRandomSource;
import com.example.quikz.session.GameClock;
import com.example.quikz.session.SystemGameClock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class GameConfiguration {
    @Bean(destroyMethod = "shutdownNow")
    public ScheduledExecutorService gameLoopScheduler() {
        return Executors.newScheduledThreadPool(GameRules.GAME_LOOP_THREAD_COUNT);
    }

    @Bean
    public RandomSource randomSource() {
        return new ThreadLocalRandomSource();
    }

    @Bean
    public GameClock gameClock() {
        return new SystemGameClock();
    }
}
