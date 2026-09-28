package com.example.quikz.game;

import java.util.ArrayList;
import java.util.List;

public final class Player {
    private Cell position = new Cell(0, 0);
    private double previousColumn;
    private double previousRow;
    private double millisecondsAccumulated;
    private boolean drawing;
    private final List<Cell> trail = new ArrayList<>();
    private double remainingInvulnerabilityMs;
    private Direction requestedDirection = Direction.STATIONARY;

    public Cell position() {
        return position;
    }

    public double previousColumn() {
        return previousColumn;
    }

    public double previousRow() {
        return previousRow;
    }

    public boolean isDrawing() {
        return drawing;
    }

    public List<Cell> trail() {
        return List.copyOf(trail);
    }

    public boolean isInvulnerable() {
        return remainingInvulnerabilityMs > 0;
    }

    public Direction requestedDirection() {
        return requestedDirection;
    }

    public void request(Direction direction) {
        requestedDirection = direction;
    }

    public void addElapsedTime(double milliseconds) {
        millisecondsAccumulated += milliseconds;
    }

    public boolean consumeStepIfReady() {
        if (millisecondsAccumulated < GameRules.MILLISECONDS_PER_PLAYER_STEP) {
            return false;
        }
        millisecondsAccumulated -= GameRules.MILLISECONDS_PER_PLAYER_STEP;
        return true;
    }

    public void reduceInvulnerability(double milliseconds) {
        if (remainingInvulnerabilityMs > 0) {
            remainingInvulnerabilityMs = Math.max(0, remainingInvulnerabilityMs - milliseconds);
        }
    }

    public void becomeInvulnerableFor(double milliseconds) {
        remainingInvulnerabilityMs = milliseconds;
    }

    public void placeAt(Cell cell) {
        position = cell;
        previousColumn = cell.column();
        previousRow = cell.row();
        drawing = false;
        trail.clear();
        millisecondsAccumulated = 0;
    }

    public void eraseTrailFrom(Grid grid) {
        for (Cell cell : trail) {
            grid.clearTrailIfPresent(cell);
        }
        trail.clear();
    }

    public void moveTo(Cell cell) {
        previousColumn = position.column();
        previousRow = position.row();
        position = cell;
    }

    public void beginDrawingToward(Cell emptyCell, Grid grid) {
        drawing = true;
        trail.clear();
        trail.add(position);
        grid.markTrail(emptyCell);
        trail.add(emptyCell);
        moveTo(emptyCell);
    }

    public void extendTrailTo(Cell emptyCell, Grid grid) {
        grid.markTrail(emptyCell);
        trail.add(emptyCell);
        moveTo(emptyCell);
    }

    public void finishDrawing() {
        drawing = false;
        trail.clear();
    }

    public void respawnOnSouthernBorder(Grid grid, double invulnerabilityMs) {
        eraseTrailFrom(grid);
        placeAt(Cell.bottomCenter());
        becomeInvulnerableFor(invulnerabilityMs);
    }
}
