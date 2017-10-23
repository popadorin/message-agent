package com.dorin.messagebroker;

import com.dorin.transport.TransportServer;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class MessageBroker {
    private final static Logger LOGGER = Logger.getLogger(MessageBroker.class.getName());
    private final static TransportBrokerImpl transport = new TransportBrokerImpl();
    private static BlockingQueue<String> messages;
    private static MessageQueue messageQueue = MessageQueue.getInstance();

    public static void main(String[] args) {

        LOGGER.info("MessageBroker started!");
        boolean isStopped = false;
        while (!isStopped) {
            String userInput = new Scanner(System.in).nextLine();

            switch (userInput.toUpperCase()) {
                case "EXIT":
                    messages = messageQueue.getQueue();
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
