package com.dorin.models;

import java.io.Serializable;

public class MessageInfo implements Serializable {
    transient private Integer id;
    private Message message;
    private String channel;
    private CommandType commandType;

    public MessageInfo(Message message, CommandType commandType) {
        this.message = message;
        this.commandType = commandType;
    }

    public Integer getId() {
        return id;
    }

    public Message getMessage() {
        return message;
    }

    public String getChannel() {
        return channel;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MessageInfo{" +
                "id=" + id +
                ", message=" + message +
                ", channel=" + channel +
                ", commandType=" + commandType +
                '}';
    }
}
