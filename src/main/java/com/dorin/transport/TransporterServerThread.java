package com.dorin.transport;

import org.apache.log4j.Logger;

import java.net.*;
import java.io.*;

public class TransporterServerThread extends Thread implements ServerConnectionProtocol {
    private Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private TransportServer server;
    private Socket socket;
    private int ID = -1;
    private DataInputStream streamIn;
    private DataOutputStream streamOut;

    TransporterServerThread(TransportServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
        ID = socket.getPort();
    }

    int getID() {
        return ID;
    }

    public void run() {
        LOGGER.info("Server Thread " + ID + " running.");
        boolean isStopped = false;
        while (!isStopped) {
            try {
                server.handle(ID, streamIn.readUTF());
            } catch (IOException ioe) {
                LOGGER.error(ID + " ERROR reading: " + ioe.getMessage());
                server.remove(ID);
                isStopped = true;
                interrupt();
            }
        }
    }

    void open() throws IOException {
        streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    @Override
    public void send(String message) {
        try {
            streamOut.writeUTF(message);
            streamOut.flush();
        } catch (IOException ioe) {
            LOGGER.error(ID + " ERROR sending: " + ioe.getMessage());
            server.remove(ID);
            this.interrupt();
        }
    }

    @Override
    public void close() throws IOException {
        if (socket != null) socket.close();
        if (streamIn != null) streamIn.close();
        if (streamOut != null) streamOut.close();
    }
}
