package com.example.quikz.session;

import com.example.quikz.game.Cell;
import com.example.quikz.game.ControllableRandom;
import com.example.quikz.game.Direction;
import com.example.quikz.game.GameEngine;
import com.example.quikz.game.GameRules;
import com.example.quikz.game.Phase;
import com.example.quikz.protocol.IncomingMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameSessionTest {
    private GameSession session;

    @BeforeEach
    void openASession() {
        ControllableRandom random = new ControllableRandom();
        random.neverOccur();
        session = new GameSession(new GameEngine(random));
    }

    @Test
    void startLaysOutTheFieldAndBeginsPlay() {
        boolean rebuiltTheField = session.rebuiltTheField(new IncomingMessage("start", 0, 0));
        assertThat(rebuiltTheField).isTrue();
        assertThat(session.state().phase()).isEqualTo(Phase.PLAYING);
        assertThat(session.state().player().position()).isEqualTo(Cell.bottomCenter());
        assertThat(session.state().grid().isClaimed(new Cell(0, 0))).isTrue();
    }

    @Test
    void retryResetsScoreLivesAndLevel() {
        session.rebuiltTheField(new IncomingMessage("start", 0, 0));
        session.state().addScore(80);
        session.state().loseLife();
        session.state().incrementLevel();
        session.rebuiltTheField(new IncomingMessage("retry", 0, 0));
        assertThat(session.state().score()).isZero();
        assertThat(session.state().lives()).isEqualTo(GameRules.STARTING_LIVES);
        assertThat(session.state().level()).isEqualTo(1);
        assertThat(session.state().phase()).isEqualTo(Phase.PLAYING);
    }

    @Test
    void directionIsStoredOnThePlayer() {
        session.rebuiltTheField(new IncomingMessage("start", 0, 0));
        assertThat(session.rebuiltTheField(new IncomingMessage("dir", -1, 0))).isFalse();
        assertThat(session.state().player().requestedDirection()).isEqualTo(Direction.LEFT);
    }

    @Test
    void pauseTogglesPlay() {
        session.rebuiltTheField(new IncomingMessage("start", 0, 0));
        session.rebuiltTheField(new IncomingMessage("pause", 0, 0));
        assertThat(session.state().phase()).isEqualTo(Phase.PAUSED);
        session.rebuiltTheField(new IncomingMessage("pause", 0, 0));
        assertThat(session.state().phase()).isEqualTo(Phase.PLAYING);
    }

    @Test
    void pauseIsIgnoredAfterTheLevelIsComplete() {
        session.rebuiltTheField(new IncomingMessage("start", 0, 0));
        session.state().setPhase(Phase.LEVEL_UP);
        session.rebuiltTheField(new IncomingMessage("pause", 0, 0));
        assertThat(session.state().phase()).isEqualTo(Phase.LEVEL_UP);
    }

    @Test
    void nextIncrementsTheLevelAndRelaysTheField() {
        session.rebuiltTheField(new IncomingMessage("start", 0, 0));
        boolean rebuiltTheField = session.rebuiltTheField(new IncomingMessage("next", 0, 0));
        assertThat(rebuiltTheField).isTrue();
        assertThat(session.state().level()).isEqualTo(2);
        assertThat(session.state().phase()).isEqualTo(Phase.PLAYING);
    }

    @Test
    void unknownCommandsAreIgnored() {
        assertThat(session.rebuiltTheField(new IncomingMessage("nope", 1, 1))).isFalse();
        assertThat(session.state().phase()).isEqualTo(Phase.IDLE);
    }

    @Test
    void advanceTimeRunsTheEngineWhilePlaying() {
        session.rebuiltTheField(new IncomingMessage("start", 0, 0));
        session.rebuiltTheField(new IncomingMessage("dir", -1, 0));
        session.advanceTime(GameRules.MILLISECONDS_PER_PLAYER_STEP);
        assertThat(session.state().player().position()).isEqualTo(Cell.bottomCenter().translated(-1, 0));
    }
}
