package com.dorin.transport;

import org.apache.log4j.Logger;

import java.net.*;
import java.io.*;

public class TransporterServerThread extends Thread {
    private Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private TransportServer server;
    private Socket socket;
    private Integer id; // connection id
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    TransporterServerThread(TransportServer server, Socket socket) {
        LOGGER.info("Started");
        this.server = server;
        this.socket = socket;
        this.id = socket.getPort();
    }

    public void run() {
        LOGGER.info("Server Thread " + id + " running.");
        boolean isStopped = false;
        while (!isStopped) {
            try {
                byte[] message = (byte[]) objectInputStream.readObject();
                server.handle(id, message);
            } catch (IOException ioe) {
                LOGGER.error(id + " ERROR reading: " + ioe.getMessage());
                server.remove(id);
                isStopped = true;
                interrupt();
            } catch (ClassNotFoundException e) {
                LOGGER.error("Not found byte[] in the inputStream");
            }
        }
    }

    void open() throws IOException {
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
    }

    void send(byte[] message) {
        try {
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
            LOGGER.info("Successfully sent to " + id);
        } catch (IOException ioe) {
            LOGGER.error(id + " ERROR sending: " + ioe.getMessage());
            server.remove(id);
            this.interrupt();
        }
    }

    void close() throws IOException {
        if (socket != null) socket.close();
        if (objectInputStream != null) objectInputStream.close();
        if (objectOutputStream != null) objectOutputStream.close();
    }

    Integer getID() {
        return id;
    }
}
