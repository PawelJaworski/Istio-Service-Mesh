package pl.javorex.poc.istio.cashloans.offer.application

import pl.javorex.poc.istio.cashloans.offer.application.command.AcceptOffer
import pl.javorex.poc.istio.cashloans.offer.application.integration.UserReadClient
import pl.javorex.poc.istio.cashloans.offer.domain.event.OfferAccepted

class OfferCommandFacade(
        private val userReadClient: UserReadClient
) {
    fun acceptOffer(command: AcceptOffer, messageBus: OfferMessageBus) {
        val insuredName = userReadClient.getUserFullName()

        val offerAccepted = OfferAccepted(
            command.loanProduct,
            command.numberOfInstalment,
            insuredName
        )

        messageBus.emit(offerAccepted, command.offerId, command.offerVersion)
    }
}