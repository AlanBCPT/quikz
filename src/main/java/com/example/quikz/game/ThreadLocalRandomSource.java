package com.example.quikz.game;

import java.util.concurrent.ThreadLocalRandom;

public final class ThreadLocalRandomSource implements RandomSource {
    @Override
    public double inRange(double minInclusive, double maxExclusive) {
        return ThreadLocalRandom.current().nextDouble(minInclusive, maxExclusive);
    }

    @Override
    public boolean occurs(double probability) {
        return ThreadLocalRandom.current().nextDouble() < probability;
    }

    @Override
    public int indexBelow(int exclusiveMaximum) {
        return ThreadLocalRandom.current().nextInt(exclusiveMaximum);
    }
}
