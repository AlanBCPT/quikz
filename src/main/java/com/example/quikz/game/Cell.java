package com.example.quikz.game;

public record Cell(int column, int row) {
    public static Cell bottomCenter() {
        return new Cell(GameRules.COLUMNS / 2, GameRules.ROWS - 1);
    }

    public Cell translated(int columnDelta, int rowDelta) {
        return new Cell(column + columnDelta, row + rowDelta);
    }

    public boolean isWithinChebyshevDistance(Cell other, int range) {
        return Math.abs(column - other.column) <= range && Math.abs(row - other.row) <= range;
    }

    public int manhattanDistanceTo(Cell other) {
        return Math.abs(column - other.column) + Math.abs(row - other.row);
    }

    public int[] asPair() {
        return new int[]{column, row};
    }
}
