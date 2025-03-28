/*
import { useEffect, useState } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

const useWebSocket = () => {
    const [notifications, setNotifications] = useState([]);

    useEffect(() => {
        const stompClient = new Client({
            brokerURL: "ws://localhost:8080/ws",
            onConnect: () => {
                stompClient.subscribe("/topic/job-notifications", (message) => {
                    setNotifications(prev => [message.body, ...prev]);
                });
            },
            onWebSocketError: (error) => console.error("WebSocket Error", error),
        });

        stompClient.activate();

        return () => stompClient.deactivate();
    }, []);

    return notifications;
};

export default useWebSocket;
*/


import { useEffect, useState } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

const useWebSocket = () => {
    const [notifications, setNotifications] = useState([]);

    useEffect(() => {
        const stompClient = new Client({
            webSocketFactory: () => new SockJS("http://localhost:8080/ws"), // Fix for SockJS
            onConnect: () => {
                console.log("Connected to WebSocket");
                stompClient.subscribe("/topic/job-notifications", (message) => { // ✅ Fix Topic Name
                    setNotifications(prev => [{ message: message.body, timestamp: new Date().toLocaleTimeString() }, ...prev]);
                });
            },
            onWebSocketError: (error) => console.error("WebSocket Error", error),
        });

        stompClient.activate();

        return () => stompClient.deactivate();
    }, []);

    return notifications;
};

export default useWebSocket;
