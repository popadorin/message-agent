package com.dorin.sender;

import com.dorin.messagebroker.CommandType;
import com.dorin.messagebroker.Message;
import org.apache.log4j.Logger;

import java.util.Scanner;

public class Sender {
    private static final Logger LOGGER = Logger.getLogger(Sender.class);
    private static final TransportSenderImpl transport = new TransportSenderImpl();

    public static void main(String[] args) {
        LOGGER.info("Sender started");

        boolean isStopped = false;
        while (!isStopped) {
            System.out.println("Commands: SEND, EXIT");
            System.out.println("Choose command:");
            String userInput = new Scanner(System.in).nextLine();

            switch (userInput.toUpperCase()) {
                case "SEND":
                    System.out.println("Type message:");
                    String messageContent = new Scanner(System.in).nextLine();
                    transport.sendToBroker(new Message(CommandType.PUT, messageContent));
                    break;
                case "EXIT":
                    isStopped = true;
                    transport.close();
                    break;
                default:
                    break;
            }
        }

        LOGGER.info("Sender Stopped");
    }
}
