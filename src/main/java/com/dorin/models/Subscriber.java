package com.dorin.models;

public class Subscriber {
    private Integer id;
    private Channel channel;

    public Subscriber(Integer id) {
        this.id = id;
    }

    public Subscriber(Integer id, Channel channel) {
        this.id = id;
        this.channel = channel;
    }

    public Integer getId() {
        return id;
    }

    public Channel getChannel() {
        return channel;
    }
}
