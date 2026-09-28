package com.example.quikz.protocol;

import com.example.quikz.game.ControllableRandom;
import com.example.quikz.game.GameEngine;
import com.example.quikz.game.GameRules;
import com.example.quikz.game.GameState;
import com.example.quikz.game.Phase;
import com.example.quikz.game.Qix;
import com.example.quikz.game.TickResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GameProtocolTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final GameProtocol protocol = new GameProtocol(mapper);
    private GameState state;

    @BeforeEach
    void playingField() {
        ControllableRandom random = new ControllableRandom();
        random.neverOccur();
        GameEngine engine = new GameEngine(random);
        state = new GameState();
        engine.initializeField(state);
        state.setPhase(Phase.PLAYING);
    }

    @Test
    void readPullsTypeAndDeltasFromJson() throws IOException {
        IncomingMessage message = protocol.read("{\"type\":\"dir\",\"dx\":-1,\"dy\":0}");
        assertThat(message.type()).isEqualTo("dir");
        assertThat(message.columnDelta()).isEqualTo(-1);
        assertThat(message.rowDelta()).isZero();
    }

    @Test
    void missingFieldsBecomeEmptyTypeAndZeroDeltas() throws IOException {
        IncomingMessage message = protocol.read("{}");
        assertThat(message.type()).isEmpty();
        assertThat(message.columnDelta()).isZero();
        assertThat(message.rowDelta()).isZero();
    }

    @Test
    void nullTypeBecomesEmpty() throws IOException {
        IncomingMessage message = protocol.read("{\"type\":null}");
        assertThat(message.type()).isEmpty();
    }

    @Test
    void initMessageMatchesTheClientContract() throws IOException {
        JsonNode json = mapper.readTree(protocol.initMessage(state));
        assertThat(json.get("type").asText()).isEqualTo("init");
        assertThat(json.get("cols").asInt()).isEqualTo(GameRules.COLUMNS);
        assertThat(json.get("rows").asInt()).isEqualTo(GameRules.ROWS);
        assertThat(json.get("cell").asInt()).isEqualTo(GameRules.CELL_PIXELS);
        assertThat(json.get("claimed").isArray()).isTrue();
        assertThat(json.get("claimed").size()).isGreaterThan(0);
        assertThat(json.get("theme").asText()).isEqualTo("blue");
        assertThat(json.get("targetPct").asInt()).isEqualTo((int) GameRules.TARGET_CLAIM_PERCENT);
        assertThat(json.get("tickMs").asLong()).isEqualTo(GameRules.TICK_PERIOD_MS);
    }

    @Test
    void stateMessageMatchesTheClientContract() throws IOException {
        JsonNode json = mapper.readTree(protocol.stateMessage(state, TickResult.claimed(List.of(4, 5))));
        assertThat(json.get("type").asText()).isEqualTo("state");
        assertThat(json.get("phase").asText()).isEqualTo("playing");
        assertThat(json.get("score").asInt()).isZero();
        assertThat(json.get("lives").asInt()).isEqualTo(GameRules.STARTING_LIVES);
        assertThat(json.get("level").asInt()).isEqualTo(1);
        assertThat(json.get("pct").isNumber()).isTrue();
        assertThat(json.get("player").get("x").asInt()).isEqualTo(state.player().position().column());
        assertThat(json.get("player").get("y").asInt()).isEqualTo(state.player().position().row());
        assertThat(json.get("player").get("px0").asDouble()).isEqualTo(state.player().previousColumn());
        assertThat(json.get("player").get("py0").asDouble()).isEqualTo(state.player().previousRow());
        assertThat(json.get("player").get("drawing").asBoolean()).isFalse();
        assertThat(json.get("player").get("invuln").asBoolean()).isTrue();
        assertThat(json.get("trail").isArray()).isTrue();
        assertThat(json.get("theme").asText()).isEqualTo("blue");
        assertThat(json.get("qix").get("x").asDouble()).isEqualTo(state.qix().xPixels());
        assertThat(json.get("qix").get("y").asDouble()).isEqualTo(state.qix().yPixels());
        assertThat(json.get("qix").get("outerRadius").asInt()).isEqualTo(Qix.outerRadiusPixels(1));
        assertThat(json.get("qix").get("innerRadius").asInt()).isEqualTo(Qix.innerRadiusPixels(1));
        assertThat(json.get("sparx").size()).isEqualTo(2);
        assertThat(json.get("sparx").get(0).isArray()).isTrue();
        assertThat(json.get("newlyClaimed").get(0).asInt()).isEqualTo(4);
        assertThat(json.get("event").isNull()).isTrue();
    }

    @Test
    void theStarRadiiGrowWithLevelOnTheWire() throws IOException {
        for (int i = 1; i < 11; i++) {
            state.incrementLevel();
        }
        JsonNode json = mapper.readTree(protocol.stateMessage(state, TickResult.nothingHappened()));
        assertThat(json.get("level").asInt()).isEqualTo(11);
        assertThat(json.get("qix").get("outerRadius").asInt()).isEqualTo(Qix.outerRadiusPixels(11));
        assertThat(json.get("qix").get("innerRadius").asInt()).isEqualTo(Qix.innerRadiusPixels(11));
    }

    @Test
    void gameOverEventUsesTheWireName() throws IOException {
        JsonNode json = mapper.readTree(protocol.stateMessage(state, TickResult.gameOver()));
        assertThat(json.get("event").asText()).isEqualTo("gameover");
    }
}
