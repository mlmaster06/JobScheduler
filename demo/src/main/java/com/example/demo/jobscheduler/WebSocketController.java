package com.example.demo.jobscheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Called by other components to notify clients over WebSocket
     */
    public void sendNotification(String message) {
        messagingTemplate.convertAndSend("/topic/job-notifications", message);
        System.out.println("WebSocket notification sent: " + message);
    }

    /**
     * Optional: Allows frontend to send messages to backend via WebSocket
     */
    /*@MessageMapping("/notify")
    public void receiveClientMessage(String clientMessage) {
        System.out.println("Received from client via WebSocket: " + clientMessage);
        // You can forward it back or handle as needed
    }*/
}

//
