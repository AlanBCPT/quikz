package com.example.quikz.game;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClampTest {
    @Test
    void clampPullsIntegersUpToTheMinimum() {
        assertThat(Clamp.between(-4, 0, 10)).isZero();
    }

    @Test
    void clampPullsIntegersDownToTheMaximum() {
        assertThat(Clamp.between(40, 0, 10)).isEqualTo(10);
    }

    @Test
    void clampLeavesIntegersInsideTheRangeAlone() {
        assertThat(Clamp.between(7, 0, 10)).isEqualTo(7);
    }

    @Test
    void clampPullsDoublesIntoRange() {
        assertThat(Clamp.between(-1.5, 0.0, 2.0)).isZero();
        assertThat(Clamp.between(9.5, 0.0, 2.0)).isEqualTo(2.0);
        assertThat(Clamp.between(1.25, 0.0, 2.0)).isEqualTo(1.25);
    }
}
