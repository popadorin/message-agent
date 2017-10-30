package com.dorin.messagebroker;

import org.apache.log4j.Logger;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class MessageBroker {
    private final static Logger LOGGER = Logger.getLogger(MessageBroker.class.getName());
    private final static TransportBrokerImpl transport = new TransportBrokerImpl();
    private static BlockingQueue<String> messages;
    private static MessageQueue messageQueue = MessageQueue.getInstance();

    public static void main(String[] args) {
        LOGGER.info("MessageBroker started!");

        transport.listenToIncomingMessages();

        boolean isStopped = false;
        while (!isStopped) {
            String userInput = new Scanner(System.in).nextLine();

            switch (userInput.toUpperCase()) {
                case "VIEW":
                    System.out.println("Messages:");
                    messages.forEach(System.out::println);
                case "EXIT":
                    transport.close();
                    isStopped = true;
                    break;
                default:
                    break;
            }

        }

        LOGGER.info("Close MessageBroker");
    }

}
