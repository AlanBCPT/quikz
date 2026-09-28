package com.example.quikz.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class ClaimedCoast {
    private static final Direction[] CLOCKWISE = {
            Direction.RIGHT, Direction.DOWN, Direction.LEFT, Direction.UP
    };
    private static final int TURN_AHEAD = 0;
    private static final int TURN_RIGHT = 1;
    private static final int TURN_BACK = 2;
    private static final int TURN_LEFT = 3;
    private static final int[] RIGHT_HAND_RULE = {TURN_RIGHT, TURN_AHEAD, TURN_LEFT, TURN_BACK};

    private ClaimedCoast() {}

    static void updatePatrol(GameState state) {
        if (state.level() < GameRules.COASTLINE_PATROL_FROM_LEVEL) {
            return;
        }
        state.reroutePatrollersAlong(trace(state.grid()));
    }

    static Perimeter trace(Grid grid) {
        Set<Cell> coast = claimedCellsTouchingEmpty(grid);
        if (coast.isEmpty()) {
            return Perimeter.empty();
        }
        return walkClockwise(coast);
    }

    private static Perimeter walkClockwise(Set<Cell> coast) {
        Cell start = northernWesternmost(coast);
        List<Cell> path = new ArrayList<>();
        Cell current = start;
        int facingNorth = clockwiseIndex(Direction.UP);
        int facing = facingNorth;
        int steps = 0;
        int maxSteps = coast.size() * CLOCKWISE.length + 1;
        do {
            path.add(current);
            int nextFacing = facingAfterRightHandTurn(current, facing, coast);
            current = CLOCKWISE[nextFacing].from(current);
            facing = nextFacing;
            steps++;
        } while (!(current.equals(start) && facing == facingNorth) && steps < maxSteps);
        return Perimeter.of(path);
    }

    private static Set<Cell> claimedCellsTouchingEmpty(Grid grid) {
        Set<Cell> coast = new HashSet<>();
        for (int row = 0; row < grid.rows(); row++) {
            for (int column = 0; column < grid.columns(); column++) {
                Cell cell = new Cell(column, row);
                if (grid.isClaimed(cell) && touchesEmpty(grid, cell)) {
                    coast.add(cell);
                }
            }
        }
        return coast;
    }

    private static boolean touchesEmpty(Grid grid, Cell cell) {
        for (int rowDelta = -1; rowDelta <= 1; rowDelta++) {
            for (int columnDelta = -1; columnDelta <= 1; columnDelta++) {
                if (columnDelta == 0 && rowDelta == 0) {
                    continue;
                }
                Cell neighbor = cell.translated(columnDelta, rowDelta);
                if (grid.contains(neighbor) && grid.isEmpty(neighbor)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Cell northernWesternmost(Set<Cell> coast) {
        Cell best = null;
        for (Cell cell : coast) {
            if (best == null
                    || cell.row() < best.row()
                    || (cell.row() == best.row() && cell.column() < best.column())) {
                best = cell;
            }
        }
        return best;
    }

    private static int clockwiseIndex(Direction direction) {
        for (int index = 0; index < CLOCKWISE.length; index++) {
            if (CLOCKWISE[index].equals(direction)) {
                return index;
            }
        }
        return 0;
    }

    private static int facingAfterRightHandTurn(Cell current, int facing, Set<Cell> coast) {
        for (int turn : RIGHT_HAND_RULE) {
            int candidate = (facing + turn) % CLOCKWISE.length;
            Cell neighbor = CLOCKWISE[candidate].from(current);
            if (coast.contains(neighbor)) {
                return candidate;
            }
        }
        return facing;
    }
}
