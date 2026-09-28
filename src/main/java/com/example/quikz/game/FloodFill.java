package com.example.quikz.game;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public final class FloodFill {
    private static final int UNLABELED = -1;

    private FloodFill() {}

    public static List<Integer> claimEmptyRegionsNotContaining(Grid grid, Cell qixCell) {
        int[] componentOf = new int[grid.size()];
        Arrays.fill(componentOf, UNLABELED);
        List<List<Integer>> cellsByComponent = new ArrayList<>();

        for (int row = 0; row < grid.rows(); row++) {
            for (int column = 0; column < grid.columns(); column++) {
                growEmptyComponent(grid, componentOf, cellsByComponent, column, row);
            }
        }

        int qixComponent = componentContaining(grid, componentOf, qixCell);
        return claimEveryComponentExcept(grid, cellsByComponent, qixComponent);
    }

    private static void growEmptyComponent(
            Grid grid,
            int[] componentOf,
            List<List<Integer>> cellsByComponent,
            int column,
            int row) {
        int start = grid.index(column, row);
        if (grid.kindAt(column, row) != CellKind.EMPTY || componentOf[start] != UNLABELED) {
            return;
        }
        int componentId = cellsByComponent.size();
        List<Integer> cells = new ArrayList<>();
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(start);
        componentOf[start] = componentId;
        cells.add(start);
        while (!stack.isEmpty()) {
            int current = stack.pop();
            Cell cell = grid.cellAt(current);
            enqueueEmptyNeighbor(grid, componentOf, stack, cells, cell.column() - 1, cell.row(), componentId);
            enqueueEmptyNeighbor(grid, componentOf, stack, cells, cell.column() + 1, cell.row(), componentId);
            enqueueEmptyNeighbor(grid, componentOf, stack, cells, cell.column(), cell.row() - 1, componentId);
            enqueueEmptyNeighbor(grid, componentOf, stack, cells, cell.column(), cell.row() + 1, componentId);
        }
        cellsByComponent.add(cells);
    }

    private static void enqueueEmptyNeighbor(
            Grid grid,
            int[] componentOf,
            Deque<Integer> stack,
            List<Integer> cells,
            int column,
            int row,
            int componentId) {
        if (!grid.contains(column, row)) {
            return;
        }
        int index = grid.index(column, row);
        if (grid.kindAt(column, row) == CellKind.EMPTY && componentOf[index] == UNLABELED) {
            componentOf[index] = componentId;
            stack.push(index);
            cells.add(index);
        }
    }

    private static int componentContaining(Grid grid, int[] componentOf, Cell qixCell) {
        Cell clamped = new Cell(
                Clamp.between(qixCell.column(), 0, grid.columns() - 1),
                Clamp.between(qixCell.row(), 0, grid.rows() - 1));
        int qixComponent = componentOf[grid.index(clamped)];
        if (qixComponent != UNLABELED) {
            return qixComponent;
        }
        return nearestComponent(grid, componentOf, clamped);
    }

    private static int nearestComponent(Grid grid, int[] componentOf, Cell origin) {
        int maxRadius = Math.max(grid.columns(), grid.rows());
        int found = UNLABELED;
        for (int radius = 1; radius < maxRadius && found == UNLABELED; radius++) {
            for (int columnDelta = -radius; columnDelta <= radius; columnDelta++) {
                for (int rowDelta = -radius; rowDelta <= radius; rowDelta++) {
                    int column = origin.column() + columnDelta;
                    int row = origin.row() + rowDelta;
                    if (!grid.contains(column, row)) {
                        continue;
                    }
                    int component = componentOf[grid.index(column, row)];
                    if (component != UNLABELED) {
                        found = component;
                    }
                }
            }
        }
        return found;
    }

    private static List<Integer> claimEveryComponentExcept(
            Grid grid,
            List<List<Integer>> cellsByComponent,
            int sparedComponent) {
        List<Integer> claimed = new ArrayList<>();
        for (int componentId = 0; componentId < cellsByComponent.size(); componentId++) {
            if (componentId == sparedComponent) {
                continue;
            }
            for (int index : cellsByComponent.get(componentId)) {
                Cell cell = grid.cellAt(index);
                grid.claim(cell);
                claimed.add(index);
            }
        }
        return claimed;
    }
}
