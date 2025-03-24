import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { fetchJobs, createJob } from "../api/jobApi";

export const getJobs = createAsyncThunk("jobs/fetchJobs", fetchJobs);
export const addJob = createAsyncThunk("jobs/addJob", async (jobData) => {
  return await createJob(jobData);
});

const jobSlice = createSlice({
  name: "jobs",
  initialState: { jobs: [], status: "idle", error: null },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(getJobs.pending, (state) => {
        state.status = "loading";
      })
      .addCase(getJobs.fulfilled, (state, action) => {
        state.status = "succeeded";
        state.jobs = action.payload;
      })
      .addCase(getJobs.rejected, (state, action) => {
        state.status = "failed";
        state.error = action.error.message;
      })
      .addCase(addJob.fulfilled, (state, action) => {
        state.jobs.push(action.payload);
      });
  },
});

export default jobSlice.reducer;
