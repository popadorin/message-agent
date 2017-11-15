package com.dorin.models;

public enum ChannelType {
    PERSISTENT(1),
    NONPERSISTENT(2);

    private int channelTypeIndex;

    ChannelType(int channelTypeIndex) { this.channelTypeIndex = channelTypeIndex; }

    public static ChannelType getChannelType(int channelTypeIndex) {
        for (ChannelType ct : ChannelType.values()) {
            if (ct.channelTypeIndex == channelTypeIndex) return ct;
        }
        throw new IllegalArgumentException("Channel not found");
    }
}
