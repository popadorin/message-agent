package com.dorin.messagebroker;

public interface TransportBroker {
    void sendToAll(String message);
    void close();
}