package com.example.quikz.game;

public final class GameRules {
    public static final int COLUMNS = 64;
    public static final int ROWS = 44;
    public static final int CELL_PIXELS = 10;
    public static final double TARGET_CLAIM_PERCENT = 75.0;

    public static final int STARTING_LIVES = 3;
    public static final int STARTING_LEVEL = 1;
    public static final int POINTS_PER_CLAIMED_CELL = 4;

    public static final double MILLISECONDS_PER_PLAYER_STEP = 78;
    public static final double FIELD_START_INVULNERABILITY_MS = 800;
    public static final double RESPAWN_INVULNERABILITY_MS = 1500;

    public static final double QIX_BASE_SPEED_PIXELS_PER_SECOND = 75;
    public static final double QIX_SPEED_INCREASE_PER_LEVEL = 14;
    public static final double QIX_SPAWN_DIRECTION_CHANGE_MIN_MS = 700;
    public static final double QIX_SPAWN_DIRECTION_CHANGE_MAX_MS = 1600;
    public static final double QIX_DIRECTION_CHANGE_MIN_MS = 600;
    public static final double QIX_DIRECTION_CHANGE_MAX_MS = 1500;
    public static final double QIX_SPEED_JITTER_MIN = 0.7;
    public static final double QIX_SPEED_JITTER_MAX = 1.3;
    public static final double QIX_EDGE_MARGIN_CELLS = 1.2;
    public static final int QIX_TRAIL_HIT_RANGE = 1;
    public static final int QIX_BASE_OUTER_RADIUS_PIXELS = 13;
    public static final int QIX_BASE_INNER_RADIUS_PIXELS = 5;
    public static final int QIX_GROWTH_EVERY_LEVELS = 2;
    public static final int QIX_GROWTH_PIXELS_PER_STEP = 2;
    public static final int QIX_MAX_GROWTH_PIXELS = 10;

    public static final double SPARX_BASE_CELLS_PER_SECOND = 3.6;
    public static final double SPARX_SPEED_INCREASE_PER_LEVEL = 0.4;
    public static final double SPARX_REVERSE_CHANCE_PER_STEP = 0.004;

    public static final long TICK_PERIOD_MS = 33;
    public static final double MAX_TICK_DELTA_MS = 50;
    public static final double MILLISECONDS_PER_SECOND = 1000.0;
    public static final int GAME_LOOP_THREAD_COUNT = 4;
    public static final int CLAIMED_PERCENT_DISPLAY_SCALE = 10;
    public static final double FULL_TURN_RADIANS = Math.PI * 2;
    public static final int FIRST_PATROLLER_INDEX = 0;
    public static final int CLOCKWISE = 1;
    public static final int COUNTERCLOCKWISE = -1;
    public static final int COASTLINE_PATROL_FROM_LEVEL = 3;

    private GameRules() {}

    public static double claimedPercentToOneDecimal(double percent) {
        return Math.floor(percent * CLAIMED_PERCENT_DISPLAY_SCALE) / CLAIMED_PERCENT_DISPLAY_SCALE;
    }
}
