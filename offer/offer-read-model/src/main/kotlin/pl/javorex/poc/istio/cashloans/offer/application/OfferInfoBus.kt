package pl.javorex.poc.istio.cashloans.offer.application

import pl.javorex.poc.istio.common.message.common.Info

interface OfferInfoBus {
    fun emit(message: Info, sourceId: String, sourceVersion: Long)
}