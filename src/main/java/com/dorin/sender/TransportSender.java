package com.dorin.sender;

public interface TransportSender {
    void sendToBroker(String message);
    void close();
}
