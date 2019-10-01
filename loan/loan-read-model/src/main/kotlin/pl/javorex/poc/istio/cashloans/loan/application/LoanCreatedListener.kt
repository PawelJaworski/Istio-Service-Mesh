package pl.javorex.poc.istio.cashloans.loan.application

import pl.javorex.poc.istio.cashloans.loan.domain.event.LoanCreated
import pl.javorex.poc.istio.cashloans.loan.domain.projection.BorrowerLoansRepository
import pl.javorex.poc.istio.cashloans.loan.domain.projection.BorrowerLoansProjection
import pl.javorex.poc.istio.common.message.MessageBus
import pl.javorex.poc.istio.common.message.listener.MessageListener

internal class LoanCreatedListener(
    borrowerLoansRepository: BorrowerLoansRepository
) : MessageListener<LoanCreated> {

    private val borrowerLoansProjection = BorrowerLoansProjection(borrowerLoansRepository)

    override fun onMessage(sourceId: String, sourceVersion: Long, loanCreated: LoanCreated,
                           messageBus: MessageBus
    ) {
        borrowerLoansProjection.accept(loanCreated)

        LoanInfoMessenger.publish(sourceId, sourceVersion, loanCreated, messageBus)
    }
}