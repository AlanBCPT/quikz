package com.example.quikz.game;

public enum Phase {
    IDLE("idle"),
    PLAYING("playing"),
    PAUSED("paused"),
    LEVEL_UP("levelup"),
    OVER("over");

    private final String wireName;

    Phase(String wireName) {
        this.wireName = wireName;
    }

    public String wireName() {
        return wireName;
    }
}
