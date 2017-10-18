package com.dorin.receiver;

import com.dorin.transport.TransporterClient;
import org.apache.log4j.Logger;

public class Receiver {
    private static final Logger LOGGER = Logger.getLogger(Receiver.class);

    public static void main(String[] args) {
        LOGGER.info("Receiver started");
        new TransporterClient("localhost", 8878);
    }
}
