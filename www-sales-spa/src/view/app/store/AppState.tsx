import {OfferKey} from "../../../model/offer/OfferKey";

export interface ApplicationState {
    offerPage: OfferPageState
}

export class OfferPageState {
    readonly offerKey: OfferKey | undefined
    readonly offerError: string | undefined
}

export const offerIdOf = (offerPage: OfferPageState) =>
    offerPage.offerKey ? offerPage.offerKey.offerId : undefined

export const offerVersionOf = (offerPage: OfferPageState) =>
    offerPage.offerKey ? offerPage.offerKey.offerVersion : undefined