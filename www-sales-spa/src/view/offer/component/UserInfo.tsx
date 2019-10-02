import React from "react";

export const ShowUserInfoBtn = (props: {className?: String}) => {

    const onClick = () => {
        window.open("user/fullName", "_blank")
    }
    return <button className={"btn btn-link " + props.className} onClick={onClick}>UserInfo</button>
}