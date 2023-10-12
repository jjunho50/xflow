package com.nhnacademy.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketInNode extends Node implements InputNode {
    private Socket socket;

    public SocketInNode(Socket socket) {
        this.socket = socket;
    }

    @Override
    public FlowMessage execute() {
        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            System.out.println("Waiting for data from Socket In Node...");
            String inputData = reader.readLine();
            return new FlowMessage(inputData);
        } catch (IOException e) {
            getLogger().severe("Error reading data from socket: " + e.getMessage());
            throw new RuntimeException("Error reading data from socket", e);
        }
    }

    public void setSocket(Socket clientSocket) {


    }
}

