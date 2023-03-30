import {configureStore} from "@reduxjs/toolkit";
import {csrfTokenSlice} from "./csrfTokenStore";

export const store = configureStore({
    reducer: {
        csrfToken: csrfTokenSlice.reducer,
    }
});