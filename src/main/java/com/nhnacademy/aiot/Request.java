package com.nhnacademy.aiot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    public static String CRLF = "\r\n";
    String method;
    String requestUri;
    String data;
    Map<String, String> headers;

    public Request(String method, String requestUri) {
        this.method = method;
        this.requestUri = requestUri;
        this.headers = new HashMap<>();
    }

    public Request(String method, String requestUri, String data) {
        this(method, requestUri);
        this.data = data;
    }

    public String getUri() {
        return this.requestUri;
    }

    public Request addHeader(String header) {
        String[] fields = header.split(":");

        if (fields.length != 2) {
            throw new InvalidHeaderException();
        }

        addHeader(fields[0].trim(), fields[1].trim());

        return this;
    }

    public Request addHeader(String key, String value) {
        headers.put(key, value);

        return this;
    }

    public Request setHost(String host) {
        addHeader("Host", host);

        return this;
    }

    public Request setHost(String host, int port) {
        addHeader("Host", host + ":" + port);

        return this;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String[] getLines() {
        List<String> lines = new ArrayList<>();

        lines.add(String.format("%s %s HTTP/1.1", method, requestUri));
        headers.forEach((key, value) -> lines.add(String.format("%s: %s", key, value)));
        if ((data != null) && (!headers.containsKey("Content-Length"))) {
            lines.add(String.format("%s: %d", "Content-Length", data.length()));
        }

        lines.add("");
        if (data != null) {
            lines.add(data);
        }

        return lines.toArray(new String[lines.size()]);
    }

    @Override
    public String toString() {
        String[] lines = getLines();

        StringBuilder builder = new StringBuilder();

        for (String line : lines) {
            builder.append(String.format("%s%s", line, CRLF));
        }

        return builder.toString();
    }
}