package com.dorin.messagebroker;

import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueue {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private static MessageQueue INSTANCE;
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
            LOGGER.error("Interrupt exception occurred !");
        }
    }

    public String removeMessage() {
        return queue.poll();
    }

}
