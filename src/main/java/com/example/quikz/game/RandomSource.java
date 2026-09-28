package com.example.quikz.game;

public interface RandomSource {
    double inRange(double minInclusive, double maxExclusive);

    boolean occurs(double probability);

    int indexBelow(int exclusiveMaximum);
}
