package com.nhnacademy.Node;

public class StandardOutNode extends Node implements OutputNode {
    @Override
    public void execute(FlowMessage message) {
        System.out.println("Standard Out Node: " + message.getData());
    }
}

