package com.dynatracewwse.codespacestracker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MaxMindGeoIP {
    private static final String ACCOUNT_ID = System.getenv("MAXMIND_ACCOUNT_ID");
    private static final String LICENSE_KEY =  System.getenv("MAXMIND_LICENSE_KEY"); 
    
    private static final String BASE_URL = "https://geolite.info/geoip/v2.1/city/";

    // Singleton instance
    private static MaxMindGeoIP instance;

    // Private constructor to prevent instantiation
    private MaxMindGeoIP() {}

    // Method to get the singleton instance
    public static MaxMindGeoIP getInstance() {
        if (instance == null) {
            synchronized (MaxMindGeoIP.class) {
                if (instance == null) {
                    instance = new MaxMindGeoIP();
                }
            }
        }
        return instance;
    }

    public JsonNode queryIP(String ip) {
        try {
            String urlStr = BASE_URL + ip;
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method
            connection.setRequestMethod("GET");
            // Set basic auth header
            String auth = ACCOUNT_ID + ":" + LICENSE_KEY;
            String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes());
            connection.setRequestProperty("Authorization", "Basic " + encodedAuth);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                // Read response
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse JSON response
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(response.toString());

                return jsonNode;

            } else {
                System.out.println("Error: Response code " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Example usage
    public static void main(String[] args) {
        MaxMindGeoIP geoIP = MaxMindGeoIP.getInstance();
        String clientIp = "217.238.152.246"; // replace with the IP you want to query
        JsonNode geoNode = MaxMindGeoIP.getInstance().queryIP(clientIp);
        String continent = "";
        String country = "";
        String region = "";
        String city = "";

        if (geoNode != null ){
                // geo.continent.name > continent.names.en
                continent = geoNode.path("continent").path("names").path("en").asText();
                // geo.region.name -> subdivisions.[0].names.en
                JsonNode subdivisionsNode = geoNode.path("subdivisions");
                if (subdivisionsNode.isArray() && subdivisionsNode.size() > 0) {
                    JsonNode firstSubdivision = subdivisionsNode.get(0);
                    // Now you can access properties of the first subdivision, for example:
                    region = firstSubdivision.path("names").path("en").asText();
                }
                // geo.country.name > country.names.en
                country = geoNode.path("country").path("names").path("en").asText();
                
                // geo.city.name -> city.names.en
                city = geoNode.path("city").path("names").path("en").asText();
            }
    }
}
