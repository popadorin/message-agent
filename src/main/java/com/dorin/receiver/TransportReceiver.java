package com.dorin.receiver;

public interface TransportReceiver {
    String readFromBroker();
    void send(String message);
    void close();
}
