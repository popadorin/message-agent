package com.dorin.sender;

import com.dorin.messagebroker.Message;

public interface TransportSender {
    void sendToBroker(Message message);
    void close();
}
