package com.example.quikz.game;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FieldThemeTest {
    @Test
    void wireNamesAreTheThreeFieldColours() {
        assertThat(FieldTheme.ORANGE.wireName()).isEqualTo("orange");
        assertThat(FieldTheme.BLUE.wireName()).isEqualTo("blue");
        assertThat(FieldTheme.GREEN.wireName()).isEqualTo("green");
    }

    @Test
    void pickChoosesFromTheThreeThemes() {
        ControllableRandom random = new ControllableRandom();
        assertThat(FieldTheme.pick(random)).isEqualTo(FieldTheme.BLUE);
    }

    @Test
    void aNewFieldDoesNotRepeatThePreviousTheme() {
        ControllableRandom random = new ControllableRandom();
        assertThat(FieldTheme.differentFrom(FieldTheme.BLUE, random)).isNotEqualTo(FieldTheme.BLUE);
        assertThat(FieldTheme.differentFrom(FieldTheme.ORANGE, random)).isNotEqualTo(FieldTheme.ORANGE);
        assertThat(FieldTheme.differentFrom(FieldTheme.GREEN, random)).isNotEqualTo(FieldTheme.GREEN);
    }
}
