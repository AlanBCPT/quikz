package com.example.quikz.game;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameRulesTest {
    @Test
    void claimedPercentOnTheWireKeepsASingleDecimalPlace() {
        assertThat(GameRules.claimedPercentToOneDecimal(18.47)).isEqualTo(18.4);
        assertThat(GameRules.claimedPercentToOneDecimal(75.0)).isEqualTo(75.0);
    }
}
