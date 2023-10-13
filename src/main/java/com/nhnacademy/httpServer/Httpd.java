package com.nhnacademy.httpServer;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Httpd {
    public static void main(String[] args) throws InterruptedException {
        int port = 8080;


        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();

                HttpServer server = new HttpServer(socket);
                server.start();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
