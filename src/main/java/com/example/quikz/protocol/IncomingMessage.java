package com.example.quikz.protocol;

import com.example.quikz.game.Direction;

public record IncomingMessage(String type, int columnDelta, int rowDelta) {
    public ClientCommand command() {
        if (type == null) {
            return ClientCommand.IGNORE;
        }
        return switch (type) {
            case "start" -> ClientCommand.START;
            case "retry" -> ClientCommand.RETRY;
            case "dir" -> ClientCommand.CHANGE_DIRECTION;
            case "pause" -> ClientCommand.TOGGLE_PAUSE;
            case "next" -> ClientCommand.NEXT_LEVEL;
            default -> ClientCommand.IGNORE;
        };
    }

    public boolean startsTickLoop() {
        return command() == ClientCommand.START || command() == ClientCommand.RETRY;
    }

    public Direction direction() {
        return Direction.of(columnDelta, rowDelta);
    }
}
