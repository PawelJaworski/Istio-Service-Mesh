package pl.javorex.poc.istio.cashloans.offer.application

import pl.javorex.poc.istio.cashloans.offer.domain.event.OfferAccepted
import pl.javorex.poc.istio.common.message.MessageBus
import pl.javorex.poc.istio.common.message.common.Info

object OfferInfoMessenger {
    fun publish(sourceId: String, sourceVersion: Long, offerAccepted: OfferAccepted, messageBus: MessageBus) {
        val info = Info("Offer $sourceId accepted.")

        messageBus.emit(sourceId, sourceVersion, info)
    }
}