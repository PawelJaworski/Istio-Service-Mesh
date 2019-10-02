import {connect} from "react-redux";
import {ApplicationState} from "../../app/store/AppState";
import React from "react";

export const OfferErrorView = (props: {id: string | undefined, error: string | undefined}) => {
    if (props.error === undefined) {
        return null
    }

    const {id} = props
    const {error} = props

    return <div id={id} className="alert alert-danger">{error}</div>
}

const mapStateToProps = ({ offerPage }: ApplicationState) => ({
    id: "offer-error",
    error: offerPage.offerError
})

export const OfferError = connect(mapStateToProps)(OfferErrorView)