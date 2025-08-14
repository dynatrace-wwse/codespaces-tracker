package com.dynatracewwse.codespacestracker;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.databind.JsonNode;


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
        logger.info(" IP:" + clientIp + " JSON: " + jsonNode.toString());
        return ResponseEntity.ok("Tracking information received, thank you! Dynatrace loves you!\n");
    }
}
