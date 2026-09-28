package com.example.quikz.protocol;

import com.example.quikz.game.Cell;
import com.example.quikz.game.GameRules;
import com.example.quikz.game.GameState;
import com.example.quikz.game.Qix;
import com.example.quikz.game.Sparx;
import com.example.quikz.game.TickResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class GameProtocol {
    public static final String WEB_SOCKET_PATH = "/ws/game";

    private final ObjectMapper mapper;

    public GameProtocol(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public IncomingMessage read(String json) throws IOException {
        JsonNode node = mapper.readTree(json);
        return new IncomingMessage(typeOf(node), node.path("dx").asInt(0), node.path("dy").asInt(0));
    }

    private static String typeOf(JsonNode node) {
        JsonNode type = node.get("type");
        if (type == null || type.isNull()) {
            return "";
        }
        return type.asText();
    }

    public String initMessage(GameState state) throws JsonProcessingException {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", "init");
        payload.put("cols", GameRules.COLUMNS);
        payload.put("rows", GameRules.ROWS);
        payload.put("cell", GameRules.CELL_PIXELS);
        payload.put("claimed", state.grid().claimedIndices());
        payload.put("theme", state.theme().wireName());
        payload.put("targetPct", (int) GameRules.TARGET_CLAIM_PERCENT);
        payload.put("tickMs", GameRules.TICK_PERIOD_MS);
        return mapper.writeValueAsString(payload);
    }

    public String stateMessage(GameState state, TickResult result) throws JsonProcessingException {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", "state");
        payload.put("phase", state.phase().wireName());
        payload.put("score", state.score());
        payload.put("lives", state.lives());
        payload.put("level", state.level());
        payload.put("theme", state.theme().wireName());
        payload.put("pct", GameRules.claimedPercentToOneDecimal(state.claimedPercent()));
        payload.put("player", playerPayload(state));
        payload.put("trail", asPairs(state.player().trail()));
        payload.put("qix", qixPayload(state));
        payload.put("sparx", sparxPayload(state));
        payload.put("newlyClaimed", result.newlyClaimedIndices());
        payload.put("event", result.wireEvent());
        return mapper.writeValueAsString(payload);
    }

    private Map<String, Object> playerPayload(GameState state) {
        Map<String, Object> player = new LinkedHashMap<>();
        player.put("x", state.player().position().column());
        player.put("y", state.player().position().row());
        player.put("px0", state.player().previousColumn());
        player.put("py0", state.player().previousRow());
        player.put("drawing", state.player().isDrawing());
        player.put("invuln", state.player().isInvulnerable());
        return player;
    }

    private Map<String, Object> qixPayload(GameState state) {
        Map<String, Object> qix = new LinkedHashMap<>();
        qix.put("x", state.qix().xPixels());
        qix.put("y", state.qix().yPixels());
        qix.put("outerRadius", Qix.outerRadiusPixels(state.level()));
        qix.put("innerRadius", Qix.innerRadiusPixels(state.level()));
        return qix;
    }

    private List<int[]> sparxPayload(GameState state) {
        List<int[]> positions = new ArrayList<>();
        for (Sparx sparx : state.patrollers()) {
            positions.add(sparx.cellOn(state.perimeter()).asPair());
        }
        return positions;
    }

    private List<int[]> asPairs(List<Cell> cells) {
        List<int[]> pairs = new ArrayList<>();
        for (Cell cell : cells) {
            pairs.add(cell.asPair());
        }
        return pairs;
    }
}
