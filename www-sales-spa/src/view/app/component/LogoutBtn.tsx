import React from "react";
import {AUTHENTICATION} from "../../../model/authentication/Authentication";

export const LogoutBtn = (props: {className?: String}) => {
    function onSubmit() {
        AUTHENTICATION.logout()
    }

    return <button className={"btn btn-secondary " + props.className} onClick={onSubmit}>Logout</button>
}