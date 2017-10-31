package com.dorin.messagebroker;

import org.apache.log4j.Logger;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageBroker {
    private final static Logger LOGGER = Logger.getLogger(MessageBroker.class.getName());
    private final static TransportBrokerImpl transport = new TransportBrokerImpl();
    private static BlockingQueue<String> messages = new LinkedBlockingQueue<>();
//    private static MessageQueue messageQueue = MessageQueue.getInstance();

    public static void main(String[] args) {
        LOGGER.info("MessageBroker started!");

        boolean isStopped = false;
        while (!isStopped) {
            String userInput = new Scanner(System.in).nextLine();

            switch (userInput.toUpperCase()) {
                case "VIEW":
                    messages = transport.getMessages();
                    System.out.println("Messages:");
                    messages.forEach(System.out::println);
                    break;
                case "EXIT":
                    transport.close();
                    isStopped = true;
                    break;
                case "SEND":
                    messages = transport.getMessages();
                    transport.sendToAll(messages.peek());
                default:
                    break;
            }

        }

        LOGGER.info("Close MessageBroker");
    }

}
