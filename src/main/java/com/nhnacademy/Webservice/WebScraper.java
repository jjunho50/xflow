package com.nhnacademy.Webservice;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebScraper {
    // main을 지우고 class로 만들고 서버에서 이 동작을 사용하자.
    public static void main(String[] args) {
        try {
            String url = "http://ems.nhnacademy.com:1880/dev";
            String responseData = fetchDataFromUrl(url);

            // 데이터를 JSON 파일에 저장
            String filePath = "output.json";
            saveDataToJsonFile(responseData, filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String fetchDataFromUrl(String url) throws IOException {
        StringBuilder responseData = new StringBuilder();

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                responseData.append(line);
            }
        } finally {
            connection.disconnect();
        }

        return responseData.toString();
    }

    private static void saveDataToJsonFile(String data, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            String extractedData = extractDataFromHtml(data);
            fileWriter.write(extractedData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String extractDataFromHtml(String html) {
        return extractText(html);
    }

    private static String extractText(String html) {
        String pattern = "<[^>]*>|\\s+";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(html);

        return m.replaceAll(" ").trim();
    }
}


