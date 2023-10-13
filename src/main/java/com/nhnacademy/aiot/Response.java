package com.nhnacademy.aiot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Response {
    static String newLine = "\r\n";
    String version;
    int status;
    String reason;
    Map<String, String> headers;
    String body;

    public Response(String version, int status, String reason) {
        this.version = version;
        this.status = status;
        this.reason = reason;
        this.body = "";
        headers = new HashMap<>();
    }

    public int getStatus() {
        return status;
    }

    public void addHeader(String key, String value) {
        headers.put(key.toLowerCase(), value);
    }

    public boolean hasHeader(String key) {
        return headers.containsKey(key.toLowerCase());
    }

    public String getHeader(String key) {
        return headers.get(key.toLowerCase());
    }

    public void addBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public boolean hasBody() {
        return body != null;
    }

    public String[] getLines() {
        List<String> lines = new ArrayList<>();

        lines.add(String.format("%s %d %s", version, status, reason));
        headers.forEach((key, value) -> lines.add(String.format("%s: %s", key, value)));
        lines.add("");
        if (hasBody()) {
            lines.add(getBody());
        }

        return lines.toArray(new String[lines.size()]);
    }

    @Override
    public String toString() {
        String[] lines = getLines();

        StringBuilder builder = new StringBuilder();

        for (String line : lines) {
            builder.append(String.format("%s%s", line, newLine));
        }

        return builder.toString();
    }
}