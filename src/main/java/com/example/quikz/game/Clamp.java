package com.example.quikz.game;

public final class Clamp {
    private Clamp() {}

    public static int between(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static double between(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }
}
