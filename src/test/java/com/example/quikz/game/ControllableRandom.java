package com.example.quikz.game;

public final class ControllableRandom implements RandomSource {
    private double fraction = 0.5;
    private Boolean occursOverride;

    public void alwaysOccur() {
        occursOverride = true;
    }

    public void neverOccur() {
        occursOverride = false;
    }

    @Override
    public double inRange(double minInclusive, double maxExclusive) {
        return minInclusive + fraction * (maxExclusive - minInclusive);
    }

    @Override
    public boolean occurs(double probability) {
        if (occursOverride != null) {
            return occursOverride;
        }
        return fraction < probability;
    }

    @Override
    public int indexBelow(int exclusiveMaximum) {
        return (int) (fraction * exclusiveMaximum);
    }
}
