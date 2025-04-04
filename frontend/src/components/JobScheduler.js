
/*
import React, { useState } from "react";
import { Button, TextField, MenuItem, Select, FormControl, InputLabel, Card, CardContent, Typography } from "@mui/material";
import axios from "axios";

const JobScheduler = () => {
    const [jobs, setJobs] = useState([]);
    const [formData, setFormData] = useState({
        name: "",
        binaryType: "JAR",
        scheduleType: "Immediate",
        scheduleTime: "",
        recurrencePattern: "",
        timezone: "UTC",
        messageBody: "",
    });

    const [selectedFile, setSelectedFile] = useState(null);

    // Handle input field changes
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    // Handle file selection
    const handleFileChange = (e) => {
        setSelectedFile(e.target.files[0]);
    };

    // Handle form submission
    const handleSubmit = async (e) => {
        e.preventDefault();

        if (formData.scheduleType === "One-Time" && !formData.scheduleTime) {
            alert("Schedule Time is required for One-Time jobs.");
            return;
        }
        if (formData.scheduleType === "Recurring" && !formData.recurrencePattern) {
            alert("Recurrence Pattern is required for Recurring jobs.");
            return;
        }

        const jobData = new FormData();
        jobData.append("job", JSON.stringify(formData));  // Convert job data to JSON
        if (selectedFile) {
            jobData.append("file", selectedFile);  // Attach file if selected
        }

        try {
            const response = await axios.post("http://127.0.0.1:8080/api/jobs", jobData, {
                headers: { "Content-Type": "multipart/form-data" },
            });

            console.log(response);
            console.log(response.body);
            alert("Job scheduled successfully!");
            setJobs([...jobs, response.data]);

        } catch (error) {
            alert("Failed to schedule job.");
            console.error(error);
        }
    };

    return (
        <div style={{ padding: "20px", maxWidth: "500px", margin: "auto" }}>
            <Typography variant="h4" gutterBottom>Job Scheduler</Typography>
            <form onSubmit={handleSubmit}>
                <TextField fullWidth margin="normal" label="Job Name" name="name" value={formData.name} onChange={handleChange} required />

                <FormControl fullWidth margin="normal">
                    <InputLabel>Binary Type</InputLabel>
                    <Select name="binaryType" value={formData.binaryType} onChange={handleChange}>
                        <MenuItem value="JAR">JAR</MenuItem>
                        <MenuItem value="NPM">NPM</MenuItem>
                    </Select>
                </FormControl>

                <input type="file" name="file" onChange={handleFileChange} required style={{ margin: "10px 0" }} />

                <FormControl fullWidth margin="normal">
                    <InputLabel>Schedule Type</InputLabel>
                    <Select name="scheduleType" value={formData.scheduleType} onChange={handleChange}>
                        <MenuItem value="Immediate">Immediate</MenuItem>
                        <MenuItem value="One-Time">One-Time</MenuItem>
                        <MenuItem value="Recurring">Recurring</MenuItem>
                    </Select>
                </FormControl>

                {formData.scheduleType === "One-Time" && (
                    <TextField fullWidth margin="normal" type="datetime-local" label="Schedule Time" name="scheduleTime" value={formData.scheduleTime} onChange={handleChange} required />
                )}

                {formData.scheduleType === "Recurring" && (
                    <TextField fullWidth margin="normal" label="Recurrence Pattern (Cron)" name="recurrencePattern" value={formData.recurrencePattern} onChange={handleChange} required />
                )}

                <FormControl fullWidth margin="normal">
                    <InputLabel>Timezone</InputLabel>
                    <Select name="timezone" value={formData.timezone} onChange={handleChange}>
                        <MenuItem value="UTC">UTC</MenuItem>
                        <MenuItem value="America/New_York">America/New_York</MenuItem>
                        <MenuItem value="Asia/Kolkata">Asia/Kolkata</MenuItem>
                    </Select>
                </FormControl>

                <TextField fullWidth margin="normal" label="Message Body (Optional)" name="messageBody" value={formData.messageBody} onChange={handleChange} />

                <Button fullWidth variant="contained" type="submit" sx={{ mt: 2 }}>Schedule Job</Button>
            </form>
        </div>
    );
};

export default JobScheduler;


//asd*/


import React, { useState } from "react";
import { Button, TextField, MenuItem, Select, FormControl, InputLabel, Card, CardContent, Typography, Box } from "@mui/material";
import axios from "axios";
import { NotificationButton } from "./Notifications";
import { Link } from "react-router-dom";

const JobScheduler = () => {
    const [jobs, setJobs] = useState([]);
    const [formData, setFormData] = useState({
        name: "",
        binaryType: "JAR",
        scheduleType: "Immediate",
        scheduleTime: "",
        recurrencePattern: "",
        timezone: "UTC",
        messageBody: "",
    });

    const [selectedFile, setSelectedFile] = useState(null);

    // Handle input field changes
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    // Handle file selection
    const handleFileChange = (e) => {
        setSelectedFile(e.target.files[0]);
    };

    // Handle form submission
    const handleSubmit = async (e) => {
        e.preventDefault();

        if (formData.scheduleType === "One-Time" && !formData.scheduleTime) {
            alert("Schedule Time is required for One-Time jobs.");
            return;
        }
        if (formData.scheduleType === "Recurring" && !formData.recurrencePattern) {
            alert("Recurrence Pattern is required for Recurring jobs.");
            return;
        }

        const jobData = new FormData();
        jobData.append("job", JSON.stringify(formData));  // Convert job data to JSON
        if (selectedFile) {
            jobData.append("file", selectedFile);  // Attach file if selected
        }

        try {
            const response = await axios.post("http://127.0.0.1:8080/api/jobs", jobData, {
                headers: { "Content-Type": "multipart/form-data" },
            });

            console.log(response);
            console.log(response.body);
            alert("Job scheduled successfully!");
            setJobs([...jobs, response.data]);

        } catch (error) {
            alert("Failed to schedule job.");
            console.error(error);
        }
    };

    return (
        <div style={{ padding: "20px", maxWidth: "500px", margin: "auto" }}>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                <Typography variant="h4">Job Scheduler</Typography>
                <Box>
                    <NotificationButton />
                    <Button
                        component={Link}
                        to="/notifications"
                        variant="outlined"
                        sx={{ ml: 2 }}
                    >
                        View All
                    </Button>
                </Box>
            </Box>

            <form onSubmit={handleSubmit}>
                <TextField fullWidth margin="normal" label="Job Name" name="name" value={formData.name} onChange={handleChange} required />

                <FormControl fullWidth margin="normal">
                    <InputLabel>Binary Type</InputLabel>
                    <Select name="binaryType" value={formData.binaryType} onChange={handleChange}>
                        <MenuItem value="JAR">JAR</MenuItem>
                        <MenuItem value="NPM">NPM</MenuItem>
                    </Select>
                </FormControl>

                <input type="file" name="file" onChange={handleFileChange} required style={{ margin: "10px 0" }} />

                <FormControl fullWidth margin="normal">
                    <InputLabel>Schedule Type</InputLabel>
                    <Select name="scheduleType" value={formData.scheduleType} onChange={handleChange}>
                        <MenuItem value="Immediate">Immediate</MenuItem>
                        <MenuItem value="One-Time">One-Time</MenuItem>
                        <MenuItem value="Recurring">Recurring</MenuItem>
                    </Select>
                </FormControl>

                {formData.scheduleType === "One-Time" && (
                    <TextField fullWidth margin="normal" type="datetime-local" label="Schedule Time" name="scheduleTime" value={formData.scheduleTime} onChange={handleChange} required />
                )}

                {formData.scheduleType === "Recurring" && (
                    <TextField fullWidth margin="normal" label="Recurrence Pattern (Cron)" name="recurrencePattern" value={formData.recurrencePattern} onChange={handleChange} required />
                )}

                <FormControl fullWidth margin="normal">
                    <InputLabel>Timezone</InputLabel>
                    <Select name="timezone" value={formData.timezone} onChange={handleChange}>
                        <MenuItem value="UTC">UTC</MenuItem>
                        <MenuItem value="America/New_York">America/New_York</MenuItem>
                        <MenuItem value="Asia/Kolkata">Asia/Kolkata</MenuItem>
                    </Select>
                </FormControl>

                <TextField fullWidth margin="normal" label="Message Body (Optional)" name="messageBody" value={formData.messageBody} onChange={handleChange} />

                <Button fullWidth variant="contained" type="submit" sx={{ mt: 2 }}>Schedule Job</Button>
            </form>
        </div>
    );
};

export default JobScheduler;
