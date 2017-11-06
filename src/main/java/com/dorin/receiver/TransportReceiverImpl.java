package com.dorin.receiver;

import com.dorin.transport.TransporterClient;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class TransportReceiverImpl extends Observable implements Observer, TransportReceiver {
    private Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private TransporterClient transportClient;

    public TransportReceiverImpl() {
        LOGGER.info("Started");
        new Thread(() -> transportClient = new TransporterClient("localhost", 8878)).start();
    }

    @Override
    public void listenFromBroker() {
        transportClient.addObserver(this);
    }

    @Override
    public void send(String message) {
        try {
            transportClient.send(message);
        } catch (IOException e) {
            LOGGER.error("Problem on sending message to broker");
        }
    }

    @Override
    public void close() {
        transportClient.stop();
    }

    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }
}
