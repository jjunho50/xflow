package com.nhnacademy.httpServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.nhnacademy.exception.InvalidHeaderException;

public class Request {
    public static String CRLF = "\r\n";
    String method;
    String requestUri;
    String data;
    Map<String, String> headers;

    // 생성자: HTTP 요청의 메서드와 URI를 받아 인스턴스를 초기화합니다.
    public Request(String method, String requestUri) {
        this.method = method;
        this.requestUri = requestUri;
        this.headers = new HashMap<>();
    }

    // 생성자: HTTP 요청의 메서드, URI, 데이터를 받아 인스턴스를 초기화합니다.
    public Request(String method, String requestUri, String data) {
        this(method, requestUri);
        this.data = data;
    }

    // URI를 반환하는 메서드
    public String getUri() {
        return this.requestUri;
    }

    // 헤더를 추가하는 메서드: "key: value" 형식의 문자열을 받아 파싱하여 헤더에 추가합니다.
    public Request addHeader(String header) {
        String[] fields = header.split(":");

        if (fields.length != 2) {
            throw new InvalidHeaderException();
        }

        addHeader(fields[0].trim(), fields[1].trim());

        return this;
    }

    // 헤더를 추가하는 메서드: key와 value를 직접 받아 헤더에 추가합니다.
    public Request addHeader(String key, String value) {
        headers.put(key, value);

        return this;
    }

    // Host 헤더를 설정하는 메서드
    public Request setHost(String host) {
        addHeader("Host", host);

        return this;
    }

    // Host와 포트를 설정하는 메서드
    public Request setHost(String host, int port) {
        addHeader("Host", host + ":" + port);

        return this;
    }

    // 데이터를 설정하는 메서드
    public void setData(String data) {
        this.data = data;
    }

    // HTTP 요청의 각 줄을 배열로 반환하는 메서드
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

    // 객체를 문자열로 변환하는 메서드: HTTP 요청의 각 줄을 CRLF로 구분하여 반환합니다.
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
