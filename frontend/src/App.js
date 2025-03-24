import React from "react";
import { Provider } from "react-redux";
import store from "./store/store";
import JobScheduler from "./components/JobScheduler";

const App = () => (
  <Provider store={store}>
    <JobScheduler />
  </Provider>
);

export default App;
