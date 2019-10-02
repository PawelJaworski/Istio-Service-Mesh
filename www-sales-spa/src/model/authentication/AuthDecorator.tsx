import React, {FunctionComponent} from "react";
import {useAuthentication} from "./AuthHook";

export const Authorization: FunctionComponent<{resource: any}> = (props) => {
    let isAuthenticated = useAuthentication()

    if (isAuthenticated) {
        return props.resource
    }

    return <div>Trying to authenticate...</div>
}