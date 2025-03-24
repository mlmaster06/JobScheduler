import axios from "axios";

const API_URL = "http://localhost:8080/api/jobs"; // Backend API base URL

export const fetchJobs = async () => {
  const response = await axios.get(API_URL);
  return response.data;
};

export const createJob = async (jobData) => {
  const response = await axios.post(API_URL, jobData);
  return response.data;
};
