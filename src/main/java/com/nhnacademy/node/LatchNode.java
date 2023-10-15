package com.nhnacademy.node;

import com.nhnacademy.message.BooleanMessage;
import com.nhnacademy.message.Message;

public class LatchNode extends InputOutputNode {
    Message message;

    public LatchNode(String name) {
        super(name, 2, 1);
    }

    public LatchNode(String name, Message initMessage) {
        super(name, 2, 1);
        message = initMessage;
    }

    @Override
    void process() {
        if ((getInputWire(0) != null) && (getInputWire(0).hasMessage())) {
            Message signalMessage = getInputWire(0).get();
            if (signalMessage instanceof BooleanMessage) {
                if (((BooleanMessage) signalMessage).getPayload()) {
                    if (message != null) {
                        output(message);
                        message = null;
                    }
                } else {
                    if ((getInputWire(1) != null) && (getInputWire(1).hasMessage())) {
                        message = getInputWire(1).get();
                    }
                }
            }
        }
    }
}
