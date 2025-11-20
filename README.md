🚀 Job Scheduler

A powerful and flexible Job Scheduling System that allows users to schedule backend tasks, execute binaries, and trigger asynchronous Kafka messages. Includes a Spring Boot backend and a React frontend for complete workflow management.

🧩 Overview
The Job Scheduler provides a centralized platform to:
• Run jobs at specific times
• Execute binaries (JAR/NPM) stored in MinIO
• Trigger delayed messages
• Schedule recurring jobs (hourly/daily/weekly/monthly)
• Track execution status and metadata
• Manage everything via a clean UI

✨ Features

🔹 Job Scheduling Capabilities

1. Specific Time Scheduling
a) Schedule jobs at an exact timestamp.
b) Full support for multiple time zones.
c) Runs the associated binary and tracks execution state:
   i) Success
   ii) Failure
   iii) In-Progress
   iv) Skipped

2. Immediate Execution
a) Execute any job instantly.
b) Result and logs are stored for auditing.

3. Delayed Messaging
a) Send a message after a specified delay (e.g., after 10 minutes).
b) Messages are dispatched asynchronously via Kafka.
c) Supports client-provided metadata.

🔹 Frequency-Based Scheduling
Recurring jobs supported:
• Hourly
• Daily
• Weekly (select days)
• Monthly (select dates)

📘 Example Use Cases
• One-Time Job: Run a job at 11 AM IST once.
• Weekly Job: Execute at 12 PM GMT on Tuesdays & Fridays.
• Delayed Reminder: Send a Kafka message 10 minutes later.

🛠️ Technology Stack

Backend
• Java + Spring Boot — business logic & scheduler
• Kafka — asynchronous messaging
• MinIO — binary (JAR/NPM) storage
• PostgreSQL — message & job metadata storage

Frontend
• React.js — responsive client UI

📝 Assumptions
• Kafka handles all async message deliveries.
• MinIO stores job execution binaries.
• PostgreSQL persists job logs, schedules, and metadata.

🚀 Future Improvements
• Migrate to YugabyteDB for horizontally scalable storage
• Introduce Next.js for server-side rendering & performance
• Expand and refine the UI for an improved developer experience
