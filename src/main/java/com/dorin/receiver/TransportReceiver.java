package com.dorin.receiver;

import com.dorin.helpers.MessageInfo;

public interface TransportReceiver {
    void listenFromBroker();
    void send(MessageInfo messageInfo);
    void close();
}
