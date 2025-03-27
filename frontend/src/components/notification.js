/*
import React, { useState, useEffect } from "react";
import { Card, CardContent, Typography } from "@mui/material";

const Notifications = () => {
    const [notifications, setNotifications] = useState([]);

    useEffect(() => {
        const socket = new WebSocket("ws://127.0.0.1:8080/ws/notifications");

        socket.onmessage = (event) => {
            setNotifications((prev) => [{ message: event.data, timestamp: new Date().toLocaleTimeString() }, ...prev]);
        };

        socket.onclose = () => console.log("WebSocket Disconnected");
        socket.onerror = (error) => console.error("WebSocket Error:", error);

        return () => socket.close();
    }, []);

    return (
        <div style={{ padding: "20px", maxWidth: "600px", margin: "auto" }}>
            <Typography variant="h4" gutterBottom>Notifications</Typography>
            {notifications.length === 0 ? (
                <Typography>No notifications yet.</Typography>
            ) : (
                notifications.map((notif, index) => (
                    <Card key={index} sx={{ mt: 2 }}>
                        <CardContent>
                            <Typography><strong>{notif.timestamp}:</strong> {notif.message}</Typography>
                        </CardContent>
                    </Card>
                ))
            )}
        </div>
    );
};

export default Notifications;*/

import React, { useState, useEffect } from "react";
import { Card, CardContent, Typography } from "@mui/material";
import SockJS from "sockjs-client";
import Stomp from "stompjs";

const Notifications = () => {
    const [notifications, setNotifications] = useState([]);

    useEffect(() => {
        const socket = new SockJS("http://localhost:8080/ws"); // Connect to WebSocket
        const stompClient = Stomp.over(socket);

        stompClient.connect({}, () => {
            console.log("Connected to WebSocket");

            // Subscribe to topic
            stompClient.subscribe("/topic/job-notifications", (message) => {
                if (message.body) {
                    setNotifications((prev) => [
                        { message: message.body, timestamp: new Date().toLocaleTimeString() },
                        ...prev,
                    ]);
                }
            });
        });

        return () => {
            stompClient.disconnect();
        };
    }, []);

    return (
        <div style={{ padding: "20px", maxWidth: "600px", margin: "auto" }}>
            <Typography variant="h4" gutterBottom>Notifications</Typography>
            {notifications.length === 0 ? (
                <Typography>No notifications yet.</Typography>
            ) : (
                notifications.map((notif, index) => (
                    <Card key={index} sx={{ mt: 2 }}>
                        <CardContent>
                            <Typography><strong>{notif.timestamp}:</strong> {notif.message}</Typography>
                        </CardContent>
                    </Card>
                ))
            )}
        </div>
    );
};

export default Notifications;

