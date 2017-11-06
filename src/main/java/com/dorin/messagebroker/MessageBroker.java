package com.dorin.messagebroker;

import org.apache.log4j.Logger;

import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageBroker implements Observer {
    private final static Logger LOGGER = Logger.getLogger(MessageBroker.class.getName());
    private final static TransportBrokerImpl transport = new TransportBrokerImpl();
    private static BlockingQueue<Message> allReceivedMessages = new LinkedBlockingQueue<>();
    private final static MessageQueue generalMessageQueue = new MessageQueue();

    private MessageBroker() {
        transport.addObserver(this);
    }

    public static void main(String[] args) {
        LOGGER.info("MessageBroker started!");

        try {
            Thread.sleep(2000);    // because the transport could not be initialized yet
            transport.listenToMessages();
        } catch (InterruptedException e) {
            LOGGER.error("thread sleep interrupted exception occurred!");
        }

        new MessageBroker();

        boolean isStopped = false;
        while (!isStopped) {
            String userInput = new Scanner(System.in).nextLine();

            switch (userInput.toUpperCase()) {
                case "VIEW":
                    System.out.println("Messages:");
                    allReceivedMessages.forEach(System.out::println);
                    break;
                case "EXIT":
                    transport.close();
                    isStopped = true;
                    break;
                case "SEND":
                    transport.sendToAll(allReceivedMessages.peek());
                default:
                    break;
            }

        }

        LOGGER.info("Close MessageBroker");
    }

    @Override
    public void update(Observable o, Object arg) {
        LOGGER.info("update with: observable - " + o + ", arg - " + arg);
        allReceivedMessages.add((Message) arg);

        // put to queue
        generalMessageQueue.push((Message) arg);

        // get from queue and send to all consumers
        Message receivedMessage = generalMessageQueue.pop();
        transport.sendToAll(receivedMessage);
    }
}
