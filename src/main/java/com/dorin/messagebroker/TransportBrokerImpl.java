package com.dorin.messagebroker;

import com.dorin.transport.TransportServer;
import org.apache.log4j.Logger;

import java.util.Observable;
import java.util.Observer;

public class TransportBrokerImpl extends Observable implements TransportBroker, Observer {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private TransportServer transportServer;

    public TransportBrokerImpl() {
        LOGGER.info("Started");
        new Thread(() -> transportServer = new TransportServer(8878)).start();
    }

    @Override
    public void listenToMessages() {
        transportServer.addObserver(this);
    }

    @Override
    public void sendToAll(String message) {
        transportServer.sendToAllClients(message);
    }

    @Override
    public void close() {
        transportServer.stop();
    }

    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }
}
