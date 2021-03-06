package com.dorin.sender;

import com.dorin.models.MessageInfo;
import com.dorin.transport.TransporterClient;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;

import java.io.IOException;

public class TransportSenderImpl implements TransportSender {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private TransporterClient transportClient;


    public TransportSenderImpl() {
        LOGGER.info("Started");
        transportClient = new TransporterClient("localhost", 8878);
    }

    @Override
    public void sendToBroker(MessageInfo messageInfo) {
        try {
            byte[] serializedMessage = SerializationUtils.serialize(messageInfo);
            transportClient.send(serializedMessage);
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
