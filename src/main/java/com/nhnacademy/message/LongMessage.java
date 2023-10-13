package com.nhnacademy.message;

public class LongMessage extends Message {
    long payload;

    public LongMessage(long payload) {
        this.payload = payload;
    }

    public long getPayload() {
        return payload;
    }
}
