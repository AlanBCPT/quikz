package com.example.quikz.game;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ThreadLocalRandomSourceTest {
    @Test
    void inRangeStaysInsideTheRequestedHalfOpenInterval() {
        ThreadLocalRandomSource random = new ThreadLocalRandomSource();
        for (int i = 0; i < 40; i++) {
            double value = random.inRange(2, 5);
            assertThat(value).isGreaterThanOrEqualTo(2).isLessThan(5);
        }
    }

    @Test
    void aZeroProbabilityNeverOccurs() {
        ThreadLocalRandomSource random = new ThreadLocalRandomSource();
        assertThat(random.occurs(0)).isFalse();
    }

    @Test
    void aCertainProbabilityAlwaysOccurs() {
        ThreadLocalRandomSource random = new ThreadLocalRandomSource();
        assertThat(random.occurs(1)).isTrue();
    }

    @Test
    void indexBelowStaysInsideZeroAndTheExclusiveMaximum() {
        ThreadLocalRandomSource random = new ThreadLocalRandomSource();
        for (int i = 0; i < 40; i++) {
            assertThat(random.indexBelow(3)).isBetween(0, 2);
        }
    }
}
