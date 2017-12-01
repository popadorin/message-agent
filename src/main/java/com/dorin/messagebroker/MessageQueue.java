package com.dorin.messagebroker;

import com.dorin.models.Message;
import com.dorin.models.MessageInfo;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueue extends Observable implements Serializable {
    private transient final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
    private String channel;

    public MessageQueue(String channel) {
        this.channel = channel;
    }

    public void push(Message message) {
        try {
            queue.put(message);
            setChanged();
            notifyObservers(new MessageInfo(message, channel));
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
