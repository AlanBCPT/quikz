package com.example.quikz.session;

public final class AdjustableGameClock implements GameClock {
    private long milliseconds;

    public void set(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public void advance(long delta) {
        milliseconds += delta;
    }

    @Override
    public long milliseconds() {
        return milliseconds;
    }
}
