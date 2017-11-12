package com.dorin.sender;

import com.dorin.helpers.MessageInfo;

public interface TransportSender {
    void sendToBroker(MessageInfo messageInfo);
    void close();
}
