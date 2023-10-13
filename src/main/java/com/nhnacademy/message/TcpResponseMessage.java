package com.nhnacademy.message;

import java.util.Arrays;
import java.util.UUID;

public class TcpResponseMessage extends Message {
    byte[] payload;
    UUID senderId;

    public TcpResponseMessage(UUID senderId, byte[] payload) {
        this.payload = Arrays.copyOf(payload, payload.length);
        this.senderId = senderId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public byte[] getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return Arrays.toString(payload);
    }
}
