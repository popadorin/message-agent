package com.dorin.transport;

import org.apache.log4j.Logger;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.OptionalInt;

public class TransportServer extends Observable implements Runnable {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private final int MAXNROFCLIENTS = 50;
    private List<TransporterServerThread> clients = new ArrayList<>();
    private ServerSocket server;
    private Thread thread;

    public TransportServer(int port) {
        try {
            LOGGER.info("Binding to port " + port + ", please wait  ...");
            server = new ServerSocket(port);
            LOGGER.info("Server started: " + server);
            startConnection();
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

    public void sendToAllClients(String message) {
        for (TransporterServerThread client : clients) {
            client.send(message);
        }
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }


    synchronized void handle(Integer id, String input) {
        LOGGER.info("Message: id = " + id + ", input = " + input);
        setChanged();
        notifyObservers(input);
    }

    synchronized void remove(Integer id) {
        OptionalInt position = findClient(id);
        if (position.isPresent()) {
            TransporterServerThread toTerminate = clients.get(position.getAsInt());
            LOGGER.info("Removing client thread " + id + " at " + position);
            try {
                toTerminate.close();
            } catch (IOException ioe) {
                LOGGER.error("Error closing thread: " + ioe);
            }
            clients.remove(toTerminate);
            toTerminate.interrupt();
        }
    }

    private void startConnection() {
        if (thread == null) {
            LOGGER.info("start new thread");
            thread = new Thread(this);
            thread.start();
        }
    }

    private void addThread(Socket socket) {
        if (clients.size() < MAXNROFCLIENTS) {
            LOGGER.info("Client accepted: " + socket);
            clients.add(new TransporterServerThread(this, socket));
            try {
                clients.get(clients.size() - 1).open();
                clients.get(clients.size() - 1).start();
            } catch (IOException ioe) {
                LOGGER.error("Error opening thread: " + ioe);
            }
        } else
            LOGGER.info("Client refused: maximum " + clients.size() + " reached.");
    }

    private OptionalInt findClient(Integer id) {
        OptionalInt result = OptionalInt.empty();
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getID().equals(id)) {
                result = OptionalInt.of(i);
            }
        }
        return result;
    }
}
