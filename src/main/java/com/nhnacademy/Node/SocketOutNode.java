package com.nhnacademy.Node;

import java.io.PrintWriter;
import java.net.Socket;

public class SocketOutNode extends Node implements OutputNode {
    private Socket socket;

    public SocketOutNode(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void execute(FlowMessage message) {
        try (PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
            System.out.println("Sending data to Socket Out Node...");
            writer.println(message.getData());
        } catch (Exception e) {
            getLogger().severe("Error writing data to socket: " + e.getMessage());
            throw new RuntimeException("Error writing data to socket", e);
        }
    }

    public void setSocket(Socket clientSocket) {


    }
}

