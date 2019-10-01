package pl.javorex.poc.istio.cashloans.loan.adapter.event

import pl.javorex.poc.istio.cashloans.instalment.domain.event.InstalmentCalculated
import pl.javorex.poc.istio.cashloans.loan.application.LoanCommandFacade
import pl.javorex.poc.istio.cashloans.loan.application.command.CreateLoan
import pl.javorex.poc.istio.common.message.MessageBus
import pl.javorex.poc.istio.common.message.async.CurrentMessages
import pl.javorex.poc.istio.common.message.listener.AsyncMessageCallback

class SaveLoanCallback(
        private val loanCommandFacade: LoanCommandFacade
) : AsyncMessageCallback {

    override fun onComplete(sourceId: String, sourceVersion: Long, messages: CurrentMessages, messageBus: MessageBus) {
        loanCommandFacade.saveLoan(
                messages[CreateLoan::class.java],
                messages[InstalmentCalculated::class.java],
                LoanMessageBusAdapter(messageBus)
        )
    }
}