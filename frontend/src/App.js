import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import JobScheduler from "./components/JobScheduler";
import Notifications from "./components/notifications";
import { NotificationProvider } from "./components/notifications";

/*function App() {
  return <JobScheduler />;
}*/

function App() {
  return (
      <NotificationProvider>
        <Router>
          <Routes>
            <Route path="/" element={<JobScheduler />} />
            <Route path="/notifications" element={<Notifications />} />
          </Routes>
        </Router>
      </NotificationProvider>
  );
}

export default App;