package com.example.quikz.game;

public enum GameEvent {
    NONE(null),
    DEATH("death"),
    LEVEL_UP("levelup"),
    GAME_OVER("gameover");

    private final String wireName;

    GameEvent(String wireName) {
        this.wireName = wireName;
    }

    public String wireName() {
        return wireName;
    }
}
