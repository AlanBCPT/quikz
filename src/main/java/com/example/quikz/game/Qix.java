package com.example.quikz.game;

public final class Qix {
    private double xPixels;
    private double yPixels;
    private double velocityX;
    private double velocityY;
    private double millisecondsUntilDirectionChange;

    public static Qix spawnAtFieldCenter(int level, RandomSource random) {
        double speed = speedForLevel(level);
        double angle = random.inRange(0, GameRules.FULL_TURN_RADIANS);
        Qix qix = new Qix();
        qix.xPixels = (GameRules.COLUMNS * GameRules.CELL_PIXELS) / 2.0;
        qix.yPixels = (GameRules.ROWS * GameRules.CELL_PIXELS) / 2.0;
        qix.velocityX = Math.cos(angle) * speed;
        qix.velocityY = Math.sin(angle) * speed;
        qix.millisecondsUntilDirectionChange = random.inRange(
                GameRules.QIX_SPAWN_DIRECTION_CHANGE_MIN_MS,
                GameRules.QIX_SPAWN_DIRECTION_CHANGE_MAX_MS);
        return qix;
    }

    public static double speedForLevel(int level) {
        return GameRules.QIX_BASE_SPEED_PIXELS_PER_SECOND
                + (level - GameRules.STARTING_LEVEL) * GameRules.QIX_SPEED_INCREASE_PER_LEVEL;
    }

    public static int growthPixels(int level) {
        int steps = Math.max(0, level - GameRules.STARTING_LEVEL) / GameRules.QIX_GROWTH_EVERY_LEVELS;
        int growth = steps * GameRules.QIX_GROWTH_PIXELS_PER_STEP;
        return Math.min(growth, GameRules.QIX_MAX_GROWTH_PIXELS);
    }

    public static int outerRadiusPixels(int level) {
        return GameRules.QIX_BASE_OUTER_RADIUS_PIXELS + growthPixels(level);
    }

    public static int innerRadiusPixels(int level) {
        return GameRules.QIX_BASE_INNER_RADIUS_PIXELS + growthPixels(level);
    }

    public double xPixels() {
        return xPixels;
    }

    public double yPixels() {
        return yPixels;
    }

    public double velocityX() {
        return velocityX;
    }

    public double velocityY() {
        return velocityY;
    }

    public void placeAt(double xPixels, double yPixels) {
        this.xPixels = xPixels;
        this.yPixels = yPixels;
    }

    public void setVelocity(double velocityX, double velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public void setMillisecondsUntilDirectionChange(double milliseconds) {
        millisecondsUntilDirectionChange = milliseconds;
    }

    public void reverseHorizontalVelocity() {
        velocityX = -velocityX;
    }

    public void reverseVerticalVelocity() {
        velocityY = -velocityY;
    }

    public void moveToClamped(double xPixels, double yPixels) {
        double min = GameRules.CELL_PIXELS * GameRules.QIX_EDGE_MARGIN_CELLS;
        double maxX = (GameRules.COLUMNS - GameRules.QIX_EDGE_MARGIN_CELLS) * GameRules.CELL_PIXELS;
        double maxY = (GameRules.ROWS - GameRules.QIX_EDGE_MARGIN_CELLS) * GameRules.CELL_PIXELS;
        this.xPixels = Clamp.between(xPixels, min, maxX);
        this.yPixels = Clamp.between(yPixels, min, maxY);
    }

    public Cell containingCell() {
        return cellContaining(xPixels, yPixels);
    }

    public Cell nearestCell() {
        return clampedCell(
                (int) Math.round(xPixels / GameRules.CELL_PIXELS),
                (int) Math.round(yPixels / GameRules.CELL_PIXELS));
    }

    public static Cell cellContaining(double xPixels, double yPixels) {
        return clampedCell(
                (int) Math.floor(xPixels / GameRules.CELL_PIXELS),
                (int) Math.floor(yPixels / GameRules.CELL_PIXELS));
    }

    private static Cell clampedCell(int column, int row) {
        return new Cell(
                Clamp.between(column, 0, GameRules.COLUMNS - 1),
                Clamp.between(row, 0, GameRules.ROWS - 1));
    }

    public void elapseDirectionChange(double milliseconds, int level, RandomSource random) {
        millisecondsUntilDirectionChange -= milliseconds;
        if (millisecondsUntilDirectionChange > 0) {
            return;
        }
        double speed = speedForLevel(level);
        double angle = random.inRange(0, GameRules.FULL_TURN_RADIANS);
        double jitter = random.inRange(GameRules.QIX_SPEED_JITTER_MIN, GameRules.QIX_SPEED_JITTER_MAX);
        velocityX = Math.cos(angle) * speed * jitter;
        velocityY = Math.sin(angle) * speed * jitter;
        millisecondsUntilDirectionChange = random.inRange(
                GameRules.QIX_DIRECTION_CHANGE_MIN_MS,
                GameRules.QIX_DIRECTION_CHANGE_MAX_MS);
    }
}
