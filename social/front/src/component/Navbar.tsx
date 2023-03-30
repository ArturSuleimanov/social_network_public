import {Link, useNavigate} from "react-router-dom";
import {useCookies} from "react-cookie";
import {DOMAIN, GOOGLE_AUTH_URL} from "../config/urls";
import Axios from "axios";

export const Navbar = () => {

    const [cookies, setCookie] = useCookies(['Authorization']);
    const navigate = useNavigate();

    const signOut = ()=>{
        console.log(cookies.Authorization);
        setCookie("Authorization", "");
        navigate("/registration");
        window.location.reload();
    };
    return (
        <div>
            <Link to={"/registration"}>Registration </Link>
            <Link to={"/authentication"}>Sign in </Link>
            <Link to={"/home"}>Home </Link>
            <a className="btn btn-block social-btn google" href={GOOGLE_AUTH_URL}>Login with Google</a>
            <button onClick={signOut}>signOut</button>
        </div>
    );
};