package com.dorin.transport;

import com.dorin.messagebroker.MessageQueue;
import org.apache.log4j.Logger;

import java.net.*;
import java.io.*;

public class TransportServer implements Runnable {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private int maxNrOfClients = 50;
    private TransporterServerThread clients[] = new TransporterServerThread[maxNrOfClients];
    private ServerSocket server;
    private Thread thread;
    private int clientCount;
    private MessageQueue messageQueue = MessageQueue.getInstance();

    public TransportServer(int port) {
        try {
            LOGGER.info("Binding to port " + port + ", please wait  ...");
            server = new ServerSocket(port);
            LOGGER.info("Server started: " + server);
            start();
        } catch (IOException ioe) {
            LOGGER.error("Can not bind to port " + port + ": " + ioe.getMessage());
        }
    }

    public void run() {
        while (thread != null) {
            try {
                LOGGER.info("Waiting for a client ...");
                addThread(server.accept());
            } catch (IOException ioe) {
                LOGGER.error("Server accept error: " + ioe);
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
        LOGGER.info("Message: ID = " + ID + ", input = " + input);
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
            LOGGER.info("Removing client thread " + ID + " at " + pos);
            if (pos < clientCount - 1)
                for (int i = pos + 1; i < clientCount; i++)
                    clients[i - 1] = clients[i];
            clientCount--;
            try {
                toTerminate.close();
            } catch (IOException ioe) {
                LOGGER.error("Error closing thread: " + ioe);
            }
            toTerminate.interrupt();
        }
    }

    private void addThread(Socket socket) {
        if (clientCount < clients.length) {
            LOGGER.info("Client accepted: " + socket);
            clients[clientCount] = new TransporterServerThread(this, socket);
            try {
                clients[clientCount].open();
                clients[clientCount].start();
                clientCount++;
            } catch (IOException ioe) {
                LOGGER.error("Error opening thread: " + ioe);
            }
        } else
            LOGGER.info("Client refused: maximum " + clients.length + " reached.");
    }

    public void sendToAllClients(String message) {
        for (int i = 0; i < clientCount; i++) {
            clients[i].send(message);
        }
    }

}
