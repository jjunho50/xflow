package com.nhnacademy.total;

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

    // 생성자: HTTP 응답의 버전, 상태 코드, 이유를 받아 인스턴스를 초기화합니다.
    public Response(String version, int status, String reason) {
        this.version = version;
        this.status = status;
        this.reason = reason;
        this.body = "";
        headers = new HashMap<>();
    }

    // 상태 코드를 반환하는 메서드
    public int getStatus() {
        return status;
    }

    // 헤더를 추가하는 메서드
    public void addHeader(String key, String value) {
        headers.put(key.toLowerCase(), value);
    }

    // 특정 헤더가 있는지 확인하는 메서드
    public boolean hasHeader(String key) {
        return headers.containsKey(key.toLowerCase());
    }

    // 특정 헤더의 값을 반환하는 메서드
    public String getHeader(String key) {
        return headers.get(key.toLowerCase());
    }

    // 응답 본문을 추가하는 메서드
    public void addBody(String body) {
        this.body = body;
    }

    // 응답 본문을 반환하는 메서드
    public String getBody() {
        return body;
    }

    // 응답에 본문이 있는지 확인하는 메서드
    public boolean hasBody() {
        return body != null;
    }

    // HTTP 응답의 각 줄을 배열로 반환하는 메서드
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

    // 객체를 문자열로 변환하는 메서드: HTTP 응답의 각 줄을 CRLF로 구분하여 반환합니다.
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
