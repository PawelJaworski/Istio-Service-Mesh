import * as React from "react";
import { string as prop_string, number as prop_number } from "prop-types";
import { useEffect, useState } from "react";

export const ErrorMessagePropTypes = {
    id: prop_string,
    sourceId: prop_string,
    sourceVersion: prop_number,
    className: prop_string
}

export const ErrorMessage = (props: Props) => {
    const [ message, setErrorMessage ] = useState("");

    let eventSource: EventSource = undefined
    useEffect(() => {
        listenLoanErrors()

        return function cleanup() {
            if (eventSource) {
                eventSource.close()
                eventSource = undefined
            }
        };
    })

    const listenLoanErrors = () => {
        if (!props.sourceId || !props.sourceVersion) {
            setErrorMessage("")
            return
        }

        const errorUrl = "/loan/error/" + props.sourceId + "/" + props.sourceVersion
        eventSource = new EventSource(errorUrl)
        eventSource.onmessage =  (event: MessageEvent) => {
            setErrorMessage(event.data)
        }
    }

    return (
        <React.Fragment>{
            !message ? null :
            <div id={props.id} className={props.className}>{message}</div>
        }</React.Fragment>
    )
}

class Props {
    id: string
    sourceId: string
    sourceVersion: string
    className: string
}