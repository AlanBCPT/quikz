package com.example.quikz.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TickResultTest {
    @Test
    void nothingHappenedCarriesNoClaimAndNoEvent() {
        TickResult result = TickResult.nothingHappened();
        assertThat(result.closedALoop()).isFalse();
        assertThat(result.wireEvent()).isNull();
        assertThat(result.event()).isEqualTo(GameEvent.NONE);
    }

    @Test
    void nullArgumentsBecomeEmptyAndNone() {
        TickResult result = new TickResult(null, null);
        assertThat(result.newlyClaimedIndices()).isEmpty();
        assertThat(result.event()).isEqualTo(GameEvent.NONE);
    }

    @Test
    void claimedIndicesAreCopiedSoLaterMutationDoesNotLeak() {
        List<Integer> source = new ArrayList<>();
        source.add(4);
        TickResult result = TickResult.claimed(source);
        source.add(9);
        assertThat(result.newlyClaimedIndices()).containsExactly(4);
        assertThat(result.closedALoop()).isTrue();
    }

    @Test
    void factoriesCarryTheMatchingEvent() {
        assertThat(TickResult.levelUp(List.of(1)).event()).isEqualTo(GameEvent.LEVEL_UP);
        assertThat(TickResult.death().event()).isEqualTo(GameEvent.DEATH);
        assertThat(TickResult.gameOver().wireEvent()).isEqualTo("gameover");
    }

    @Test
    void phaseWireNamesMatchTheClientContract() {
        assertThat(Phase.IDLE.wireName()).isEqualTo("idle");
        assertThat(Phase.PLAYING.wireName()).isEqualTo("playing");
        assertThat(Phase.PAUSED.wireName()).isEqualTo("paused");
        assertThat(Phase.LEVEL_UP.wireName()).isEqualTo("levelup");
        assertThat(Phase.OVER.wireName()).isEqualTo("over");
    }
}
