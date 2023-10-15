package com.nhnacademy.node;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.ServerSocket;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.github.f4b6a3.uuid.UuidCreator;
import com.nhnacademy.exception.InvalidRequestException;
import com.nhnacademy.httpServer.Jsonparcing;
import com.nhnacademy.httpServer.Response;
import com.nhnacademy.message.Message;
import com.nhnacademy.message.TcpRequestMessage;
import com.nhnacademy.message.TcpResponseMessage;
import com.nhnacademy.node.TCPServer.Handler;

public class MessageFeedback extends InputOutputNode {

    public static String getJsonData(String uri) {
        try {
            // output.json 파일의 경로
            String filePath = "src/main/java/com/nhnacademy/dataFile/" + uri + ".json";

            // 파일 읽기
            StringBuilder jsonData = new StringBuilder();
            try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = fileReader.readLine()) != null) {
                    jsonData.append(line);
                }
            }

            return jsonData.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static JSONArray getJson(String uri) throws IOException, ParseException {

        // output.json 파일의 경로
        String filePath = "src/main/java/com/nhnacademy/dataFile/" + uri + ".json";

        // 파일 읽기
        JSONParser parser = new JSONParser();

        Reader reader = new FileReader(filePath);
        // System.out.println(parser.parse(reader).getClass());
        JSONArray object = (JSONArray) parser.parse(reader);

        System.out.println(object.getClass());

        return object;

    }

    public MessageFeedback() {
        super(1, 1);
    }

    MessageFeedback(int inCount, int outCount) {
        super(inCount, outCount);
    }

    Thread messageReceiver;
    Response response;
    UUID id;

    public String receiveRequest(TcpRequestMessage message) throws IOException {
        byte[] data = message.getPayload();

        String request = new String(data);

        String line = request.split("\n")[0];
        if (line == null) {
            throw new InvalidRequestException();
        }
        String[] requestLine = line.split("\\s");
        if (requestLine.length != 3) {
            throw new InvalidRequestException();
        }
        if (!requestLine[2].equals("HTTP/1.1")) {
            throw new InvalidRequestException();
        }

        System.out.println(request);

        return line;
    }

    @Override
    void preprocess() {
        setInterval(1);
    }

    @Override
    void process() {
        try {
            for (int i = 0; i < getInputWireCount(); i++) {
                if ((getInputWire(i) != null) && getInputWire(i).hasMessage()) {
                    Message message = getInputWire(i).get();
                    if (message instanceof TcpRequestMessage) {
                        TcpRequestMessage request = (TcpRequestMessage) message;

                        String line = receiveRequest(request);
                        String uri = line.split(" ")[1].replaceFirst("/", "");
                        String access = uri.split("/")[0];
                        String option = "";
                        if (uri.split("/").length > 1) {
                            option = uri.split("/", 2)[1];
                        }
                        System.out.println(uri);

                        if (access.equals("dev")) {
                            Jsonparcing.creatJsonFile(access);
                            JSONArray jsonArray = getJson(access);
                            String jsonData = getJsonData(access);
                            if (uri.equals(access)) {
                                response = new Response("HTTP/1.1", 200, "OK");
                                response.addHeader("Content-Type", "application/json");
                                response.addBody(jsonData);
                                response.addHeader("Content-Length", String.valueOf(jsonData.length()));
                            } else if (jsonArray.stream().anyMatch(obj -> ((JSONObject) obj).containsKey("id"))) {
                                response = new Response("HTTP/1.1", 200, "OK");
                                response.addHeader("Content-Type", "application/json");
                                Jsonparcing.creatJsonFile(uri);
                                jsonData = getJsonData(uri);
                                response.addBody(jsonData);
                                response.addHeader("Content-Length", String.valueOf(jsonData.length()));
                            } else {
                                response = new Response("HTTP/1.1", 404, "NOT FOUND");
                            }
                        }
                        id = UuidCreator.getTimeBased();
                        System.out.println(response.toString());
                        output(new TcpResponseMessage(id, response.toString().getBytes()));
                    }
                }
            }
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // try {
    // for (int i = 0; i < getInputWireCount(); i++) {
    // if ((getInputWire(i) != null) && getInputWire(i).hasMessage()) {
    // Message message = getInputWire(i).get();
    // if (message instanceof TcpRequestMessage) {
    // request = (TcpRequestMessage) message;
    // }
    // }
    // }
    // Thread.sleep(1);
    // } catch (InterruptedException e) {
    // Thread.currentThread().interrupt();
    // } catch (InvalidRequestException e) {
    // e.printStackTrace();
    // }

    // try {
    // if (!receiveRequest(request)) {
    // throw new InvalidRequestException();
    // }
    // String uri = line.split(" ")[1].replaceFirst("/", "");
    // String access = uri.split("/")[0];
    // String option = "";
    // if (uri.split("/").length > 1) {
    // option = uri.split("/", 2)[1];
    // }
    // System.out.println(uri);

    // if (access.equals("dev")) {
    // Jsonparcing.creatJsonFile(access);
    // JSONArray jsonArray = getJson(access);
    // String jsonData = getJsonData(access);
    // if (uri.equals(access)) {
    // response = new Response("HTTP/1.1", 200, "OK");
    // response.addHeader("Content-Type", "application/json");
    // response.addBody(jsonData);
    // response.addHeader("Content-Length", String.valueOf(jsonData.length()));
    // } else if (jsonArray.stream().anyMatch(obj -> ((JSONObject)
    // obj).containsKey("id"))) {
    // response = new Response("HTTP/1.1", 200, "OK");
    // response.addHeader("Content-Type", "application/json");
    // Jsonparcing.creatJsonFile(uri);
    // jsonData = getJsonData(uri);
    // response.addBody(jsonData);
    // response.addHeader("Content-Length", String.valueOf(jsonData.length()));
    // } else {
    // response = new Response("HTTP/1.1", 404, "NOT FOUND");
    // }
    // }
    // id = UuidCreator.getTimeBased();
    // System.out.println(response.toString());
    // output(new TcpResponseMessage(id, response.toString().getBytes()));
    // } catch (IOException e) {
    // e.printStackTrace();
    // } catch (InvalidRequestException e) {
    // //
    // } catch (ParseException e) {
    // e.printStackTrace();
    // } catch (NullPointerException e) {
    // //
    // }
    // }
}
