package com.example.quikz.game;

import java.util.ArrayList;
import java.util.List;

public final class Grid {
    private final int columns;
    private final int rows;
    private final CellKind[] cells;

    public Grid(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;
        this.cells = new CellKind[columns * rows];
        for (int i = 0; i < cells.length; i++) {
            cells[i] = CellKind.EMPTY;
        }
    }

    public static Grid withClaimedBorder(int columns, int rows) {
        Grid grid = new Grid(columns, rows);
        grid.claimOuterBorder();
        return grid;
    }

    public int columns() {
        return columns;
    }

    public int rows() {
        return rows;
    }

    public int size() {
        return cells.length;
    }

    public boolean contains(Cell cell) {
        return contains(cell.column(), cell.row());
    }

    public boolean contains(int column, int row) {
        return column >= 0 && column < columns && row >= 0 && row < rows;
    }

    public CellKind kindAt(Cell cell) {
        return kindAt(cell.column(), cell.row());
    }

    public CellKind kindAt(int column, int row) {
        return cells[index(column, row)];
    }

    private void set(Cell cell, CellKind kind) {
        set(cell.column(), cell.row(), kind);
    }

    private void set(int column, int row, CellKind kind) {
        cells[index(column, row)] = kind;
    }

    public void claim(Cell cell) {
        set(cell, CellKind.CLAIMED);
    }

    public void markTrail(Cell cell) {
        set(cell, CellKind.TRAIL);
    }

    public void clearTrailIfPresent(Cell cell) {
        if (kindAt(cell) == CellKind.TRAIL) {
            set(cell, CellKind.EMPTY);
        }
    }

    public boolean isEmpty(Cell cell) {
        return kindAt(cell) == CellKind.EMPTY;
    }

    public boolean isClaimed(Cell cell) {
        return kindAt(cell) == CellKind.CLAIMED;
    }

    public boolean isTrail(Cell cell) {
        return kindAt(cell) == CellKind.TRAIL;
    }

    public int index(Cell cell) {
        return index(cell.column(), cell.row());
    }

    public int index(int column, int row) {
        return row * columns + column;
    }

    public Cell cellAt(int index) {
        return new Cell(index % columns, index / columns);
    }

    public double claimedPercent() {
        int claimed = 0;
        for (CellKind kind : cells) {
            if (kind == CellKind.CLAIMED) {
                claimed++;
            }
        }
        return claimed * 100.0 / cells.length;
    }

    public List<Integer> claimedIndices() {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < cells.length; i++) {
            if (cells[i] == CellKind.CLAIMED) {
                indices.add(i);
            }
        }
        return indices;
    }

    public void claimOuterBorder() {
        for (int column = 0; column < columns; column++) {
            set(column, 0, CellKind.CLAIMED);
            set(column, rows - 1, CellKind.CLAIMED);
        }
        for (int row = 0; row < rows; row++) {
            set(0, row, CellKind.CLAIMED);
            set(columns - 1, row, CellKind.CLAIMED);
        }
    }
}
