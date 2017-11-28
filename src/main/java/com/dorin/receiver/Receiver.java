package com.dorin.receiver;

import com.dorin.models.ChannelType;
import com.dorin.models.MessageInfo;
import com.dorin.models.CommandType;
import com.dorin.models.Message;
import org.apache.log4j.Logger;

import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class Receiver implements Observer {
    private static final Logger LOGGER = Logger.getLogger(Receiver.class);
    private static final TransportReceiverImpl transport = new TransportReceiverImpl();

    private Receiver() {
        transport.listenFromBroker();
        transport.addObserver(this);
    }

    public static void main(String[] args) {
        LOGGER.info("Receiver started");

        new Receiver(); // activate observer

        boolean isStopped = false;
        while (!isStopped) {
            System.out.println("Commands: CREATE, SUBSCRIBE, SEND, EXIT");
            System.out.println("Choose command:");
            String userInput = new Scanner(System.in).nextLine();

            switch (userInput.toUpperCase()) {
                case "CREATE":
                    treatCreate();
                    break;
                case "SUBSCRIBE":
                    treatSubscribe();
                    break;
                case "SEND":
                    treatSend();
                    break;
                case "EXIT":
                    isStopped = true;
                    transport.close();
                    break;
                default:
                    break;
            }
        }

        LOGGER.info("Receiver Stopped");
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

        transport.send(new MessageInfo(channelName, channelType, CommandType.CREATE));
    }

    private static void treatSend() {
        System.out.println("Type command: (GET, PUT)");
        CommandType commandType;
        try {
            commandType = CommandType.valueOf(new Scanner(System.in).nextLine().toUpperCase());
        } catch (IllegalArgumentException iae) {
            LOGGER.error("There is no such command-type, please insert message information again:");
            return;
        }

        Message message = null;
        if (commandType.equals(CommandType.PUT)) {
            System.out.println("Insert message to Broker:");
            String messageContent = new Scanner(System.in).nextLine();
            message = new Message(messageContent);
        }
        System.out.println("Type channel:");
        String channel = new Scanner(System.in).nextLine().toUpperCase();

        transport.send(new MessageInfo(message, channel, commandType));
    }

    private static void treatSubscribe() {
        System.out.println("To channel:");
        String channel = new Scanner(System.in).nextLine().toUpperCase();

        transport.send(new MessageInfo(channel, CommandType.SUBSCRIBE));
    }

    @Override
    public void update(Observable o, Object arg) {
        LOGGER.info("Received message: " + arg);
    }

}
