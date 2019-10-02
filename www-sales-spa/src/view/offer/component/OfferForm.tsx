import {connect} from "react-redux";
import {ApplicationState} from "../../app/store/AppState";
import {OfferKey} from "../../../model/offer/OfferKey";
import {dispatchOfferErr, dispatchOfferKey} from "../store/OfferAction";
import {OfferFormWc} from "../../../integration/offer/OfferFormWc";

const mapStateToProps = ({ offerPage }: ApplicationState) => ({
    id: "offer-criteria"
})

const mapDispatchToProps = (dispatch: any) => {
    return {
        onOfferChange: (offerKey: OfferKey) => {
            return dispatch(dispatchOfferKey(offerKey))
        },
        onOfferError: (error: string) => {
            return dispatch(dispatchOfferErr(error))
        }
    }
}

export const OfferForm = connect(mapStateToProps, mapDispatchToProps)(OfferFormWc)