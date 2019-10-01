package pl.javorex.poc.istio.cashloans.loan.adapter.event

import pl.javorex.poc.istio.cashloans.loan.application.LoanCommandFacade
import pl.javorex.poc.istio.cashloans.loan.application.command.CreateLoan
import pl.javorex.poc.istio.cashloans.offer.domain.event.OfferAccepted
import pl.javorex.poc.istio.common.message.MessageBus
import pl.javorex.poc.istio.common.message.envelope.MessageEnvelope
import pl.javorex.poc.istio.common.message.listener.MessageUniquenessCallback

class CreateLoanCallback(
        private val loanCommandFacade: LoanCommandFacade
) : MessageUniquenessCallback {
    override fun onUniqueViolated(error: MessageEnvelope, messageBus: MessageBus) {
        messageBus.emitError(error.sourceId, error.sourceVersion, "Offer already submited.")
    }

    override fun onFirst(message: MessageEnvelope, messageBus: MessageBus) {
        val offerAccepted = message.unpack(OfferAccepted::class.java)
        val createLoan = CreateLoan(
                message.sourceId,
                message.sourceVersion,
                offerAccepted.loanProduct,
                offerAccepted.numberOfInstalment,
                offerAccepted.borrowerName
        )
        val messageBus = LoanMessageBusAdapter(messageBus)

        loanCommandFacade.createLoan(createLoan, messageBus)
        loanCommandFacade.getLoanCost(createLoan, messageBus)
    }
}