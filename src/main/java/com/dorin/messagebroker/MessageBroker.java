package com.dorin.messagebroker;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageBroker implements Observer {
    private final static Logger LOGGER = Logger.getLogger(MessageBroker.class.getName());
    private final static TransportBrokerImpl transport = new TransportBrokerImpl();
    private static MessageQueue generalMessageQueue = new MessageQueue();
    private static final String MESSAGEQUEUE_PATH = "./src/main/resources/messagequeue-backup";

    private MessageBroker() {
        transport.addObserver(this);
    }

    public static void main(String[] args) {
        try {
            LOGGER.info("MessageBroker started!");
            System.out.println("Broker commands: BACKUP, GET BACKUP, VIEW, SEND, EXIT");

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
                    case "BACKUP":
                        backupMessageQueue(generalMessageQueue);
                        break;
                    case "GET BACKUP":
                        generalMessageQueue = readBackup();
                    case "VIEW":
                        System.out.println("Messages:");
                        generalMessageQueue.getQueue().forEach(System.out::println);
                        break;
                    case "SEND":
                        transport.sendToAll(generalMessageQueue.pop());
                        break;
                    case "EXIT":
                        transport.close();
                        isStopped = true;
                        break;
                    default:
                        break;
                }

            }

            LOGGER.info("Close MessageBroker");
        } catch (Exception ex) {
            backupMessageQueue(generalMessageQueue);

            LOGGER.error("Broker has been broken");
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        LOGGER.info("update with: observable - " + o + ", arg - " + arg);

        // put to queue
        generalMessageQueue.push((Message) arg);

        // get from queue and send to all consumers
        Message receivedMessage = generalMessageQueue.pop();
        transport.sendToAll(receivedMessage);
    }

    private static void backupMessageQueue(MessageQueue messageQueue) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(new File(MESSAGEQUEUE_PATH)));

            oos.writeObject(messageQueue);
            oos.close();
        } catch (IOException e) {
            LOGGER.error("Output exception to write in messagequeue-backup file");
        }

    }

    private static MessageQueue readBackup() {
        MessageQueue messageQueue = null;

        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(new File(MESSAGEQUEUE_PATH)));
            messageQueue = (MessageQueue) ois.readObject();

        } catch (ClassNotFoundException e) {
            LOGGER.error("ClassNotFoundException occurred");
        } catch (FileNotFoundException e) {
            LOGGER.error("FileNotFoundException occurred");
        } catch (IOException e) {
            LOGGER.error("IOException occurred");
        }

        return messageQueue;
    }
}
