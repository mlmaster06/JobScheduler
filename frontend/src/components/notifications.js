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

/*

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

*/

import React, { useState, useEffect, useContext } from "react";
import { Card, CardContent, Typography, Badge, Button } from "@mui/material";
import NotificationsIcon from "@mui/icons-material/Notifications";
import SockJS from "sockjs-client";
import Stomp from "stompjs";

// Create a context to share notification state across components
export const NotificationContext = React.createContext({
    unreadCount: 0,
    setUnreadCount: () => {},
    notifications: [],
    setNotifications: () => {},
});

// Hook to use notifications in other components
export const useNotifications = () => useContext(NotificationContext);

// Provider component to wrap your app
export const NotificationProvider = ({ children }) => {
    const [stompClient, setStompClient] = useState(null);
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);

    useEffect(() => {
        // Connect to WebSocket
        const socket = new SockJS("http://localhost:8080/ws");
        const client = Stomp.over(socket);

        client.connect({}, () => {
            console.log("Connected to WebSocket");
            setStompClient(client);

            // Subscribe to topic
            client.subscribe("/topic/job-notifications", (message) => {
                if (message.body) {
                    const newNotification = {
                        message: message.body,
                        timestamp: new Date().toLocaleTimeString(),
                        read: false
                    };

                    setNotifications((prev) => [newNotification, ...prev]);
                    setUnreadCount((prev) => prev + 1);
                }
            });
        });

        return () => {
            if (stompClient) {
                stompClient.disconnect();
            }
        };
    }, []);

    const value = {
        notifications,
        setNotifications,
        unreadCount,
        setUnreadCount,
    };

    return (
        <NotificationContext.Provider value={value}>
            {children}
        </NotificationContext.Provider>
    );
};

// Notification Button Component
export const NotificationButton = () => {
    const [open, setOpen] = useState(false);
    const { notifications, unreadCount, setUnreadCount } = useNotifications();

    const handleOpen = () => {
        setOpen(!open);
        if (!open) {
            // Mark all as read when opening
            setUnreadCount(0);
        }
    };

    return (
        <div style={{ position: "relative", display: "inline-block" }}>
            <Badge badgeContent={unreadCount} color="error">
                <Button
                    variant="contained"
                    color="primary"
                    onClick={handleOpen}
                    startIcon={<NotificationsIcon />}
                >
                    Notifications
                </Button>
            </Badge>

            {open && (
                <div
                    style={{
                        position: "absolute",
                        width: "300px",
                        maxHeight: "400px",
                        overflowY: "auto",
                        right: 0,
                        marginTop: "10px",
                        zIndex: 1000,
                        backgroundColor: "white",
                        boxShadow: "0 4px 8px rgba(0,0,0,0.2)",
                        borderRadius: "4px"
                    }}
                >
                    {notifications.length === 0 ? (
                        <Card>
                            <CardContent>
                                <Typography>No notifications yet.</Typography>
                            </CardContent>
                        </Card>
                    ) : (
                        notifications.map((notif, index) => (
                            <Card key={index} sx={{ mb: 1 }}>
                                <CardContent>
                                    <Typography><strong>{notif.timestamp}:</strong> {notif.message}</Typography>
                                </CardContent>
                            </Card>
                        ))
                    )}
                </div>
            )}
        </div>
    );
};

// Notifications Page Component
const Notifications = () => {
    const { notifications } = useNotifications();

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


