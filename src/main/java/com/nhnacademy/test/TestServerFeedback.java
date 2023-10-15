package com.nhnacademy.test;

import com.nhnacademy.node.MessageFeedback;
import com.nhnacademy.node.TCPServer;
import com.nhnacademy.node.TerminalOutNode;
import com.nhnacademy.wire.BufferedWire;
import com.nhnacademy.wire.Wire;

public class TestServerFeedback {

    public static void main(String[] args) {
        TCPServer server = new TCPServer("server");
        MessageFeedback a = new MessageFeedback();
        Wire wire1 = new BufferedWire();

        server.connectOutputWire(0, wire1);
        a.connectInputWire(0, wire1);

        a.start();
        server.start();
    }
}
