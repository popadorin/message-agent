package com.dorin.transport;

import com.dorin.messagebroker.MessageQueue;

import java.net.*;
import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TransportServer implements Runnable {
    private int maxNrOfClients = 50;
    private TransporterServerThread clients[] = new TransporterServerThread[maxNrOfClients];
    private ServerSocket server;
    private Thread thread;
    private int clientCount;
    private MessageQueue messageQueue = MessageQueue.getInstance();

    public TransportServer(int port) {
        try {
            System.out.println("Binding to port " + port + ", please wait  ...");
            server = new ServerSocket(port);
            System.out.println("Server started: " + server);
            start();
        } catch (IOException ioe) {
            System.out.println("Can not bind to port " + port + ": " + ioe.getMessage());
        }
    }

    public void run() {
        while (thread != null) {
            try {
                System.out.println("Waiting for a client ...");
                addThread(server.accept());
            } catch (IOException ioe) {
                System.out.println("Server accept error: " + ioe);
                stop();
            }
        }
    }

    private void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    private int findClient(int ID) {
        for (int i = 0; i < clientCount; i++)
            if (clients[i].getID() == ID)
                return i;
        return -1;
    }

    synchronized void handle(int ID, String input) {
        System.out.println("Message: ID = " + ID + ", input = " + input);
        messageQueue.addMessage(input);
        if (input.equals("EXIT")) {
            clients[findClient(ID)].send("EXIT");
            remove(ID);
        } else {
            String message = messageQueue.removeMessage();
            sendToAllClients(ID + ": " + message);
        }
    }

    synchronized void remove(int ID) {
        int pos = findClient(ID);
        if (pos >= 0) {
            TransporterServerThread toTerminate = clients[pos];
            System.out.println("Removing client thread " + ID + " at " + pos);
            if (pos < clientCount - 1)
                for (int i = pos + 1; i < clientCount; i++)
                    clients[i - 1] = clients[i];
            clientCount--;
            try {
                toTerminate.close();
            } catch (IOException ioe) {
                System.out.println("Error closing thread: " + ioe);
            }
            toTerminate.interrupt();
        }
    }

    private void addThread(Socket socket) {
        if (clientCount < clients.length) {
            System.out.println("Client accepted: " + socket);
            clients[clientCount] = new TransporterServerThread(this, socket);
            try {
                clients[clientCount].open();
                clients[clientCount].start();
                clientCount++;
            } catch (IOException ioe) {
                System.out.println("Error opening thread: " + ioe);
            }
        } else
            System.out.println("Client refused: maximum " + clients.length + " reached.");
    }

    public void sendToAllClients(String message) {
        for (int i = 0; i < clientCount; i++) {
            clients[i].send(message);
        }
    }
}
