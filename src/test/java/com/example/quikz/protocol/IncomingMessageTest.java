package com.example.quikz.protocol;

import com.example.quikz.game.Direction;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IncomingMessageTest {
    @Test
    void knownTypesMapOntoCommands() {
        assertThat(new IncomingMessage("start", 0, 0).command()).isEqualTo(ClientCommand.START);
        assertThat(new IncomingMessage("retry", 0, 0).command()).isEqualTo(ClientCommand.RETRY);
        assertThat(new IncomingMessage("dir", 1, 0).command()).isEqualTo(ClientCommand.CHANGE_DIRECTION);
        assertThat(new IncomingMessage("pause", 0, 0).command()).isEqualTo(ClientCommand.TOGGLE_PAUSE);
        assertThat(new IncomingMessage("next", 0, 0).command()).isEqualTo(ClientCommand.NEXT_LEVEL);
        assertThat(new IncomingMessage("wat", 0, 0).command()).isEqualTo(ClientCommand.IGNORE);
        assertThat(new IncomingMessage(null, 0, 0).command()).isEqualTo(ClientCommand.IGNORE);
    }

    @Test
    void onlyStartAndRetryOpenTheTickLoop() {
        assertThat(new IncomingMessage("start", 0, 0).startsTickLoop()).isTrue();
        assertThat(new IncomingMessage("retry", 0, 0).startsTickLoop()).isTrue();
        assertThat(new IncomingMessage("next", 0, 0).startsTickLoop()).isFalse();
        assertThat(new IncomingMessage("dir", 0, 0).startsTickLoop()).isFalse();
    }

    @Test
    void directionUsesTheClientDeltas() {
        assertThat(new IncomingMessage("dir", 0, 1).direction()).isEqualTo(Direction.DOWN);
    }
}
