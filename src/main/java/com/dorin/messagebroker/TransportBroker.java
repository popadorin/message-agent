package com.dorin.messagebroker;

public interface TransportBroker {
    void listenToMessages();
    void sendToAll(Message message);
    void close();
}
