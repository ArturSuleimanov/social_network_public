import * as yup from "yup";
import { useForm } from "react-hook-form";
import {yupResolver} from "@hookform/resolvers/yup";
import Axios from "axios";
import {useState} from "react";
import {useQuery} from "@tanstack/react-query";
import {DOMAIN} from "../config/urls";
import {CSRF_TOKEN_NAME_IN_HEADERS} from "../config/tokens";
import {useDispatch, useSelector} from "react-redux";
import {Link, useNavigate} from "react-router-dom";


const message = `You must provide a`;
const toLongError = "Not more than 55 symbols.";
const toShortError = "Must contain at least 3 symbols."
const userAlreadyExistsMessage = <p>This email is already taken <Link to={"/authenticate"}>Sign in</Link></p>

export interface RegisterUserData {
    firstname: string,
    lastname: string,
    email: string,
    password: string,
    confirmPassword: string
}

//

export const RegisterUserForm = (props: any) => {
    const schema = yup.object().shape({
        firstname: yup
            .string()
            .max(55, toLongError)
            .min(3, toShortError)
            .required(message + " firstname!"),
        lastname: yup
            .string()
            .max(55, toLongError)
            .min(3, toShortError)
            .required(message + " lastname!"),
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
            .required(message + " password!"),
        confirmPassword: yup
            .string()
            .max(55)
            .min(8)
            .oneOf([yup.ref("password"), ""],
                "Confirmed password must be the same as password!")
            .required("You need to confirm password!")
    });

    const { register, handleSubmit, formState: {errors} } = useForm<RegisterUserData>({
        resolver: yupResolver(schema),
    });


    const navigate = useNavigate();



    const [userExistsError, setUserExistsError] = useState<JSX.Element>(<div></div>);

    const onSubmit = async (formData: RegisterUserData) => {
        const {password, lastname, firstname, email} = formData;
        const registerData = {password, lastname, firstname, email};
        props.setEmail(email);
        const status =
            await Axios.post(
                DOMAIN + "/auth/register",
                    registerData,
            )
            .then((response:any) => response.data.status)

        if (status === 300) {   // email already in use
            setUserExistsError(userAlreadyExistsMessage);
        } else {
            navigate('/confirm-registration', { replace: true });
        }
    };




    return (
        <div>
            <form onSubmit={handleSubmit(onSubmit)}>
                <input type="text" placeholder="Firstname" {...register("firstname")}/>
                <p>{errors.firstname?.message}</p>
                <input type="text" placeholder="Firstname" {...register("lastname")}/>
                <p>{errors.lastname?.message}</p>
                <input type="text" placeholder="Email" {...register("email")}/>
                <p>{errors.email?.message}</p>
                {userExistsError}
                <input type="password" placeholder="Password" {...register("password")}/>
                <p>{errors.password?.message}</p>
                <input type="password" placeholder="Confirm password" {...register("confirmPassword")}/>
                <p>{errors.confirmPassword?.message}</p>
                <input type="submit"/>
            </form>
        </div>
    )
};