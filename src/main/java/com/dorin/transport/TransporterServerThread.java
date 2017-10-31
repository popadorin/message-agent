package com.dorin.transport;

import org.apache.log4j.Logger;

import java.net.*;
import java.io.*;

public class TransporterServerThread extends Thread {
    private Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private TransportServer server;
    private Socket socket;
    private Integer id; // connection id
    private DataInputStream streamIn;
    private DataOutputStream streamOut;

    TransporterServerThread(TransportServer server, Socket socket) {
        LOGGER.info("Started");
        this.server = server;
        this.socket = socket;
        id = socket.getPort();
    }

    public void run() {
        LOGGER.info("Server Thread " + id + " running.");
        boolean isStopped = false;
        while (!isStopped) {
            try {
                server.handle(id, streamIn.readUTF());
            } catch (IOException ioe) {
                LOGGER.error(id + " ERROR reading: " + ioe.getMessage());
                server.remove(id);
                isStopped = true;
                interrupt();
            }
        }
    }

    void open() throws IOException {
        streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    void send(String message) {
        try {
            streamOut.writeUTF(message);
            streamOut.flush();
            LOGGER.info("Successfully sent message: " + message);
        } catch (IOException ioe) {
            LOGGER.error(id + " ERROR sending: " + ioe.getMessage());
            server.remove(id);
            this.interrupt();
        }
    }

    void close() throws IOException {
        if (socket != null) socket.close();
        if (streamIn != null) streamIn.close();
        if (streamOut != null) streamOut.close();
    }

    Integer getID() {
        return id;
    }
}
