package com.nhnacademy.node;

import com.nhnacademy.message.LongMessage;
import com.nhnacademy.message.Message;
import com.nhnacademy.message.StringMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TerminalOutNode extends OutputNode {
    public TerminalOutNode() {
        super(1);
    }

    public TerminalOutNode(String name) {
        super(name, 1);
    }

    public TerminalOutNode(int count) {
        super(count);
    }

    @Override
    void preprocess() {
        setInterval(1);
    }

    @Override
    void process() {
        for (int i = 0; i < getInputWireCount(); i++) {
            if (getInputWire(i).hasMessage()) {
                log.trace("Message : {}", i);

                Message message = getInputWire(i).get();

                if (message instanceof StringMessage) {
                    System.out.println(((StringMessage) message).getPayload());
                } else if (message instanceof LongMessage) {
                    System.out.println(((LongMessage) message).getPayload());
                } else {
                    System.out.println(message.toString());
                }

            }
        }
    }
}
