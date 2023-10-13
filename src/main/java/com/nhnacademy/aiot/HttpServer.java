package com.nhnacademy.aiot;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

public class HttpServer extends Thread {
    Logger logger = LogManager.getLogger(this.getClass().getSimpleName());
    Socket socket;
    JSONObject object;

    public HttpServer(Socket socket) {
        this.socket = socket;
        object = new JSONObject();
    }

    public static Set<String> listFilesUsingJavaIO(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    public boolean isReadable(String dir) {
        return (new File(dir).canRead());
    }

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

    public Response receiveResponse(BufferedInputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line = reader.readLine();
        if (line == null) {
            throw new InvalidResponseException();
        }

        String[] responseHeader = line.split("\\s");
        if (responseHeader.length != 3) {
            throw new InvalidResponseException();
        }

        Response response = new Response(responseHeader[0], Integer.parseInt(responseHeader[1]), responseHeader[2]);

        while ((line = reader.readLine()) != null) {

            if (line.length() == 0) {
                break;
            }

            int delimiterIndex = line.indexOf(':', 0);
            if (delimiterIndex < 0) {
                throw new InvalidResponseException();
            }

            response.addHeader(line.substring(0, delimiterIndex).trim(), line.substring(delimiterIndex + 1).trim());
        }

        if (response.hasHeader("Content-Length")) {
            int length = Integer.parseInt(response.getHeader("Content-Length"));
            char[] buffer = new char[length];

            reader.read(buffer, 0, length);

            response.addBody(new String(buffer, 0, length));
        }

        return response;
    }

    public void sendResponse(BufferedOutputStream outputStream, Request request) throws IOException {
        Response response = new Response("", 404, "");
        String uri = request.getUri().replaceFirst("/", "");
        System.out.println(uri);

        if (uri.equals("")) {
            response = new Response("HTTP/1.1", 403, "Forbidden");
            response.addHeader("Content-Type", "text/plain");
            response.addHeader("Content-Length", "0");
        }

        else if (uri.equals("..")) {
            response = new Response("HTTP/1.1", 403, "Forbidden");
        }

        else if (!isReadable(uri)) {
            response = new Response("HTTP/1.1", 403, "Forbidden");
            response.addHeader("Content-Type", "text/html");
            response.addHeader("Content-Length", "0");

        }

        else if (uri.equals("dev")) {
            response = new Response("HTTP/1.1", 200, "OK");
            response.addHeader("Content-Type", "application/json");
            StringBuilder emsBody = new StringBuilder();

            try (Socket socket = new Socket("ems.nhnacademy.com", 1880);
                    BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
                StringBuilder builder = new StringBuilder();
                builder.append(String.format("%s %s %s", "GET", "/dev", "HTTP/1.1"));
                builder.append("\n\n");

                writer.write(builder.toString());
                writer.flush();

                String line;
                while ((line = socketIn.readLine()) != null)
                    emsBody.append(line + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            request = new Request("GET", "ems.nhnacademy.com");

            BufferedReader reader = new BufferedReader(new FileReader(uri));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }
            response.addBody(builder.toString());
            response.addHeader("Content-Length", String.valueOf(builder.toString().length()));
            System.out.println(builder.toString());
        }
        // else if () {

        // response = new Response("HTTP/1.1", 200, "OK");
        // // response.addHeader
        // }

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
            //
        }
    }

    public static void main(String[] args) {
        System.out.println((Arrays.toString(new File("..").list())));
        File f = new File(".vscode/settings.json");
        System.out.println(f.getAbsolutePath());
        System.out.println(f.canRead());
        System.out.println(f.getParent());

        // File parent = f.getParentFile();
        // System.out.println(parent.getAbsolutePath());

    }
}
