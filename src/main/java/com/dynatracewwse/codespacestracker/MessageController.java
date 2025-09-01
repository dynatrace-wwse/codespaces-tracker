package com.dynatracewwse.codespacestracker;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;



@RestController
@RequestMapping("/api")
public class MessageController {


    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @PostMapping("/receive")
    public ResponseEntity<String> receiveJson(@RequestBody JsonNode jsonNode, HttpServletRequest request) {
        String token = request.getHeader("Authorization");


        if (token == null || !token.equals(JsonReceiverApplication.ENCODEDTOKEN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You shall not pass!\n");
        }

        // We log the payload and retrieve the metrics as Bizevents from the log/code with Dynatrace. 
        // This way we can add more metrics to the payload without the need of recompiling or changing this code
        String clientIp = request.getRemoteAddr();

        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            // Add a new field
            objectNode.put("client.ip", clientIp);
            
            //Query geo locations
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
            objectNode.put("geo.continent.name", continent);
            objectNode.put("geo.country.name", country);
            objectNode.put("geo.region.name", region);
            objectNode.put("geo.city.name", city);
        }
        
        logger.info(" IP:" + clientIp + " JSON: " + jsonNode.toString());
        return ResponseEntity.ok("Tracking information received, thank you! Dynatrace loves you!\n");
    }
}
