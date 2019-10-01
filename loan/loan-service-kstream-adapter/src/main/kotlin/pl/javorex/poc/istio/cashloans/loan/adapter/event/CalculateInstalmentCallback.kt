package pl.javorex.poc.istio.cashloans.loan.adapter.event

import pl.javorex.poc.istio.cashloans.product.application.query.LoanCostFetched
import pl.javorex.poc.istio.cashloans.loan.application.LoanCommandFacade
import pl.javorex.poc.istio.cashloans.loan.application.command.CreateLoan
import pl.javorex.poc.istio.common.message.MessageBus
import pl.javorex.poc.istio.common.message.async.CurrentMessages
import pl.javorex.poc.istio.common.message.listener.AsyncMessageCallback

class CalculateInstalmentCallback(
        private val loanCommandFacade: LoanCommandFacade
) : AsyncMessageCallback {
    override fun onComplete(sourceId: String, sourceVersion: Long, messages: CurrentMessages, messageBus: MessageBus) {
        loanCommandFacade.calculateInstalment(
                messages[CreateLoan::class.java],
                messages[LoanCostFetched::class.java],
                LoanMessageBusAdapter(messageBus)
        )
    }
}