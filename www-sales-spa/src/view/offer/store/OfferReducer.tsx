import {Reducer} from "redux";
import {OfferPageState} from "../../app/store/AppState";
import {OfferAction} from "./OfferAction";
import {OfferKey} from "../../../model/offer/OfferKey";

const initialState: OfferPageState = new OfferPageState()

export function offerReducer(): Reducer<OfferPageState> {
    return (state = initialState, action) => {
        const {type, payload} = action

        switch (type) {
            case OfferAction.OFFER_KEY_RES:
                return setOfferKey(state, payload)
            case OfferAction.OFFER_ERR:
                return setOfferErr(state, payload)

            default: {
                return state
            }
        }
    }
}

export const setOfferKey = (state: OfferPageState, offerKey: OfferKey) => {
    return {
        ...state,
        offerKey: offerKey
    }
}

export const setOfferErr = (state: OfferPageState, error: string) => {
    return {
        ...state,
        offerError: error
    }
}