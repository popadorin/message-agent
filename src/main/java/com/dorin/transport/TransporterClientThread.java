package com.dorin.transport;

import java.net.*;
import java.io.*;

public class TransporterClientThread extends Thread {
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
            System.out.println("Error getting input stream: " + ioe);
            client.stop();
        }
    }

    void close() {
        try {
            if (streamIn != null) streamIn.close();
        } catch (IOException ioe) {
            System.out.println("Error closing input stream: " + ioe);
        }
    }

    public void run() {
        boolean isStopped = false;
        while (!isStopped) {
            try {
                client.handle(streamIn.readUTF());
            } catch (IOException ioe) {
                System.out.println("Listening error: " + ioe.getMessage());
                isStopped = true;
                client.stop();
            }
        }
    }
}