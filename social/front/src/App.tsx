import React from 'react';
import './App.css';
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import Axios from "axios";
import {store} from "./store/store";
import { Provider } from "react-redux";
import {AppRoutes} from "./component/AppRoutes";

function App() {
    const client = new QueryClient({defaultOptions: {   // axios config
            queries: {
                refetchOnWindowFocus: false,     // not gonna update tab every time we switch it
            }
        }
    });
    Axios.defaults.xsrfCookieName = "null";
    Axios.defaults.headers['Content-Type'] = 'application/json';
    Axios.defaults.withCredentials = true;


  return (
    <div className="App">
        <QueryClientProvider client={client}>
            <Provider store={store}>
                <AppRoutes/>
            </Provider>
        </QueryClientProvider>
    </div>
  );
}

export default App;
