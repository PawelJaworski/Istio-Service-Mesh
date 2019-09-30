package pl.javorex.poc.istio.cashloans.offer.application

import pl.javorex.poc.istio.cashloans.offer.domain.event.OfferMessage

interface OfferMessageBus {
    fun emit(offerMessage: OfferMessage, offerId: String, offerVersion: Long)
}