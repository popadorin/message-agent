package com.dorin.receiver;

import com.dorin.models.Command;
import com.dorin.receiver.commands.*;

import java.util.Optional;

public class CommandProvider {
    private final TransportReceiver transport;

    public CommandProvider(TransportReceiver transport) {
        this.transport = transport;
    }

    public Optional<Command> getCommand(String userInput) {
        switch (userInput.toUpperCase().trim()) {
            case "CREATE":
                return Optional.of(new CreateCommand(transport));
            case "SUBSCRIBE":
                return Optional.of(new SubscribeCommand(transport));
            case "SEND":
                return Optional.of(new SendCommand(transport));
            case "EXIT":
                return Optional.of(new ExitCommand(transport));
            default:
                return Optional.empty();
        }
    }
}
