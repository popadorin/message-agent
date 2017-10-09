package com.dorin.sender;

import com.dorin.transport.TransporterClient;

public class Sender {
    public static void main(String[] args) {
        new TransporterClient("localhost", 8878);
    }
}
