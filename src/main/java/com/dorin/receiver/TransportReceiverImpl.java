package com.dorin.receiver;

import com.dorin.transport.TransporterClient;
import org.apache.log4j.Logger;

import java.io.IOException;

public class TransportReceiverImpl implements TransportReceiver {
    private Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private TransporterClient transportClient;

    public TransportReceiverImpl() {
        LOGGER.info("Started");
        transportClient = new TransporterClient("localhost", 8878);
    }

    @Override
    public String readFromBroker() {
        return null;
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
}
