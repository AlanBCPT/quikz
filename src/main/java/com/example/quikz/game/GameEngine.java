package com.example.quikz.game;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class GameEngine {
    private final RandomSource random;
    private final PlayerMover playerMover;
    private final QixMover qixMover;
    private final SparxMover sparxMover;
    private final CollisionDetector collisions;

    public GameEngine(RandomSource random) {
        this.random = random;
        this.playerMover = new PlayerMover();
        this.qixMover = new QixMover(random);
        this.sparxMover = new SparxMover(random);
        this.collisions = new CollisionDetector();
    }

    public void initializeField(GameState state) {
        int level = state.level();
        Grid grid = Grid.withClaimedBorder(GameRules.COLUMNS, GameRules.ROWS);
        Perimeter perimeter = Perimeter.clockwiseBorder(GameRules.COLUMNS, GameRules.ROWS);
        FieldTheme theme = state.hasTheme()
                ? FieldTheme.differentFrom(state.theme(), random)
                : FieldTheme.pick(random);
        state.replaceField(new FieldLayout(
                grid,
                Qix.spawnAtFieldCenter(level, random),
                perimeter,
                startingPatrollers(level, perimeter.size()),
                theme));
        state.player().respawnOnSouthernBorder(grid, GameRules.FIELD_START_INVULNERABILITY_MS);
    }

    public TickResult tick(GameState state, double elapsedMilliseconds) {
        if (!state.isPlaying()) {
            return TickResult.nothingHappened();
        }
        Optional<TickResult> closedLoop = playerMover.advance(state, elapsedMilliseconds);
        state.player().reduceInvulnerability(elapsedMilliseconds);
        if (closedLoop.isPresent()) {
            return closedLoop.get();
        }
        qixMover.advance(state, elapsedMilliseconds);
        sparxMover.advance(state, elapsedMilliseconds);
        return collisions.detect(state);
    }

    private List<Sparx> startingPatrollers(int level, int perimeterLength) {
        double speed = GameRules.SPARX_BASE_CELLS_PER_SECOND
                + (level - GameRules.STARTING_LEVEL) * GameRules.SPARX_SPEED_INCREASE_PER_LEVEL;
        List<Sparx> patrollers = new ArrayList<>();
        patrollers.add(new Sparx(GameRules.FIRST_PATROLLER_INDEX, GameRules.CLOCKWISE, speed));
        patrollers.add(new Sparx(perimeterLength / 2, GameRules.COUNTERCLOCKWISE, speed));
        return patrollers;
    }
}
