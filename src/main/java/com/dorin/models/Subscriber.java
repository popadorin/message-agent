package com.dorin.models;

import java.util.Objects;

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

    @Override
    public String toString() {
        return "Subscriber{" +
                "id=" + id +
                ", channel='" + channel + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subscriber)) return false;
        Subscriber that = (Subscriber) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(channel, that.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, channel);
    }
}
