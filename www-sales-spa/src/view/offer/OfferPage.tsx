import React from "react"
import {OfferForm} from "./component/OfferForm";
import {LoanInfo} from "./component/LoanInfo";
import {LoanError} from "./component/LoanError";
import {OfferError} from "./component/OfferError";
import {NavigationRight} from "../app/component/NavigationRight";

const OfferPage = () => {
    return (
        <div className="container">
            <NavigationRight/>
            <div className={rowCss + " section-message"}>
                <LoanInfo/>
                <OfferError/>
                <LoanError/>
            </div>
            <OfferForm/>
        </div>
    );
}

const rowCss = "col-md-6 offset-md-3"

export default OfferPage
