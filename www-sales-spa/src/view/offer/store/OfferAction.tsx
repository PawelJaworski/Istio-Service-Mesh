import {OfferKey} from "../../../model/offer/OfferKey";
import {PayloadAction} from "typesafe-actions/dist/types";
import {action} from "typesafe-actions";

export enum OfferAction {
    OFFER_KEY_RES = "@@OFFER_KEY_RESPONSE",
    OFFER_ERR = "@@OFFER_ERROR_OCCURED"
}

export const dispatchOfferKey = (offerKey: OfferKey): PayloadAction<string, OfferKey> =>
    action(OfferAction.OFFER_KEY_RES, offerKey)
export const dispatchOfferErr = (offerKey: string): PayloadAction<string, string> =>
    action(OfferAction.OFFER_ERR, offerKey)