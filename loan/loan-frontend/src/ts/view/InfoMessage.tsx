import * as React from "react";
import { string as prop_string, number as prop_number } from "prop-types";
import { useEffect, useState } from "react";;

export const InfoMessagePropTypes = {
    id: prop_string,
    sourceId: prop_string,
    sourceVersion: prop_number,
    className: prop_string
}

export const InfoMessage = (props: Props) => {
    const [ message, setMessage ] = useState("");

    let eventSource: EventSource = undefined
    useEffect(() => {
        listenLoanMessages()

        return function cleanup() {
            if (eventSource) {
                eventSource.close()
                eventSource = undefined
            }
        };
    })

    const listenLoanMessages = () => {
        if (!props.sourceId || !props.sourceVersion) {
            setMessage("")
            return
        }

        const eventsUrl = "/loan/info/" + props.sourceId + "/" + props.sourceVersion
        eventSource = new EventSource(eventsUrl)
        eventSource.onmessage =  (event: MessageEvent) => {
            setMessage(event.data)
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