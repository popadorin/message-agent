package com.dorin.messagebroker;

import com.dorin.transport.TransportServer;
import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;

public class TransportBrokerImpl implements TransportBroker {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private TransportServer transportServer;
    private boolean status;


    public TransportBrokerImpl() {
        LOGGER.info("Started");
        new Thread(() -> transportServer = new TransportServer(8878)).start();
    }

    @Override
    public void sendToAll(String message) {
        transportServer.sendToAllClients(message);
    }

    public BlockingQueue<String> getMessages() {
        return transportServer.getMessages();
    }

    @Override
    public void close() {
        transportServer.stop();
    }
}
