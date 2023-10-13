package com.nhnacademy.message;

import java.util.Arrays;
import java.util.UUID;

public class TcpRequestMessage extends Message {
    byte[] payload;
    UUID senderId;

    public TcpRequestMessage(UUID id, byte[] payload, int length) {
        this.payload = Arrays.copyOf(payload, length);
        senderId = id;
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
