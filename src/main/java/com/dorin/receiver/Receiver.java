package com.dorin.receiver;

import org.apache.log4j.Logger;

import java.util.Scanner;

public class Receiver {
    private static final Logger LOGGER = Logger.getLogger(Receiver.class);
    private static final TransportReceiverImpl transport = new TransportReceiverImpl();
    private static String message;

    public static void main(String[] args) {
        LOGGER.info("Receiver started");

        boolean isStopped = false;
        while (!isStopped) {
            System.out.println("Commands: SEND, EXIT");
            System.out.println("Choose command:");
            String userInput = new Scanner(System.in).nextLine();

            switch (userInput.toUpperCase()) {
                case "SEND":
                    System.out.println("Type message:");
                    String message = new Scanner(System.in).nextLine();
                    transport.send(message);
                    break;
                case "GET":
                    message = transport.readFromBroker();
                    LOGGER.info("Message from broker: " + message);
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
}
