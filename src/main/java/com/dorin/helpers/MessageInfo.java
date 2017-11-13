package com.dorin.helpers;

import com.dorin.models.Channel;
import com.dorin.models.CommandType;
import com.dorin.models.Message;

import java.io.Serializable;

public class MessageInfo implements Serializable {
    transient private Integer id;
    private Message message;
    private Channel channel;
    private CommandType commandType;

    public MessageInfo(Message message, Channel channel, CommandType commandType) {
        this.message = message;
        this.channel = channel;
        this.commandType = commandType;
    }

    public Integer getId() {
        return id;
    }

    public Message getMessage() {
        return message;
    }

    public Channel getChannel() {
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
