package com.dorin.receiver;

import com.dorin.transport.TransporterClient;

public class Receiver {
    public static void main(String[] args) {
        new TransporterClient("localhost", 8878);
    }
}
