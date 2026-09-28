package com.example.quikz.session;

public final class SystemGameClock implements GameClock {
    @Override
    public long milliseconds() {
        return System.currentTimeMillis();
    }
}
