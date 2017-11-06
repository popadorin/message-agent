package com.dorin.receiver;

import com.dorin.messagebroker.Message;

public interface TransportReceiver {
    void listenFromBroker();
    void send(Message message);
    void close();
}
