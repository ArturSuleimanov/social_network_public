import {BrowserRouter as Router, Routes, Route, Link} from "react-router-dom";
import {RegisterUserForm} from "./RegisterUserForm";
import Axios from "axios";
import {DOMAIN} from "../config/urls";
import {CSRF_TOKEN_NAME_IN_HEADERS} from "../config/tokens";
import {useDispatch, useSelector} from "react-redux";
import {setCsrfToken} from "../store/csrfTokenStore";
import React, {useEffect, useState} from "react";
import {ConfirmRegistrationForm} from "./ConfirmRegistrationForm";
import {AuthenticationUserForm} from "./AuthenticationUserForm";
import {HomePage} from "../pages/HomePage";
import {Navbar} from "./Navbar";
import {useCookies} from "react-cookie";
import {OAuth2RedirectHandler} from "./OAuth2RedirectHandler";


export const AppRoutes = () => {

    // const [cookies, setCookie] = useCookies(['Authorization']);
    const dispatch = useDispatch();
    const csrfToken = useSelector((state: any) => state.csrfToken.value.csrfToken);

    Axios.defaults.headers["X-XSRF-TOKEN"] = csrfToken;

    const [email, setEmail] = useState<string>("");
    const [jwt, setJwt] = useState<string>("");



    useEffect( ()=>{
            const csrfGen = async () => {
                await Axios
                    .get(DOMAIN + "/csrf")
                    .then((response: any) => {
                        dispatch(setCsrfToken({
                            csrfToken: response
                                .headers[CSRF_TOKEN_NAME_IN_HEADERS]
                        }));
                    });
                };
            csrfGen();
        }, []
    );

    return (
        <div>
            <Router>
                <Navbar/>
                <Routes>
                    <Route path="/registration" element={<RegisterUserForm {...{setEmail: setEmail}}/>} />
                    <Route path="/confirm-registration" element={<ConfirmRegistrationForm {...{email: email, setJwt: setJwt}}/>}/>
                    <Route path="/authentication" element={<AuthenticationUserForm {...{setEmail: setEmail}}/>}/>
                    <Route path="/home" element={<HomePage {...{jwt: jwt}}/>}/>
                    <Route path="/oauth2/redirect" element={<OAuth2RedirectHandler {...{setJwt: setJwt}}/>}/>
                </Routes>
            </Router>
        </div>
    )
}