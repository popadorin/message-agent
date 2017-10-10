package com.dorin.messagebroker;

import com.dorin.transport.TransportServer;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class MessageBroker {
    private static BlockingQueue<String> messages;
    private static TransportServer transportServer;
    private static MessageQueue messageQueue = MessageQueue.getInstance();

    public static void main(String[] args) {

        new Thread(() -> transportServer = new TransportServer(8878)).start();

        System.out.println("Type GET to stop the broker and print the messages!");
        boolean isStopped = false;
        while (!isStopped) {
            String userInput = new Scanner(System.in).nextLine();

            if (userInput.toUpperCase().equals("POP")) {
                System.out.println(messageQueue.removeMessage());
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

        System.out.println("Close MessageBroker");
    }

}
