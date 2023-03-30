import {useCookies} from "react-cookie";
import {useNavigate} from "react-router-dom";
import {useEffect} from "react";

const getUrlParameter = (name: string) => {
  name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
  var regex = new RegExp('[\\?&]' + name + '=([^&#]*)');

  var results = regex.exec(window.location.href);
  return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
};


export const OAuth2RedirectHandler = (props: any)=> {
  const token = getUrlParameter('token');
  const error = getUrlParameter('error');
  const [cookies, setCookie] = useCookies(['Authorization']);
  const navigate = useNavigate();
  let navigationUrl = "/authentication";
  if(token) {
    navigationUrl = "/home";
  }
  useEffect(()=>{
    if (!cookies.Authorization) {
      setCookie("Authorization", token);
    }
    props.setJwt(token);
    navigate(navigationUrl);
  });
  return <div></div>
};


