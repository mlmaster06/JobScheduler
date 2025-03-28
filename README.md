"# JobScheduler" 

Job Scheduler

Overview

This project is a Job Scheduler that allows users to schedule backend tasks (jobs) to run at specific times, execute binaries (e.g., JAR files or NPM packages), and manage asynchronous messaging using Kafka. It includes both a backend API and a frontend user interface.

Features

1. Job Scheduling Capabilities

Specific Time Scheduling:

Users can schedule jobs at a specific time with support for different time zones.

The scheduled job runs a binary and stores execution status (success, failure, etc.).

Immediate Execution:

Allows jobs to be executed immediately with status tracking.

Delayed Messaging:

Schedule a message to be sent after a specified number of minutes.

Messages are sent asynchronously using Kafka with client-provided metadata.

2. Frequency-Based Scheduling

Recurring Jobs:

Hourly

Daily

Weekly (specific days)

Monthly (specific dates)

3. Example Use Cases

One-Time Job: Schedule a job to run at 11 AM IST once.

Weekly Job: Schedule a job to run at 12 PM GMT on Tuesdays and Fridays.

Delayed Reminder: Schedule a message to be sent after 10 minutes via Kafka.

Technology Stack

Backend

Java & Spring Boot: Core framework for backend logic.

Kafka: Used for asynchronous messaging.

MinIO: Used for storing binaries.

PostgreSQL: Used for storing messages and message details.

Frontend

React: Used for building the UI.


Assumptions

Kafka is used for handling asynchronous messaging.

MinIO is used for storing job execution binaries.

PostgreSQL is used for storing messages and message details.


Future Improvements

Implementing YugabyteDB for scalable storage.

Adding Next.js for server-side rendering improvements.

Expanding UI for better user experience