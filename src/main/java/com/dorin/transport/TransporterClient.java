package com.dorin.transport;

import org.apache.log4j.Logger;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class TransporterClient implements Runnable {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private Socket socket;
    private Thread thread;
    private String message;
    private DataOutputStream streamOut;
    private TransporterClientThread client;

    public TransporterClient(String serverName, int serverPort) {
        LOGGER.info("Establishing connection. Please wait ...");
        try {
            socket = new Socket(serverName, serverPort);
            LOGGER.info("Connected: " + socket);
            start();
        } catch (UnknownHostException uhe) {
            LOGGER.error("Host unknown: " + uhe.getMessage());
        } catch (IOException ioe) {
            LOGGER.error("Unexpected exception: " + ioe.getMessage());
        }
    }

    public void run() {
        while (thread != null) {
            try {
                message = new Scanner(System.in).nextLine();
                streamOut.writeUTF(message);
                streamOut.flush();
            } catch (IOException ioe) {
                LOGGER.error("Sending error: " + ioe.getMessage());
                stop();
            }
        }
    }

    void handle(String msg) {
        if (msg.equals("EXIT")) {
            LOGGER.info("Good bye. Press RETURN to exit ...");
            stop();
        } else
            LOGGER.info(msg);
    }

    private void start() throws IOException {
        streamOut = new DataOutputStream(socket.getOutputStream());
        if (thread == null) {
            client = new TransporterClientThread(this, socket);
            thread = new Thread(this);
            thread.start();
        }
    }

    void stop() {
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
