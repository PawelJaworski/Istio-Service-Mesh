import React from "react";
import {ShowUserInfoBtn} from "../../offer/component/UserInfo";
import {LogoutBtn} from "./LogoutBtn";
import {CopyTokenToClipBoardBtn} from "./CopyTokenToClipBoard";

export const NavigationRight = () => {
    return (
        <div className="right">
            <div><LogoutBtn className="navigation"/></div>
            <div><ShowUserInfoBtn className="navigation"/></div>
            <div><CopyTokenToClipBoardBtn className="navigation"/></div>
        </div>
    )
}