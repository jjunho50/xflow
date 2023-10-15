package com.nhnacademy.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import com.nhnacademy.message.Message;
import com.nhnacademy.message.StringMessage;
import com.nhnacademy.wire.Wire;

public class SocketInNode extends InputNode {

    private Socket socket;

    public SocketInNode(String name, int count, int port, Socket socket) {
        super(name, count);
    }

    public void activate() {
        try {
            // 클라이언트 연결을 대기하고 연결이 성립되면 데이터를 읽음
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                // 데이터를 읽어서 flow 메시지로 만들고 Wire를 통해 전송
                Message message = new StringMessage(inputLine);

                for (Wire wire : outputWires) {
                    if (wire != null) {
                        wire.put(message);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cleanUp();
        }
    }

    public void cleanUp() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}