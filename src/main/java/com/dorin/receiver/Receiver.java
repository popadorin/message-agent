package com.dorin.receiver;

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
        transport.addObserver(this);
    }

    public static void main(String[] args) {
        LOGGER.info("Receiver started");

        try {
            Thread.sleep(2000);
            transport.listenFromBroker();
        } catch (InterruptedException e) {
            LOGGER.error("thread sleep interrupted exception occurred!");
        }

        transport.listenFromBroker();

        new Receiver(); // activate observer

        boolean isStopped = false;
        while (!isStopped) {
            System.out.println("Commands: SEND, EXIT");
            System.out.println("Choose command:");
            String userInput = new Scanner(System.in).nextLine();

            switch (userInput.toUpperCase()) {
                case "SUBSCRIBE":
                    transport.send(new Message(CommandType.SUBSCRIBE, null));
                    break;
                case "SEND":
                    System.out.println("Type message to Broker:");
                    String messageContent = new Scanner(System.in).nextLine();
                    transport.send(new Message(CommandType.GET, messageContent));
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

    @Override
    public void update(Observable o, Object arg) {
        LOGGER.info("Received message: " + arg);
    }
}
