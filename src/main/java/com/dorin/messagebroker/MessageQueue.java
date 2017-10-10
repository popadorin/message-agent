package com.dorin.messagebroker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueue {
    private static MessageQueue INSTANCE = null;
    private BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    private MessageQueue() {}

    public static MessageQueue getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MessageQueue();
        }

        return INSTANCE;
    }

    public BlockingQueue<String> getQueue() {
        return queue;
    }

    public void addMessage(String message) {
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            System.out.println("Interrupt exception occurred !");
        }
    }

    public String removeMessage() {
        return queue.poll();
    }

}
