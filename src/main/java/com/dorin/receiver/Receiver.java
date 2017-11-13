package com.dorin.receiver;

import com.dorin.helpers.MessageInfo;
import com.dorin.models.Channel;
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
            System.out.println("Commands: SEND, EXIT");
            System.out.println("Choose command:");
            String userInput = new Scanner(System.in).nextLine();

            switch (userInput.toUpperCase()) {
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

    private static void treatSend() {
        System.out.println("Type command:");
        CommandType commandType;
        try {
            commandType = CommandType.valueOf(new Scanner(System.in).nextLine().toUpperCase());
        } catch (IllegalArgumentException iae) {
            LOGGER.error("There is no such command-type, please insert message information again:");
            return;
        }
        System.out.println("Insert message to Broker:");
        String messageContent = new Scanner(System.in).nextLine();
        System.out.println("Type channel:");
        Channel channelToSend;
        try {
            channelToSend = Channel.valueOf(new Scanner(System.in).nextLine().toUpperCase());
        } catch (IllegalArgumentException iae) {
            channelToSend = null;
        }
        Message message = new Message(messageContent);
        transport.send(new MessageInfo(message, channelToSend, commandType));
    }

    private static void treatSubscribe() {
        System.out.println("To channel:");
        Channel channel;
        try {
            channel = Channel.valueOf(new Scanner(System.in).nextLine().toUpperCase());
        } catch (IllegalArgumentException iae) {
            LOGGER.error("channel not set");
            channel = null;
        }
        transport.send(new MessageInfo(null, channel, CommandType.SUBSCRIBE));
    }

    @Override
    public void update(Observable o, Object arg) {
        LOGGER.info("Received message: " + arg);
    }

}
