package com.dorin.helpers;

import com.dorin.models.Message;

public class MessageInfo {
    private Integer id;
    private Message message;

    public MessageInfo(Integer id, Message message) {
        this.id = id;
        this.message = message;
    }

    public Integer getId() {
        return id;
    }

    public Message getMessage() {
        return message;
    }
}
