package com.dorin.sender;

import com.dorin.transport.TransporterClient;
import org.apache.log4j.Logger;

import java.io.IOException;

public class TransportSenderImpl implements TransportSender {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private TransporterClient transportClient;


    public TransportSenderImpl() {
        LOGGER.info("started");
        transportClient = new TransporterClient("localhost", 8878);
    }

    @Override
    public void sendToBroker(String message) {
        try {
            transportClient.send(message);
            LOGGER.info("Message successfully sent to broker");
        } catch (IOException e) {
            LOGGER.error("Problem on sending message to broker");
        }
    }

    @Override
    public void close() {
        transportClient.stop();
    }
}
