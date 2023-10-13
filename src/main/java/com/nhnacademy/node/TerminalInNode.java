package com.nhnacademy.node;

import java.util.Scanner;

import com.nhnacademy.message.StringMessage;

public class TerminalInNode extends InputNode {
    Scanner scanner;

    public TerminalInNode() {
        this(1);
    }

    public TerminalInNode(int count) {
        super(count);
    }

    @Override
    void preprocess() {
        scanner = new Scanner(System.in);
        setInterval(1);
    }

    @Override
    void process() {
        String line = scanner.nextLine();
        StringMessage message = new StringMessage(line);

        output(message);
    }

    @Override
    void postprocess() {
        scanner = null;
    }
}
