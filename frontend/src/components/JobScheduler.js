import React, { useEffect } from "react";
import { useForm } from "react-hook-form";
import { useDispatch, useSelector } from "react-redux";
import { getJobs, addJob } from "../store/jobSlice";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import {
  Button,
  TextField,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  Card,
  CardContent,
  Typography,
} from "@mui/material";

const schema = yup.object().shape({
  jobName: yup.string().required("Job name is required"),
  scheduleType: yup.string().required("Schedule type is required"),
  time: yup.string().when("scheduleType", {
    is: "specificTime",
    then: yup.string().required("Time is required"),
  }),
});

const JobScheduler = () => {
  const dispatch = useDispatch();
  const { jobs, status } = useSelector((state) => state.jobs);
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm({
    resolver: yupResolver(schema),
  });

  useEffect(() => {
    dispatch(getJobs());
  }, [dispatch]);

  const onSubmit = (data) => {
    dispatch(addJob(data));
    reset();
  };

  return (
    <div style={{ maxWidth: 500, margin: "auto", padding: 20 }}>
      <Typography variant="h4" align="center" gutterBottom>
        Job Scheduler
      </Typography>
      <form
        onSubmit={handleSubmit(onSubmit)}
        style={{ display: "flex", flexDirection: "column", gap: "16px" }}
      >
        <TextField
          label="Job Name"
          {...register("jobName")}
          error={!!errors.jobName}
          helperText={errors.jobName?.message}
        />
        <FormControl>
          <InputLabel>Schedule Type</InputLabel>
          <Select {...register("scheduleType")}>
            <MenuItem value="specificTime">Specific Time</MenuItem>
            <MenuItem value="immediate">Immediate</MenuItem>
            <MenuItem value="recurring">Recurring</MenuItem>
          </Select>
        </FormControl>
        <Button type="submit" variant="contained" color="primary" fullWidth>
          Schedule Job
        </Button>
      </form>

      <Typography variant="h5" align="center" marginTop={3}>
        Scheduled Jobs
      </Typography>
      {status === "loading" ? (
        <p>Loading...</p>
      ) : jobs.length === 0 ? (
        <p>No jobs scheduled yet.</p>
      ) : (
        jobs.map((job) => (
          <Card key={job.id} sx={{ marginTop: 2, padding: 2 }}>
            <CardContent>
              <Typography variant="h6">{job.jobName}</Typography>
              <Typography variant="body1">Type: {job.scheduleType}</Typography>
            </CardContent>
          </Card>
        ))
      )}
    </div>
  );
};

export default JobScheduler;
