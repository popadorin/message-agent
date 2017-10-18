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

        LOGGER.info("Type GET to stop the broker and print the messages!");
        boolean isStopped = false;
        while (!isStopped) {
            String userInput = new Scanner(System.in).nextLine();

            if (userInput.toUpperCase().equals("POP")) {
                LOGGER.info(messageQueue.removeMessage());
                continue;
            }

            if (userInput.toUpperCase().equals("SEND")) {
                String message = new Scanner(System.in).nextLine();
                transportServer.sendToAllClients(message);
            }

            if (userInput.toUpperCase().equals("GET")) {
                messages = messageQueue.getQueue();
                transportServer.stop();
                transportServer = null;
                isStopped = true;
            }
        }

        LOGGER.info("Close MessageBroker");
    }

}
