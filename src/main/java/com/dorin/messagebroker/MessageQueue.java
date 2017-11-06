package com.dorin.messagebroker;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueue implements Serializable {
    private transient final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();

    public void push(Message message) {
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            LOGGER.error("Interrupt exception occurred !");
        }
    }

    public Message pop() {
        return queue.poll();
    }

    public BlockingQueue<Message> getQueue() {
        return queue;
    }

}
