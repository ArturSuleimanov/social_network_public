import * as yup from "yup";
import {useForm} from "react-hook-form";
import {yupResolver} from "@hookform/resolvers/yup/dist/yup";
import {RegisterUserData} from "./RegisterUserForm";
import {useDispatch, useSelector} from "react-redux";
import Axios from "axios";
import {DOMAIN} from "../config/urls";
import {useNavigate} from "react-router-dom";
import {useCookies} from "react-cookie";
import {useState} from "react";

const message = `You must provide a`;
const toLongError = "Not more than 55 symbols.";
const toShortError = "Must contain at least 3 symbols."
const incorrectMailOrPasswordMessage = "Incorrect email or password!"

export interface AuthenticationUserData {
    email: string,
    password: string,
}


export const AuthenticationUserForm = (props: any) => {
    const schema = yup.object().shape({
        email: yup
            .string()
            .email("It's not an email!")
            .max(55, toLongError)
            .min(3, toShortError)
            .required(message + "n email!"),
        password: yup
            .string()
            .max(55, toLongError)
            .min(8, "Password must contain at least 8 symbols!")
            .required(message + " password!")
    });

    const { register, handleSubmit, formState: {errors} } = useForm<RegisterUserData>({
        resolver: yupResolver(schema),
    });

    const dispatchCsrfToken = useDispatch();
    const csrfToken = useSelector((state: any) => state.csrfToken.value.csrfToken);

    const navigate = useNavigate();

    const [cookies, setCookie] = useCookies(['Authorization']);

    const [incorrectEmailOrPasswordError, setIncorrectEOrPError] = useState<JSX.Element>(<div></div>)


    const onSubmit = async (formData: AuthenticationUserData) => {
        props.setEmail(formData.email);
        const response =
            await Axios.post(
                DOMAIN + "/auth/authenticate",
                formData,
            )
                .then((response: any) => response).catch(err=>err);
        const status:number = response?.data?.status;
        if (status === 300) {
            navigate("/confirm-registration");
        } else if (status === 200) {
            console.log(response.data.token)
            setCookie("Authorization", response.data.token)
            navigate('/home');
        } else {
            setIncorrectEOrPError(<p>{incorrectMailOrPasswordMessage}</p>)
        }
    };


    return (
        <div>
            <form onSubmit={handleSubmit(onSubmit)}>
                {incorrectEmailOrPasswordError}
                <input type="text" placeholder="Email" {...register("email")}/>
                <p>{errors.email?.message}</p>
                <input type="password" placeholder="Password" {...register("password")}/>
                <p>{errors.password?.message}</p>
                <input type="submit"/>
            </form>
        </div>
    );
};