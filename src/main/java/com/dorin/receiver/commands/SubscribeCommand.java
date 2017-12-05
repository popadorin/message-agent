package com.dorin.receiver.commands;

import com.dorin.models.Command;
import com.dorin.models.CommandType;
import com.dorin.models.MessageInfo;
import com.dorin.receiver.TransportReceiver;
import org.apache.log4j.Logger;

import java.util.Scanner;

public class SubscribeCommand implements Command {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private TransportReceiver transport;

    public SubscribeCommand(TransportReceiver transport) {
        this.transport = transport;
    }

    @Override
    public void execute() {
        System.out.println("To channel:");
        String channel = new Scanner(System.in).nextLine().toUpperCase();

        transport.send(new MessageInfo(channel, CommandType.SUBSCRIBE));
    }
}
