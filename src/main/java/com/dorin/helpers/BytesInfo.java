package com.dorin.helpers;

public class BytesInfo {
    private Integer id;
    private byte[] objectBytes;

    public BytesInfo(Integer id, byte[] objectBytes) {
        this.id = id;
        this.objectBytes = objectBytes;
    }

    public Integer getId() {
        return id;
    }

    public byte[] getObjectBytes() {
        return objectBytes;
    }
}
