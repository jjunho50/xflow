package com.nhnacademy.Webservice;

import java.util.ArrayList;
import java.util.List;
import com.nhnacademy.Node.FlowMessage;
import com.nhnacademy.Node.InputNode;
import com.nhnacademy.Node.Node;
import com.nhnacademy.Node.OutputNode;

// HTTP Client 노드
public class HttpClientNode extends Node implements InputNode, OutputNode {

    private List<OutputNode> connectedNodes = new ArrayList<>();

    public void addConnectedNode(OutputNode node) {
        connectedNodes.add(node);
    }

    @Override
    public FlowMessage execute() {
        // HTTP Request 생성 및 서버에 요청 후 결과 반환
        HttpRequest request = createHttpRequest();
        HttpResponse response = sendHttpRequest(request);

        // 결과를 FlowMessage로 반환
        return new FlowMessage(response.getData());
    }

    private HttpRequest createHttpRequest() {
        // HTTP Request 생성 로직
        // 구현은 실제로 필요한 정보들을 설정하는 로직 필요
        return new HttpRequest();
    }

    private HttpResponse sendHttpRequest(HttpRequest request) {
        // HTTP Request를 서버에 전송하고 결과를 받아오는 로직
        // 구현은 실제로 서버와 통신하는 로직 필요
        return new HttpResponse();
    }

    @Override
    public void execute(FlowMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }



}

