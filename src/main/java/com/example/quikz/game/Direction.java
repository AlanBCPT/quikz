package com.example.quikz.game;

public record Direction(int columnDelta, int rowDelta) {
    public static final Direction STATIONARY = new Direction(0, 0);
    public static final Direction LEFT = new Direction(-1, 0);
    public static final Direction RIGHT = new Direction(1, 0);
    public static final Direction UP = new Direction(0, -1);
    public static final Direction DOWN = new Direction(0, 1);

    public static Direction of(int columnDelta, int rowDelta) {
        return new Direction(columnDelta, rowDelta);
    }

    public boolean isStationary() {
        return columnDelta == 0 && rowDelta == 0;
    }

    public Cell from(Cell origin) {
        return origin.translated(columnDelta, rowDelta);
    }
}
