package com.dorin.sender;

import com.dorin.helpers.MessageInfo;
import com.dorin.models.Channel;
import com.dorin.models.CommandType;
import com.dorin.models.Message;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;
import java.util.Scanner;

public class Sender {
    private static final Logger LOGGER = Logger.getLogger(Sender.class);
    private static final TransportSenderImpl transport = new TransportSenderImpl();

    public static void main(String[] args) {
        LOGGER.info("Sender started");

        boolean isStopped = false;
        while (!isStopped) {
            System.out.println("Commands: SEND, EXIT");
            System.out.println("Choose command:");
            String userInput = new Scanner(System.in).nextLine();

            switch (userInput.toUpperCase()) {
                case "SEND":
                    System.out.println("Insert message:");
                    String messageContent = new Scanner(System.in).nextLine();
                    System.out.println("Type channel:");
                    Channel channel;
                    try {
                        channel = Channel.valueOf(new Scanner(System.in).nextLine().toUpperCase());
                    } catch (IllegalArgumentException iae) {
                        channel = null;
                    }
                    Message message = new Message(messageContent);
                    transport.sendToBroker(new MessageInfo(message, channel, CommandType.PUT));
                    break;
                case "EXIT":
                    isStopped = true;
                    transport.close();
                    break;
                default:
                    break;
            }
        }

        LOGGER.info("Sender Stopped");
    }
}
