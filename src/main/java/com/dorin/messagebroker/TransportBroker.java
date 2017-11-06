package com.dorin.messagebroker;

public interface TransportBroker {
    void listenToMessages();
    void sendToAll(String message);
    void close();
}
