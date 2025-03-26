/*
import React, { useState } from "react";
import { Button, TextField, MenuItem, Select, FormControl, InputLabel, Card, CardContent, Typography } from "@mui/material";
import axios from "axios";

const JobScheduler = () => {
    const [jobs, setJobs] = useState([]);
    const [formData, setFormData] = useState({
        name: "",
        binaryType: "JAR",
        binaryPath: null,
        scheduleType: "Immediate",
        scheduleTime: "",
        recurrencePattern: "",
        timezone: "UTC",
        messageBody: "",
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleFileChange = (e) => {
        setFormData({ ...formData, binaryPath: e.target.files[0] });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Form validation
        if (formData.scheduleType === "One-Time" && !formData.scheduleTime) {
            alert("Schedule Time is required for One-Time jobs.");
            return;
        }
        if (formData.scheduleType === "Recurring" && !formData.recurrencePattern) {
            alert("Recurrence Pattern is required for Recurring jobs.");
            return;
        }

        const jobData = new FormData();
        jobData.append("name", formData.name);
        jobData.append("binaryType", formData.binaryType);
        jobData.append("binaryPath", formData.binaryPath);
        jobData.append("scheduleType", formData.scheduleType);
        jobData.append("scheduleTime", formData.scheduleType === "One-Time" ? formData.scheduleTime : "");
        jobData.append("recurrencePattern", formData.scheduleType === "Recurring" ? formData.recurrencePattern : "");
        jobData.append("timezone", formData.timezone);
        jobData.append("messageBody", formData.messageBody);

        try {
            const response = await axios.post("http://127.0.0.1:8080/api/jobs", jobData, {
                headers: { "Content-Type": "multipart/form-data" },
            });
            //console.log(response)
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

                <input type="file" name="binaryPath" onChange={handleFileChange} required style={{ margin: "10px 0" }} />

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

            <Typography variant="h5" gutterBottom sx={{ mt: 3 }}>Scheduled Jobs</Typography>
            {jobs.length === 0 ? (
                <Typography>No jobs scheduled yet.</Typography>
            ) : (
                jobs.map((job) => (
                    <Card key={job.id} sx={{ mt: 2 }}>
                        <CardContent>
                            <Typography><strong>Name:</strong> {job.name}</Typography>
                            <Typography><strong>Binary Type:</strong> {job.binaryType}</Typography>
                            <Typography><strong>Schedule Type:</strong> {job.scheduleType}</Typography>
                            {job.scheduleType === "One-Time" && <Typography><strong>Schedule Time:</strong> {job.scheduleTime}</Typography>}
                            {job.scheduleType === "Recurring" && <Typography><strong>Recurrence:</strong> {job.recurrencePattern}</Typography>}
                            <Typography><strong>Timezone:</strong> {job.timezone}</Typography>
                            {job.messageBody && <Typography><strong>Message:</strong> {job.messageBody}</Typography>}
                        </CardContent>
                    </Card>
                ))
            )}
        </div>
    );
};

export default JobScheduler;*/

/*import React, { useState } from "react";
import { Button, TextField, MenuItem, Select, FormControl, InputLabel, Card, CardContent, Typography } from "@mui/material";
import axios from "axios";

const JobScheduler = () => {
    const [jobs, setJobs] = useState([]);
    const [formData, setFormData] = useState({
        name: "",
        binaryType: "JAR",
        binaryPath: "", // This will store the URL of the uploaded file
        scheduleType: "Immediate",
        scheduleTime: "",
        recurrencePattern: "",
        timezone: "UTC",
        messageBody: "",
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleFileChange = async (e) => {
        const file = e.target.files[0];
        if (file) {
            try {
                // Upload file to server/storage and get the URL
                const formData = new FormData();
                formData.append("file", file);

                // Replace the URL with your actual file upload endpoint
                const response = await axios.post("http://127.0.0.1:8080/api/upload", formData, {
                    headers: {
                        "Content-Type": "multipart/form-data",
                    },
                });

                // Assuming response.data.url contains the URL of the uploaded file
                setFormData({ ...formData, binaryPath: response.data.url });
            } catch (error) {
                console.error("Error uploading file:", error);
                alert("Failed to upload the file.");
            }
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Form validation
        if (formData.scheduleType === "One-Time" && !formData.scheduleTime) {
            alert("Schedule Time is required for One-Time jobs.");
            return;
        }
        if (formData.scheduleType === "Recurring" && !formData.recurrencePattern) {
            alert("Recurrence Pattern is required for Recurring jobs.");
            return;
        }

        try {
            const response = await axios.post("http://127.0.0.1:8080/api/jobs", formData, {
                headers: { "Content-Type": "application/json" },
            });
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

                <input type="file" name="binaryPath" onChange={handleFileChange} required style={{ margin: "10px 0" }} />

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

            <Typography variant="h5" gutterBottom sx={{ mt: 3 }}>Scheduled Jobs</Typography>
            {jobs.length === 0 ? (
                <Typography>No jobs scheduled yet.</Typography>
            ) : (
                jobs.map((job) => (
                    <Card key={job.id} sx={{ mt: 2 }}>
                        <CardContent>
                            <Typography><strong>Name:</strong> {job.name}</Typography>
                            <Typography><strong>Binary Type:</strong> {job.binaryType}</Typography>
                            <Typography><strong>Schedule Type:</strong> {job.scheduleType}</Typography>
                            {job.scheduleType === "One-Time" && <Typography><strong>Schedule Time:</strong> {job.scheduleTime}</Typography>}
                            {job.scheduleType === "Recurring" && <Typography><strong>Recurrence:</strong> {job.recurrencePattern}</Typography>}
                            <Typography><strong>Timezone:</strong> {job.timezone}</Typography>
                            {job.messageBody && <Typography><strong>Message:</strong> {job.messageBody}</Typography>}
                        </CardContent>
                    </Card>
                ))
            )}
        </div>
    );
};

export default JobScheduler;*/

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




