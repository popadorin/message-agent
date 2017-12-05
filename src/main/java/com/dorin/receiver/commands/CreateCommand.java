package com.dorin.receiver.commands;

import com.dorin.models.ChannelType;
import com.dorin.models.Command;
import com.dorin.models.CommandType;
import com.dorin.models.MessageInfo;
import com.dorin.receiver.TransportReceiver;
import org.apache.log4j.Logger;

import java.util.Scanner;

public class CreateCommand implements Command {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private final TransportReceiver transport;

    public CreateCommand(TransportReceiver transport) {
        this.transport = transport;
    }


    @Override
    public void execute() {
        System.out.println("Insert channel name:");
        String channelName = new Scanner(System.in).nextLine().toUpperCase();
        System.out.println("Choose channel type: \n 1. PERSISTENT \n 2. NONPERSISTENT");

        ChannelType channelType;
        try {
            channelType = ChannelType.getChannelType(Integer.parseInt(
                    new Scanner(System.in).nextLine().toUpperCase()));
        } catch (IllegalArgumentException iae) {
            LOGGER.error("No such channel-type");
            return;
        }

        transport.send(new MessageInfo(channelName, channelType, CommandType.CREATE));
    }
}
