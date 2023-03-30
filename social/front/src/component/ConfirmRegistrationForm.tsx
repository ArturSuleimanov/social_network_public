import {useForm} from "react-hook-form";
import {yupResolver} from "@hookform/resolvers/yup/dist/yup";
import Axios from "axios";
import {DOMAIN} from "../config/urls";
import * as yup from "yup";
import {useCookies} from "react-cookie";
import {useNavigate} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {setCsrfToken} from "../store/csrfTokenStore";
import {CSRF_TOKEN_NAME_IN_HEADERS} from "../config/tokens";
import {useState} from "react";


const activationCodeMessage = <p>Invalid activation code!</p>;

export interface ConfirmRegistrationData {

    email: string,
    activationCode: string
}

export const ConfirmRegistrationForm = (props: any) => {
    const schema = yup.object().shape({
        activationCode: yup.string().max(8).min(8).required(),
    });

    const { register: register, handleSubmit, formState: {errors} } = useForm<ConfirmRegistrationData>({
        resolver: yupResolver(schema),
    });

    const [activationCodeError, setActivationCodeError] = useState<JSX.Element>(<div></div>);
    const [cookies, setCookie] = useCookies(['Authorization']);

    const navigate = useNavigate();
    const onSubmit = async (data: ConfirmRegistrationData) => {
        data.email = props.email;

        const jwtToken = await Axios.post(
            DOMAIN + "/auth/confirm-registration",
            data,
        )
            .then((response:any) => response.data?.token).catch(err=>err);

        if (!jwtToken) {
            setActivationCodeError(activationCodeMessage);
        } else {
            props.setJwt(jwtToken);
            setCookie("Authorization", jwtToken);
            navigate('/home', {replace: true});
        }


    };


    return (
        <div>
            <form onSubmit={handleSubmit(onSubmit)}>
                <input type="text" placeholder="Code" {...register("activationCode")}/>
                <p>{errors.activationCode?.message}</p>
                {activationCodeError}
                <input type="submit"/>
            </form>
        </div>
    );
}