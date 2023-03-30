import {useEffect, useState} from "react";
import Axios from "axios";
import {DOMAIN} from "../config/urls";
import {setCsrfToken} from "../store/csrfTokenStore";
import {CSRF_TOKEN_NAME_IN_HEADERS} from "../config/tokens";
import {useCookies} from "react-cookie";

export const HomePage = (props: any) => {
    const [text, setText] = useState<string>("");


    useEffect(()=>{

        Axios
            .get(DOMAIN + "/api/v1/greetings", {headers: {"Authorization": props.jwt}})
            .then((response) => {
                setText(response.data)
            })}, []);

    return (
        <div>{text}</div>
    );
}