package com.example.quikz.game;

import java.util.ArrayList;
import java.util.List;

public final class Perimeter {
    private final List<Cell> cells;

    private Perimeter(List<Cell> cells) {
        this.cells = List.copyOf(cells);
    }

    public static Perimeter of(List<Cell> cells) {
        return new Perimeter(cells);
    }

    public static Perimeter empty() {
        return new Perimeter(List.of());
    }

    public static Perimeter clockwiseBorder(int columns, int rows) {
        List<Cell> cells = new ArrayList<>();
        for (int column = 0; column < columns - 1; column++) {
            cells.add(new Cell(column, 0));
        }
        for (int row = 0; row < rows - 1; row++) {
            cells.add(new Cell(columns - 1, row));
        }
        for (int column = columns - 1; column > 0; column--) {
            cells.add(new Cell(column, rows - 1));
        }
        for (int row = rows - 1; row > 0; row--) {
            cells.add(new Cell(0, row));
        }
        return new Perimeter(cells);
    }

    public Cell at(int index) {
        return cells.get(index);
    }

    public int size() {
        return cells.size();
    }

    public boolean isEmpty() {
        return cells.isEmpty();
    }

    public int wrappedIndex(int index) {
        return Math.floorMod(index, cells.size());
    }

    public List<Cell> cells() {
        return cells;
    }

    public int indexOf(Cell cell) {
        return cells.indexOf(cell);
    }

    public int continueOnto(Perimeter newPath, int index, int stepDirection) {
        for (int step = 0; step < size(); step++) {
            Cell cell = at(wrappedIndex(index + step * stepDirection));
            int onNewPath = newPath.indexOf(cell);
            if (onNewPath >= 0) {
                return onNewPath;
            }
        }
        return newPath.indexNearestTo(at(index));
    }

    public int indexNearestTo(Cell cell) {
        int bestIndex = 0;
        int bestDistance = Integer.MAX_VALUE;
        for (int index = 0; index < cells.size(); index++) {
            int distance = cells.get(index).manhattanDistanceTo(cell);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestIndex = index;
            }
        }
        return bestIndex;
    }
}
