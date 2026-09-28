package com.example.quikz.game;

public enum FieldTheme {
    ORANGE,
    BLUE,
    GREEN;

    public String wireName() {
        return name().toLowerCase();
    }

    public static FieldTheme pick(RandomSource random) {
        FieldTheme[] themes = values();
        return themes[random.indexBelow(themes.length)];
    }

    public static FieldTheme differentFrom(FieldTheme current, RandomSource random) {
        FieldTheme[] themes = values();
        int shift = 1 + random.indexBelow(themes.length - 1);
        return themes[(current.ordinal() + shift) % themes.length];
    }
}
