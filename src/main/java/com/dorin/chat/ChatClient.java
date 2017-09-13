package com.dorin.chat;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ChatClient implements Runnable {
    private Socket socket;
    private Thread thread;
    private String message;
    private DataOutputStream streamOut;
    private ChatClientThread client;

    private ChatClient(String serverName, int serverPort) {
        System.out.println("Establishing connection. Please wait ...");
        try {
            socket = new Socket(serverName, serverPort);
            System.out.println("Connected: " + socket);
            start();
        } catch (UnknownHostException uhe) {
            System.out.println("Host unknown: " + uhe.getMessage());
        } catch (IOException ioe) {
            System.out.println("Unexpected exception: " + ioe.getMessage());
        }
    }

    public void run() {
        while (thread != null) {
            try {
                message = new Scanner(System.in).nextLine();
                streamOut.writeUTF(message);
                streamOut.flush();
            } catch (IOException ioe) {
                System.out.println("Sending error: " + ioe.getMessage());
                stop();
            }
        }
    }

    void handle(String msg) {
        if (msg.equals("EXIT")) {
            System.out.println("Good bye. Press RETURN to exit ...");
            stop();
        } else
            System.out.println(msg);
    }

    private void start() throws IOException {
        streamOut = new DataOutputStream(socket.getOutputStream());
        if (thread == null) {
            client = new ChatClientThread(this, socket);
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
            System.out.println("Error closing ...");
        }
        client.close();
        client.interrupt();
    }

    public static void main(String args[]) {
        new ChatClient("localhost", 8878);
    }
}
