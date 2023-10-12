package com.nhnacademy.Webservice;

import java.util.ArrayList;
import java.util.List;
import com.nhnacademy.Node.FlowMessage;
import com.nhnacademy.Node.InputNode;
import com.nhnacademy.Node.Node;

// URL을 분석하여 필요한 정보를 출력하는 노드
public class WebAnalysisNode extends Node implements InputNode {
    private String url;

    public WebAnalysisNode(String url) {
        this.url = url;
    }

    private List<Node> connectedNodes = new ArrayList<>();

    public void addConnectedNode(Node node) {
        connectedNodes.add(node);
    }

    @Override
    public FlowMessage execute() {
        // URL 분석 및 필요한 정보 추출
        String scheme = extractScheme(url);
        String host = extractHost(url);
        int port = extractPort(url);
        String target = extractTarget(url);
        String parameters = extractParameters(url);

        // 결과를 FlowMessage로 반환
        String result = String.format("Scheme: %s, Host: %s, Port: %d, Target: %s, Parameters: %s",
                scheme, host, port, target, parameters);
        return new FlowMessage(result);
    }

    private String extractScheme(String url) {
        // URL에서 Scheme 추출 로직
        // 예: "http://www.example.com" -> "http"
        // 구현은 간소화했으며 실제로는 더 복잡한 로직 필요
        return "http";
    }

    private String extractHost(String url) {
        // URL에서 Host 추출 로직
        // 예: "http://www.example.com" -> "www.example.com"
        return "www.example.com";
    }

    private int extractPort(String url) {
        // URL에서 Port 추출 로직
        // 예: "http://www.example.com:8080" -> 8080
        // 구현은 간소화했으며 실제로는 더 복잡한 로직 필요
        return 8080;
    }

    private String extractTarget(String url) {
        // URL에서 Target 추출 로직
        // 예: "http://www.example.com/path/to/resource" -> "/path/to/resource"
        return "/path/to/resource";
    }

    private String extractParameters(String url) {
        // URL에서 Parameters 추출 로직
        // 예: "http://www.example.com?param1=value1&param2=value2" -> "param1=value1&param2=value2"
        return "param1=value1&param2=value2";
    }


}

