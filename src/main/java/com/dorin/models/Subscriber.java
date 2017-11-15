package com.dorin.models;

public class Subscriber {
    private Integer id;
    private String channel;

    public Subscriber(Integer id) {
        this.id = id;
    }

    public Subscriber(Integer id, String channel) {
        this.id = id;
        this.channel = channel;
    }

    public Integer getId() {
        return id;
    }

    public String getChannel() {
        return channel;
    }
}
