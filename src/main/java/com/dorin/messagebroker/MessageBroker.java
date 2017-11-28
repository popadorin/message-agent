package com.dorin.messagebroker;

import com.dorin.models.*;
import org.apache.log4j.Logger;

import java.util.*;

public class MessageBroker implements Observer {
    private final static Logger LOGGER = Logger.getLogger(MessageBroker.class.getName());
    public final static TransportBrokerImpl transport = new TransportBrokerImpl();

    // queues
    private static MessageQueue generalMessageQueue = new MessageQueue();

    private MessageBroker() {
        transport.listenToMessages();
        transport.addObserver(this);
    }

    public static void main(String[] args) {
        LOGGER.info("MessageBroker started!");
        System.out.println("Broker commands: LS, SEND, EXIT");

        new MessageBroker();

        boolean isStopped = false;
        while (!isStopped) {
            String userInput = new Scanner(System.in).nextLine();

            switch (userInput.toUpperCase().trim()) {
                case "LS":
                    System.out.println("List all the messages:");
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
                    LOGGER.error("No such command");
                    break;
            }

        }

        LOGGER.info("Close MessageBroker");

    }

    @Override
    public void update(Observable o, Object arg) {
        MessageInfo inputInfo = (MessageInfo) arg;

        LOGGER.info("update with arg - " + arg);
        treatMessageInput(inputInfo);
    }

    private void treatMessageInput(MessageInfo inputInfo) {
        switch (inputInfo.getCommandType()) {
            case GET:
                treatGet(inputInfo.getId());
                break;
            case PUT:
                treatPut(inputInfo.getMessage());
                break;
            default:
                LOGGER.error("Something wrong with the command-type");
                break;
        }

    }

    private void treatGet(Integer id) {
        transport.send(id, generalMessageQueue.pop());
    }

    private void treatPut(Message message) {
        generalMessageQueue.push(message);
    }

}
