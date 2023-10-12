package com.nhnacademy.Node;

import java.util.Scanner;

public class StandardInNode extends Node implements InputNode {
    @Override
    public FlowMessage execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter data for Standard In Node: ");
        String inputData = scanner.nextLine();
        return new FlowMessage(inputData);
    }
}
