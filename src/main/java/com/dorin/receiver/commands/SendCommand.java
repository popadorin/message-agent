package com.dorin.receiver.commands;

import com.dorin.models.Command;
import com.dorin.models.CommandType;
import com.dorin.models.Message;
import com.dorin.models.MessageInfo;
import com.dorin.receiver.TransportReceiver;
import org.apache.log4j.Logger;

import java.util.Scanner;

public class SendCommand implements Command {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private final TransportReceiver transport;

    public SendCommand(TransportReceiver transport) {
        this.transport = transport;
    }

    @Override
    public void execute() {
        System.out.println("Type command: (GET, PUT)");
        CommandType commandType;
        try {
            commandType = CommandType.valueOf(new Scanner(System.in).nextLine().toUpperCase());
        } catch (IllegalArgumentException iae) {
            LOGGER.error("There is no such command-type, please insert message information again:");
            return;
        }

        Message message = null;
        if (commandType.equals(CommandType.PUT)) {
            System.out.println("Insert message to Broker:");
            String messageContent = new Scanner(System.in).nextLine();
            message = new Message(messageContent);
        }
        System.out.println("Type channel:");
        String channel = new Scanner(System.in).nextLine().toUpperCase();

        transport.send(new MessageInfo(message, channel, commandType));
    }
}
