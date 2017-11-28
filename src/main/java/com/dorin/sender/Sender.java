package com.dorin.sender;

import com.dorin.models.*;
import org.apache.log4j.Logger;

import java.util.Scanner;

public class Sender {
    private static final Logger LOGGER = Logger.getLogger(Sender.class);
    private static final TransportSenderImpl transport = new TransportSenderImpl();

    public static void main(String[] args) {
        LOGGER.info("Sender started");

        boolean isStopped = false;
        while (!isStopped) {
            System.out.println("Commands: CREATE, SEND, EXIT");
            System.out.println("Choose command:");
            String userInput = new Scanner(System.in).nextLine();

            switch (userInput.toUpperCase()) {
                case "CREATE":
                    treatCreate();
                    break;
                case "SEND":
                    treatSend();
                    break;
                case "EXIT":
                    transport.sendToBroker(new MessageInfo(CommandType.EXIT));
                    isStopped = true;
                    transport.close();
                    break;
                default:
                    break;
            }
        }

        LOGGER.info("Sender Stopped");
    }

    private static void treatCreate() {
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

        transport.sendToBroker(new MessageInfo(channelName, channelType, CommandType.CREATE));
    }

    private static void treatSend() {
        System.out.println("Insert message:");
        String messageContent = new Scanner(System.in).nextLine();
        System.out.println("Type channel:");
        String channel = new Scanner(System.in).nextLine().toUpperCase();
        if (channel.trim().isEmpty()) {
            channel = null;
        }
        Message message = new Message(messageContent);
        transport.sendToBroker(new MessageInfo(message, channel, CommandType.PUT));
    }
}
