package com.dorin.sender;

import com.dorin.models.Message;

public interface TransportSender {
    void sendToBroker(Message message);
    void close();
}
