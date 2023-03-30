import {createSlice} from "@reduxjs/toolkit";


export const csrfTokenSlice = createSlice({
    name: "csrfToken",
    initialState: {value: {csrfToken: null}},
    reducers: {
        setCsrfToken: (state, action) => {
            state.value = action.payload;
        },
    }
});

export const { setCsrfToken } = csrfTokenSlice.actions;

