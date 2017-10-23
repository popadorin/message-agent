package com.dorin.sender;

public interface TransportSender {
    void send(String message);
    void close();
}
