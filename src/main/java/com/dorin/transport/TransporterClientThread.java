package com.dorin.transport;

import org.apache.log4j.Logger;

import java.net.*;
import java.io.*;

public class TransporterClientThread extends Thread {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private Socket socket;
    private TransporterClient client;
    private ObjectInputStream objectInputStream;

    TransporterClientThread(TransporterClient client, Socket socket) {
        LOGGER.info("New Thread connection successfully started");
        this.client = client;
        this.socket = socket;
        initializeReader();
        start();
    }

    private void initializeReader() {
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ioe) {
            LOGGER.error("Error getting input stream: " + ioe);
            client.stop();
        }
    }

    void close() {
        try {
            if (objectInputStream != null) objectInputStream.close();
        } catch (IOException ioe) {
            LOGGER.error("Error closing input stream: " + ioe);
        }
    }

    public void run() {
        boolean isStopped = false;
        while (!isStopped) {
            try {
                byte[] message = (byte []) objectInputStream.readObject();
                client.handleInput(message);
            } catch (IOException ioe) {
                LOGGER.error("Listening error: " + ioe.getMessage());
                isStopped = true;
                client.stop();
            } catch (ClassNotFoundException e) {
                LOGGER.info("ClassNotFoundException error on message reading");
            }
        }
    }

}
