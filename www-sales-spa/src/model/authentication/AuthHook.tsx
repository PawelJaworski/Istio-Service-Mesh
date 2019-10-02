import React, { useState, useEffect } from "react";
import {AUTHENTICATION} from "./Authentication";

export function useAuthentication() {
    const [ authenticated, setAuthenticated] = useState(false);

    useEffect(() => {
        subscribeAuthentication()
    }, []);

    function subscribeAuthentication() {
        AUTHENTICATION
            .onAuthenticated(() => {
                setAuthenticated(true)
            })
            .onError((error: string) => {
                setAuthenticated(false)
            })
            .applyAuth()
    }

    return authenticated;
}