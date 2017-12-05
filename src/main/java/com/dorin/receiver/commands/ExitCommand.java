package com.dorin.receiver.commands;

import com.dorin.models.Command;
import com.dorin.receiver.TransportReceiver;
import org.apache.log4j.Logger;

public class ExitCommand implements Command {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private final TransportReceiver transport;

    public ExitCommand(TransportReceiver transport) {
        this.transport = transport;
    }

    @Override
    public void execute() {
        transport.close();
    }
}
