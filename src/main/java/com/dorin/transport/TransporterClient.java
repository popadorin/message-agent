package com.dorin.transport;

import org.apache.log4j.Logger;

import java.net.*;
import java.io.*;

public class TransporterClient implements Runnable {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private Socket socket;
    private Thread thread;
    private DataOutputStream streamOut;
    private TransporterClientThread client;

    public TransporterClient(String serverName, int serverPort) {
        LOGGER.info("Establishing connection. Please wait ...");
        try {
            socket = new Socket(serverName, serverPort);
            LOGGER.info("Connected: " + socket);
            streamOut = new DataOutputStream(socket.getOutputStream());
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

    public void send(String message) throws IOException {
        streamOut.writeUTF(message);
        streamOut.flush();
    }

    void handleInput(String message) {
        if (message.equals("EXIT")) {
            LOGGER.info("Good bye. Press RETURN to exit ...");
            stop();
        } else {
            boolean readMessage = true;
            if (readMessage) {
                LOGGER.info("Received message: " + message);
            }

        }
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
            if (streamOut != null) streamOut.close();
            if (socket != null) socket.close();
        } catch (IOException ioe) {
            LOGGER.error("Error closing ...");
        }
        client.close();
        client.interrupt();
    }

}
