package com.dorin.receiver;

import com.dorin.models.Message;
import com.dorin.transport.TransporterClient;
import org.apache.commons.lang3.SerializationUtils;
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
    public void send(Message message) {
        try {
            byte[] serializedMessage = SerializationUtils.serialize(message);
            transportClient.send(serializedMessage);
            LOGGER.info("Message successfully sent message to broker");
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
        Message message =  SerializationUtils.deserialize((byte[]) arg);

        setChanged();
        notifyObservers(message);
    }
}
