import * as React from "react";
import * as POST from "../model/POST";
import * as GET from "../model/GET";
import {NumberInput, ReadOnly} from "./Form";
import {useEffect, useState} from "react";
import {func as prop_func, string as prop_string, number as prop_number} from "prop-types";


export const OfferForm = (props: OfferCriteriaProps) => {
    const [numberOfInstalment, setNumberOfInstalment] = useState(undefined)
    let offerId = undefined
    let offerVersion = undefined

    init()

    function init() {
        GET.nextOfferId()
            .then(id => {
                offerId = id
            })
            .then(() => {
                GET.nextOfferVersion()
                    .then(version => {
                        offerVersion = version
                        props.onOfferVersionChanged({
                            offerId: offerId,
                            offerVersion: offerVersion
                        })
                    })
            })
    }

    function onNumberOfInstalmentChange(event) {
        setNumberOfInstalment(event.target.value);
    }

    function onSubmit(event) {
        const validationError = validateForm()
        if (validationError) {
            props.onOfferError(validationError)
            return
        }

        POST.offerAccepted(offerId, offerVersion, "GREAT_PRODUCT", numberOfInstalment)
            .catch(reason => {
                const errMsg = "Offer-Service " + JSON.stringify(reason.response.data) + "."
                props.onOfferError(errMsg)
            })
    }

    function validateForm(): string {
        const required = []
        if (numberOfInstalment === undefined) {
            required.push("Number of Instalment")
        }

        if (required.length > 0) {
            return required.join(", ") + " required."
        }

        return undefined
    }
    return (
        <div id={props.id}>
            <div className={rowCss}>
                <h1 className="header text-center">
                    Loan Offer
                </h1>
            </div>
            <div className={rowCss}>
                <form>
                    <ReadOnly label="Loan Product" id="loanProduct" value="BESTSELLING_LOAN"/>
                    <NumberInput label="Number of instalment per year" id="numberOfInstalment" placeholder="1, 2, 6 or 12"
                                 onChange={onNumberOfInstalmentChange}/>
                </form>
            </div>
            <div className={rowCss}>
                <button className="btn btn-primary btn-right" onClick={onSubmit}>Submit</button>
            </div>
        </div>
    )
}

class OfferCriteriaProps {
    id: string
    onOfferVersionChanged: (offerVersion: {offerId: string, offerVersion: number}) => void
    onOfferError: (error: string) => void
}

OfferForm.propTypes = {
    id: prop_string,
    onOfferVersionChanged: prop_func,
    onOfferError: prop_func
}

const rowCss = "col-md-6 offset-md-3"