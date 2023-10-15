package com.nhnacademy.node;

import java.util.Scanner;
import com.nhnacademy.message.Message;
import com.nhnacademy.message.StringMessage;

public class StandardInNode extends InputNode {

    private Scanner scanner;

    public StandardInNode(String name, int count) {
        super(name, count);
        scanner = new Scanner(System.in);
    }

    public void activate() {
        String input = scanner.nextLine();
        Message message = new StringMessage(input);
        output(message);
    }
}
