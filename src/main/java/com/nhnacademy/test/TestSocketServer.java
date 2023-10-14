package com.nhnacademy.test;

import com.nhnacademy.node.TCPServer;
import com.nhnacademy.node.TerminalOutNode;
import com.nhnacademy.wire.BufferedWire;
import com.nhnacademy.wire.Wire;

public class TestSocketServer {
    public static void main(String[] args) {
        TCPServer server = new TCPServer("server");
        TerminalOutNode out = new TerminalOutNode();

        Wire wire1 = new BufferedWire();

        server.connectOutputWire(0, wire1);
        out.connectInputWire(0, wire1);

        out.start();
        server.start();
    }
}
