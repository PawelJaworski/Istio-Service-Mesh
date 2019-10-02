import React, {useEffect} from "react";
import {OfferKey} from "../../model/offer/OfferKey";

export const OfferFormWc = (props: {
    id: String,
    onOfferChange: (offerKey: OfferKey) => void,
    onOfferError: (error: string) => void
}) => {

    const _onOfferVersionChanged: EventListener = ((event: CustomEvent) => {
        const {offerId} = event.detail
        const {offerVersion} = event.detail

        if (offerId === undefined) {
            throw new Error("Offer-Id undefined")
        }
        if (offerVersion === undefined) {
            throw new Error("Offer-Version undefined")
        }

        const offerKey: OfferKey = {
            offerId: offerId,
            offerVersion: offerVersion
        }

        props.onOfferChange(offerKey)
    }) as EventListener

    const _onOfferError: EventListener = ((event: CustomEvent) => {

        const error = event.detail
        props.onOfferError(error)
    }) as EventListener

    unsubscribeLoanSourceChanged()
    subscribeOfferChanged()
    unsubscribeOfferError()
    subscribeOfferError()

    useEffect(() => {
        return function cleanup() {
            unsubscribeLoanSourceChanged()
            unsubscribeOfferError()
        };
    }, [])

    function subscribeOfferChanged() {
        document.addEventListener(
            props.id + ".onOfferVersionChanged",
            _onOfferVersionChanged
        );
    }

    function unsubscribeLoanSourceChanged() {
        document.removeEventListener(
            props.id + ".onOfferVersionChanged",
            _onOfferVersionChanged
        );
    }

    function subscribeOfferError() {
        document.addEventListener(
            props.id + ".onOfferError",
            _onOfferError
        );
    }

    function unsubscribeOfferError() {
        document.removeEventListener(
            props.id + ".onOfferError",
            _onOfferError
        );
    }

    return <offer-form id={props.id} />
}