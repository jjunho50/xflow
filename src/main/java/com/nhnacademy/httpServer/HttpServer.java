package com.nhnacademy.httpServer;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.nhnacademy.exception.InvalidRequestException;

public class HttpServer extends Thread {
    Logger logger = LogManager.getLogger(this.getClass().getSimpleName());
    Socket socket;

    public HttpServer(Socket socket) {
        this.socket = socket;
    }
    // 우리가 사실 dev만했는데 / 요청잘못하면 / 예외 처리 다른거좀 해주고
    // ep등 다른것도 좀 해주고

    // 밥먹고 와서 해야할거 -> ndoe 써야하고, json파일 파싱해서 id 밸류값만 추출하고 추가 그거에따른 예외처리
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

    // 디렉토리 내의 파일 목록을 가져오는 메서드
    public static Set<String> listFilesUsingJavaIO(String dir) {
        return Stream.of(new File(dir).listFiles()).filter(file -> !file.isDirectory())
                .map(File::getName).collect(Collectors.toSet());
    }

    // 디렉토리가 읽을 수 있는지 확인하는 메서드
    public boolean isReadable(String dir) {
        return (new File(dir).canRead());
    }

    // 클라이언트로부터 HTTP 요청을 받아오는 메서드
    public Request receiveRequest(BufferedInputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line = reader.readLine();
        if (line == null) {
            throw new InvalidRequestException();
        }
        String[] requestR = line.split("\\s");
        if (requestR.length != 3) {
            throw new InvalidRequestException();
        }
        if (!requestR[2].equals("HTTP/1.1")) {
            throw new InvalidRequestException();
        }

        Request request = new Request(requestR[0], requestR[1]);

        System.out.println(request.toString());

        return request;
    }

    // HTTP 응답을 클라이언트에게 보내는 메서드
    public void sendResponse(BufferedOutputStream outputStream, Request request)
            throws IOException, ParseException {
        Response response = new Response("", 404, "");
        String uri = request.getUri().replaceFirst("/", "");
        String access = uri.split("/")[0];
        String option = "";
        if (uri.split("/").length > 1) {
            option = uri.split("/", 2)[1];

        }
        System.out.println(uri);

        if (uri.equals("")) {
            // 루트 디렉토리 요청에 대한 응답
            response = new Response("HTTP/1.1", 200, "OK");
            response.addHeader("Content-Type", "text/plain");
            String line = listFilesUsingJavaIO(".").toString();
            response.addBody(line);
            response.addHeader("Content-Length", String.valueOf(line.length()));
        } else if (access.equals("dev")) {
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

        } else if (uri.equals("ep")) {
            Jsonparcing.creatJsonFile(uri);
            String jsonData = getJsonData(uri);
            response = new Response("HTTP/1.1", 200, "OK");
            response.addHeader("Content-Type", "application/json");
            response.addBody(jsonData);
            response.addHeader("Content-Length", String.valueOf(jsonData.length()));
        } else if (uri.equals("..")) {
            // 상위 디렉토리 요청에 대한 응답
            response = new Response("HTTP/1.1", 403, "Forbidden");
        } else if (!isReadable(uri)) {
            // 읽을 수 없는 디렉토리에 대한 응답
            response = new Response("HTTP/1.1", 403, "Forbidden");
            response.addHeader("Content-Type", "text/html");
            response.addHeader("Content-Length", "0");
        } else if (new File(uri).canRead()) {
            // 파일을 읽어 클라이언트에게 응답
            response = new Response("HTTP/1.1", 200, "OK");
            response.addHeader("Content-Type", "text/plain");
            BufferedReader reader = new BufferedReader(new FileReader(uri));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            response.addBody(builder.toString());
            response.addHeader("Content-Length", String.valueOf(builder.toString().length()));
            System.out.println(builder.toString());
        }

        outputStream.write(response.toString().getBytes());
        outputStream.flush();
    }

    @Override
    public void run() {
        try (BufferedInputStream socketIn = new BufferedInputStream(socket.getInputStream());
                BufferedOutputStream socketOut = new BufferedOutputStream(socket.getOutputStream())) {

            Request request = receiveRequest(socketIn);
            sendResponse(socketOut, request);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
