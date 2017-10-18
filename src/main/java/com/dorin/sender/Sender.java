package com.dorin.sender;

import com.dorin.transport.TransporterClient;
import org.apache.log4j.Logger;

public class Sender {
    private static final Logger LOGGER = Logger.getLogger(Sender.class);

    public static void main(String[] args) {
        LOGGER.info("Sender started");
        new TransporterClient("localhost", 8878);
    }
}
