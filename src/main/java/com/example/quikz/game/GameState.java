package com.example.quikz.game;

import java.util.ArrayList;
import java.util.List;

public final class GameState {
    private Grid grid = new Grid(GameRules.COLUMNS, GameRules.ROWS);
    private final Player player = new Player();
    private Qix qix = new Qix();
    private List<Sparx> patrollers = new ArrayList<>();
    private Perimeter perimeter = Perimeter.clockwiseBorder(GameRules.COLUMNS, GameRules.ROWS);
    private int score;
    private int lives = GameRules.STARTING_LIVES;
    private int level = GameRules.STARTING_LEVEL;
    private Phase phase = Phase.IDLE;
    private FieldTheme theme;

    public Grid grid() {
        return grid;
    }

    public Player player() {
        return player;
    }

    public Qix qix() {
        return qix;
    }

    public List<Sparx> patrollers() {
        return List.copyOf(patrollers);
    }

    public Perimeter perimeter() {
        return perimeter;
    }

    public int score() {
        return score;
    }

    public int lives() {
        return lives;
    }

    public int level() {
        return level;
    }

    public Phase phase() {
        return phase;
    }

    public boolean isPlaying() {
        return phase == Phase.PLAYING;
    }

    public FieldTheme theme() {
        return theme;
    }

    public boolean hasTheme() {
        return theme != null;
    }

    public double claimedPercent() {
        return grid.claimedPercent();
    }

    public void reroutePatrollersAlong(Perimeter path) {
        if (path.isEmpty()) {
            return;
        }
        for (Sparx sparx : patrollers) {
            sparx.continueOnto(path, perimeter);
        }
        this.perimeter = path;
    }

    public void replaceField(FieldLayout layout) {
        this.grid = layout.grid();
        this.qix = layout.qix();
        this.perimeter = layout.perimeter();
        this.patrollers = new ArrayList<>(layout.patrollers());
        this.theme = layout.theme();
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public void beginFromLevelOne() {
        score = 0;
        lives = GameRules.STARTING_LIVES;
        level = GameRules.STARTING_LEVEL;
    }

    public void incrementLevel() {
        level++;
    }

    public void addScore(int points) {
        score += points;
    }

    public void loseLife() {
        lives--;
    }

    public boolean hasLivesRemaining() {
        return lives > 0;
    }

    public void togglePause() {
        if (phase == Phase.PLAYING) {
            phase = Phase.PAUSED;
        } else if (phase == Phase.PAUSED) {
            phase = Phase.PLAYING;
        }
    }
}
