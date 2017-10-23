package com.dorin.messagebroker;

import com.dorin.transport.TransportServer;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class MessageBroker {
    private final static Logger LOGGER = Logger.getLogger(MessageBroker.class.getName());
    private static BlockingQueue<String> messages;
    private static TransportServer transportServer;
    private static MessageQueue messageQueue = MessageQueue.getInstance();

    public static void main(String[] args) {

        new Thread(() -> transportServer = new TransportServer(8878)).start();
        LOGGER.info("MessageBroker started!");
        LOGGER.info("Type STOP to stop the broker and print the messages!");
        boolean isStopped = false;
        while (!isStopped) {
            String userInput = new Scanner(System.in).nextLine();

            switch (userInput.toUpperCase()) {
                case "POP":
                    LOGGER.info(messageQueue.removeMessage());
                    break;
                case "SEND":
                    String message = new Scanner(System.in).nextLine();
                    transportServer.sendToAllClients(message);
                    break;
                case "STOP":
                    messages = messageQueue.getQueue();
                    transportServer.stop();
                    transportServer = null;
                    isStopped = true;
                    break;
                default:
                    break;
            }

        }

        LOGGER.info("Close MessageBroker");
    }

}
