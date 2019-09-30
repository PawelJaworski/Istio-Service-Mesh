import * as React from "react";
import {func as prop_func, string as prop_string} from "prop-types";
import {useEffect, useState} from "react";

export const ErrorMessagePropTypes = {
    className: prop_string
}

export const ErrorMessage = (props: { className: string }) => {
    const [message, setErrorMessage] = useState("");

    useEffect(() => {
        subscribeErrors()

        return function cleanup() {
            unsubscribeErrors()
        };
    })

    function subscribeErrors() {
        document.addEventListener("offer-form.offer-criteria.error", onError);
    }

    function unsubscribeErrors() {
        document.removeEventListener("offer-form.offer-criteria.error", onError);
    }

    const onError: EventListener = ((event: CustomEvent) => {
        setErrorMessage(event.detail.error)
    }) as EventListener

    return <React.Fragment>
        { message ? <div className={props.className}>{message}</div> : null }
    </React.Fragment>
}