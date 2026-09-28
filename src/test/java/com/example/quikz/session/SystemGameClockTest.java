package com.example.quikz.session;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SystemGameClockTest {
    @Test
    void reportsWallClockMilliseconds() {
        long before = System.currentTimeMillis();
        long reported = new SystemGameClock().milliseconds();
        long after = System.currentTimeMillis();
        assertThat(reported).isBetween(before, after);
    }
}
