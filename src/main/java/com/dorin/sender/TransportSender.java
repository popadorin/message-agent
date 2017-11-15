package com.dorin.sender;

import com.dorin.models.MessageInfo;

public interface TransportSender {
    void sendToBroker(MessageInfo messageInfo);
    void close();
}
