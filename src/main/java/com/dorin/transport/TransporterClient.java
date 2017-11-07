package com.dorin.transport;

import org.apache.log4j.Logger;

import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.Observable;

public class TransporterClient extends Observable implements Runnable {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private Socket socket;
    private Thread thread;
    private TransporterClientThread client;
    private ObjectOutputStream objectOutputStream;

    public TransporterClient(String serverName, int serverPort) {
        LOGGER.info("Establishing connection. Please wait ...");
        try {
            socket = new Socket(serverName, serverPort);
            LOGGER.info("Connected: " + socket);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            startNewConnection();
        } catch (UnknownHostException uhe) {
            LOGGER.error("Host unknown: " + uhe.getMessage());
        } catch (IOException ioe) {
            LOGGER.error("Unexpected exception: " + ioe.getMessage());
        }
    }

    @Override
    public void run() {
        //
    }

    public void send(byte[] message) throws IOException {
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();
    }

    void handleInput(byte[] message) {
        setChanged();
        notifyObservers(message);
    }

    private void startNewConnection() throws IOException {
        if (thread == null) {
            client = new TransporterClientThread(this, socket);
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
        try {
            if (objectOutputStream != null) objectOutputStream.close();
            if (socket != null) socket.close();
        } catch (IOException ioe) {
            LOGGER.error("Error closing ...");
        }
        client.close();
        client.interrupt();
    }

}
