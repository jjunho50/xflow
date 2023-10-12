package com.nhnacademy.Webservice;

import com.nhnacademy.Node.FlowMessage;
import com.nhnacademy.Node.Node;
import com.nhnacademy.Node.OutputNode;

// Web 서비스의 결과를 출력하는 노드
public class WebOutputNode extends Node implements OutputNode {
    @Override
    public void execute(FlowMessage message) {
        // 결과를 원하는 형식으로 출력하는 로직
        System.out.println("Web Service Output: " + message.getData());
    }
}
