package com.dorin.messagebroker;

import com.dorin.transport.TransportServer;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class MessageBroker {
    private static BlockingQueue<String> messages;
    private static TransportServer transportServer;

    public static void main(String[] args) {

        new Thread(() -> transportServer = new TransportServer(8878)).start();

        System.out.println("Type GET to stop the broker and print the messages!");
        boolean isStopped = false;
        while (!isStopped) {

            Scanner scanner = new Scanner(System.in);
            String userInput = scanner.nextLine();

            if (userInput.toUpperCase().equals("GET")) {
                messages = transportServer.getMessages();
                transportServer.stop();
                isStopped = true;
            }
        }

        messages.forEach(System.out::println);

        System.out.println("Close MessageBroker");
    }

}
