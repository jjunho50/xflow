package com.nhnacademy.Webservice;

public class WebService {
    public static void main(String[] args) {
        // Web 서비스를 구성하는 노드들 생성 및 연결
        WebAnalysisNode analysisNode = new WebAnalysisNode("http://www.example.com");
        HttpClientNode clientNode = new HttpClientNode();
        WebOutputNode outputNode = new WebOutputNode();

        // 노드 간 연결
        analysisNode.addConnectedNode(clientNode);
        clientNode.addConnectedNode(outputNode);

        // 실행
        analysisNode.execute();
    }
}
