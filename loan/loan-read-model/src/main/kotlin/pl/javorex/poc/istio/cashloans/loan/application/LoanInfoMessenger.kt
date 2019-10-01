package pl.javorex.poc.istio.cashloans.loan.application

import pl.javorex.poc.istio.cashloans.loan.domain.event.LoanCreated
import pl.javorex.poc.istio.common.message.MessageBus
import pl.javorex.poc.istio.common.message.common.Info

internal object LoanInfoMessenger {
    fun publish(sourceId: String, sourceVersion: Long, loanCreated: LoanCreated, messageBus: MessageBus) {
        val sourceId = loanCreated.sourceId
        val sourceVersion = loanCreated.sourceVersion
        val info =
            Info("Loan ${loanCreated.loanNumber} with instalment ${loanCreated.instalmentAmount} ready.")
        messageBus.emit(sourceId, sourceVersion, info)
    }
}