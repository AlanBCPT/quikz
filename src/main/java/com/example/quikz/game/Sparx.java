package com.example.quikz.game;

public final class Sparx {
    private int perimeterIndex;
    private int stepDirection;
    private final double cellsPerSecond;
    private double millisecondsAccumulated;

    public Sparx(int perimeterIndex, int stepDirection, double cellsPerSecond) {
        this.perimeterIndex = perimeterIndex;
        this.stepDirection = stepDirection;
        this.cellsPerSecond = cellsPerSecond;
        this.millisecondsAccumulated = 0;
    }

    public int perimeterIndex() {
        return perimeterIndex;
    }

    public int stepDirection() {
        return stepDirection;
    }

    public double cellsPerSecond() {
        return cellsPerSecond;
    }

    public void placeAt(int perimeterIndex) {
        this.perimeterIndex = perimeterIndex;
    }

    public void continueOnto(Perimeter newPath, Perimeter oldPath) {
        perimeterIndex = oldPath.continueOnto(newPath, perimeterIndex, stepDirection);
    }

    public void addElapsedTime(double milliseconds) {
        millisecondsAccumulated += milliseconds;
    }

    public boolean consumeStepIfReady(double millisecondsPerStep) {
        if (millisecondsAccumulated < millisecondsPerStep) {
            return false;
        }
        millisecondsAccumulated -= millisecondsPerStep;
        return true;
    }

    public void advanceOn(Perimeter perimeter) {
        perimeterIndex = perimeter.wrappedIndex(perimeterIndex + stepDirection);
    }

    public void reverse() {
        stepDirection *= -1;
    }

    public Cell cellOn(Perimeter perimeter) {
        return perimeter.at(perimeterIndex);
    }
}
