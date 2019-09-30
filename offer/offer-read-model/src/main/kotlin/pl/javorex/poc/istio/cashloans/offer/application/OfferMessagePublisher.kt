package pl.javorex.poc.istio.cashloans.offer.application

import org.reactivestreams.Publisher

interface OfferMessagePublisher {
    fun ofInfo(offerId: String, offerVersionId: Long): Publisher<String>
}