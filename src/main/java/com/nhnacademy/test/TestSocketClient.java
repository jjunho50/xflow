package com.nhnacademy.test;

import com.nhnacademy.node.TCPClient;
import com.nhnacademy.node.TerminalInNode;
import com.nhnacademy.node.TerminalOutNode;
import com.nhnacademy.wire.BufferedWire;
import com.nhnacademy.wire.Wire;

public class TestSocketClient {
    public static void main(String[] args) {
        TCPClient client = new TCPClient("client");
        TerminalInNode in = new TerminalInNode();
        TerminalOutNode out = new TerminalOutNode();

        Wire wire0 = new BufferedWire();
        Wire wire1 = new BufferedWire();

        in.connectOutputWire(0, wire0);
        client.connectInputWire(0, wire0);
        client.connectOutputWire(0, wire1);
        out.connectInputWire(0, wire1);

        in.start();
        out.start();
        client.start();
    }
}