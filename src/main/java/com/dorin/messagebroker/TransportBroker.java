package com.dorin.messagebroker;

import com.dorin.models.Message;

public interface TransportBroker {
    void listenToMessages();
    void sendToAll(Message message);
    void send(Integer id, Message message);
    void close();
}
