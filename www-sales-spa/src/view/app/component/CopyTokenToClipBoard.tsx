import React from "react";
import {AUTHENTICATION} from "../../../model/authentication/Authentication";

export const CopyTokenToClipBoardBtn = (props: {className?: String}) => {
    function onSubmit() {
        const token = AUTHENTICATION.getToken()
        if (token) {
            navigator.clipboard.writeText(token)
        }
    }

    return <button className={"btn btn-info " + props.className} onClick={onSubmit}>Copy Token</button>
}