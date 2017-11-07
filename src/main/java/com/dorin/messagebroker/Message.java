package com.dorin.messagebroker;

import java.io.Serializable;

public class Message implements Serializable {
    private CommandType commandType;
    private String content;

    public Message(CommandType commandType, String content) {
        this.commandType = commandType;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    @Override
    public String toString() {
        return "Message{" +
                "commandType=" + commandType +
                ", content='" + content + '\'' +
                '}';
    }
}
