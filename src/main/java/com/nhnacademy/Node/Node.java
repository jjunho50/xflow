package com.nhnacademy.Node;

import java.util.UUID;
import java.util.logging.Logger;

// 추상 클래스 Node 선언
public abstract class Node {
    private static Integer count = 0; // count 변수 초기화
    private final String id; // id 변수 선언
    private String name; // name 변수 선언
    private Logger logger; // logger 변수 선언

    // 기본 생성자
    public Node() {
        this.id = generateUniqueId(); // 고유한 ID 생성 및 할당
        this.name = generateDefaultName(); // 기본 이름 생성 및 할당
        this.logger = Logger.getLogger(Node.class.getName()); // Logger 객체 생성 및 할당
        count++; // count 증가
    }

    // name 변수의 getter 및 setter 메서드
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // id 변수의 getter 메서드
    public String getId() {
        return id;
    }

    // count 변수의 getter 메서드
    public static Integer getCount() {
        return count;
    }

    // logger 변수의 getter 및 setter 메서드
    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    // 고유한 ID를 생성하는 도우미 메서드
    private String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    // 기본 이름을 생성하는 도우미 메서드
    private String generateDefaultName() {
        return "Node_" + count;
    }
}
