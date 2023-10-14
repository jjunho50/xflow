package com.nhnacademy.node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import com.github.f4b6a3.uuid.UuidCreator;
import com.nhnacademy.message.Message;
import com.nhnacademy.message.StringMessage;
import com.nhnacademy.message.TcpRequestMessage;
import com.nhnacademy.message.TcpResponseMessage;

public class TCPClient extends InputOutputNode {
    static class Handler implements Runnable {
        UUID id;
        Thread thread;
        Socket socket;
        BufferedInputStream inputStream;
        BufferedOutputStream outputStream;
        byte[] buffer;
        BiConsumer<byte[], Integer> callback;

        public Handler(Socket socket) {
            id = UuidCreator.getTimeBased();
            thread = new Thread(this);
            buffer = new byte[100000];
            this.socket = socket;
        }

        public UUID getId() {
            return id;
        }

        public void setCallback(BiConsumer<byte[], Integer> callback) {
            this.callback = callback;
        }

        public void start() {
            thread.start();
        }

        public void stop() {
            try {
                inputStream.close();
            } catch (IOException ignore) {

            } finally {
                thread.interrupt();
            }
        }

        public void write(byte[] data) {
            try {
                outputStream.write(data);
                outputStream.flush();
            } catch (IOException ignore) {

            }
        }

        @Override
        public void run() {
            try {
                inputStream = new BufferedInputStream(socket.getInputStream());
                outputStream = new BufferedOutputStream(socket.getOutputStream());

                while (!Thread.currentThread().isInterrupted()) {
                    int length = inputStream.read(buffer);

                    callback.accept(buffer, length);
                }

                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
            } finally {
                inputStream = null;
                outputStream = null;
            }
        }

    }

    int port = 8888;
    Socket socket;
    Map<UUID, Handler> handlerMap;
    Thread messageSender;

    public TCPClient(String name) {
        super(name, 1, 1);
        handlerMap = new HashMap<>();
    }

    Handler getHandler(UUID id) {
        return handlerMap.get(id);
    }

    @Override
    void preprocess() {
        try {
            // serverSocket = new ServerSocket(port);
            socket = new Socket("localhost", port);

            messageSender = new Thread(() -> {

                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        for (int i = 0; i < getInputWireCount(); i++) {
                            if ((getInputWire(i) != null) && getInputWire(i).hasMessage()) {
                                Message message = getInputWire(i).get();
                                if (message instanceof TcpRequestMessage) {
                                    TcpRequestMessage request = (TcpRequestMessage) message;
                                    Handler handler = getHandler(request.getSenderId());

                                    handler.write(request.getPayload());
                                } else if (message instanceof StringMessage) {
                                    output(message);
                                }
                            }
                        }
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            messageSender.start();
        } catch (IOException e) {
            log.error(e.getMessage());
            stop();
        }
    }

    @Override
    void process() {
        try {
            socket = new Socket("localhost", port);

            Handler handler = new Handler(socket);

            handler.setCallback((data, length) -> {
                System.out.println(data);
                output(new TcpResponseMessage(handler.getId(), data));
            });

            handler.start();

            handlerMap.put(handler.getId(), handler);

        } catch (IOException e) {
            log.error(e.getMessage());
            stop();
        }

    }
}
