package org.networks.subject5.individual5;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class UrlQueryExtractor {


    public static void main(String[] args) {

        String urlString = "http://example.com/search?query=java&lang=en";


        try {
            URL url = new URL(urlString);
            String query = url.getQuery();
            Map<String, String> queryParams = new HashMap<>();

            if (query != null && !query.isEmpty()) {

                String[] pairs = query.split("&");

                for (String pair : pairs) {
                    String[] keyValues = pair.split("=");
                    if (keyValues.length == 2) {
                        String key = URLDecoder.decode(keyValues[0], StandardCharsets.UTF_8);
                        String value = URLDecoder.decode(keyValues[1], StandardCharsets.UTF_8);
                        queryParams.put(key, value);
                    }
                }
            }

            if (!queryParams.isEmpty()) {
                System.out.println("Data is collected into Hashmap");

                queryParams.forEach((k, v) -> System.out.println(k + ':' + v));
            } else {
                System.out.println("The queries is empty");
            }


        } catch (Exception e) {
            System.out.println("Uncorrected URl");
            e.printStackTrace();
        }

    }
}
