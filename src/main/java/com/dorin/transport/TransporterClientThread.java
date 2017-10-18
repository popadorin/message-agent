package com.dorin.transport;

import org.apache.log4j.Logger;

import java.net.*;
import java.io.*;

public class TransporterClientThread extends Thread {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private Socket socket;
    private TransporterClient client;
    private DataInputStream streamIn;

    TransporterClientThread(TransporterClient client, Socket socket) {
        this.client = client;
        this.socket = socket;
        open();
        start();
    }

    private void open() {
        try {
            streamIn = new DataInputStream(socket.getInputStream());
        } catch (IOException ioe) {
            LOGGER.error("Error getting input stream: " + ioe);
            client.stop();
        }
    }

    void close() {
        try {
            if (streamIn != null) streamIn.close();
        } catch (IOException ioe) {
            LOGGER.error("Error closing input stream: " + ioe);
        }
    }

    public void run() {
        boolean isStopped = false;
        while (!isStopped) {
            try {
                client.handle(streamIn.readUTF());
            } catch (IOException ioe) {
                LOGGER.error("Listening error: " + ioe.getMessage());
                isStopped = true;
                client.stop();
            }
        }
    }
}
