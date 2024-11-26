package org.example;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.util.Properties;

public class Main {
    private static String apiKey = null;
    private static String apiURL = "https://api.weather.yandex.ru/v2/forecast";

    public static void main(String[] args) {
        URL url;
        HttpURLConnection connection = null;
        StringBuilder content;
        Properties properties = new Properties();
        double lat = -19.244594;
        double lon = 146.809476;
        apiURL += "?lat=" + lat + "&lon=" + lon;

        try (FileInputStream input = new FileInputStream("config.properties")) {
            properties.load(input);
            apiKey = properties.getProperty("api.key");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            url = new URL(apiURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Yandex-Weather-Key", apiKey);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect();
        }
        System.out.println(content);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(String.valueOf(content));
            JsonNode factNode = rootNode.path("fact");
            System.out.println("Текущая температура: " + factNode.path("temp") + "°C");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}


