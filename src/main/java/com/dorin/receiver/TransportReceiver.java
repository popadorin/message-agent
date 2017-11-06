package com.dorin.receiver;

public interface TransportReceiver {
    void listenFromBroker();
    void send(String message);
    void close();
}
