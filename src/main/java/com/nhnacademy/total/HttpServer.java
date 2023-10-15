package com.nhnacademy.total;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nhnacademy.exception.InvalidRequestException;

public class HttpServer extends Thread {
    private static final Logger logger = LogManager.getLogger(HttpServer.class.getSimpleName());
    private Socket socket;

    public HttpServer(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedInputStream socketIn = new BufferedInputStream(socket.getInputStream());
                BufferedOutputStream socketOut =
                        new BufferedOutputStream(socket.getOutputStream())) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(socketIn));
            String line = reader.readLine();

            if (line != null) {
                String[] requestR = line.split("\\s");
                if (requestR.length == 3 && requestR[2].equals("HTTP/1.1")) {
                    Request request = new Request(requestR[0], requestR[1]);
                    System.out.println(request.toString());

                    handleRequest(socketOut, request);
                } else {
                    throw new InvalidRequestException();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(BufferedOutputStream outputStream, Request request)
            throws IOException {
        Response response;
        String uri = request.getUri().replaceFirst("/", "");

        if (uri.equals("")) {
            response = handleRootDirectory();
        } else if (uri.equals("dev")) {
            response = handleDevRequest(uri);
        } else if (uri.equals("..")) {
            response = new Response("HTTP/1.1", 403, "Forbidden");
        } else if (!isReadable(uri)) {
            response = handleUnreadableDirectory();
        } else {
            response = handleReadableFile(uri);
        }

        outputStream.write(response.toString().getBytes());
        outputStream.flush();
    }

    private Response handleRootDirectory() {
        Response response;
        Set<String> files = listFilesUsingJavaIO(".");
        String line = files.toString();
        response = new Response("HTTP/1.1", 200, "OK");
        response.addHeader("Content-Type", "text/plain");
        response.addBody(line);
        response.addHeader("Content-Length", String.valueOf(line.length()));
        return response;
    }

    private Response handleDevRequest(String uri) {
        Response response;
        CreatJson.creatJsonFile(uri);
        String jsonData = CreatJson.getJsonData(uri);
        response = new Response("HTTP/1.1", 200, "OK");
        response.addHeader("Content-Type", "application/json");
        response.addBody(jsonData);
        response.addHeader("Content-Length", String.valueOf(jsonData.length()));
        return response;
    }

    private Response handleUnreadableDirectory() {
        Response response = new Response("HTTP/1.1", 403, "Forbidden");
        response.addHeader("Content-Type", "text/html");
        response.addHeader("Content-Length", "0");
        return response;
    }

    private Response handleReadableFile(String uri) throws IOException {
        Response response;
        if (new File(uri).canRead()) {
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
        } else {
            response = new Response("HTTP/1.1", 403, "Forbidden");
        }
        return response;
    }

    public static Set<String> listFilesUsingJavaIO(String dir) {
        return Stream.of(new File(dir).listFiles()).filter(file -> !file.isDirectory())
                .map(File::getName).collect(Collectors.toSet());
    }

    private boolean isReadable(String dir) {
        return new File(dir).canRead();
    }
}

