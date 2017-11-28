package com.dorin.models;

import java.io.Serializable;

public class MessageInfo implements Serializable {
    transient private Integer id;
    private Message message;
    private String channel;
    private ChannelType channelType;
    private CommandType commandType;

    public MessageInfo(Message message, String channel) {
        this.message = message;
        this.channel = channel;
    }

    public MessageInfo(String channel, ChannelType channelType, CommandType commandType) {
        this.channel = channel;
        this.channelType = channelType;
        this.commandType = commandType;
    }

    public MessageInfo(Message message, String channel, CommandType commandType) {
        this.message = message;
        this.channel = channel;
        this.commandType = commandType;
    }

    public MessageInfo(Message message, String channel, ChannelType channelType, CommandType commandType) {
        this.message = message;
        this.channel = channel;
        this.channelType = channelType;
        this.commandType = commandType;
    }

    public MessageInfo(String channel, CommandType commandType) {
        this.channel = channel;
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

    public ChannelType getChannelType() {
        return channelType;
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
                ", channelType=" + channelType +
                ", commandType=" + commandType +
                '}';
    }
}
