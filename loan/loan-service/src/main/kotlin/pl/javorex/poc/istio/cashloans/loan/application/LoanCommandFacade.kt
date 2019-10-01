package pl.javorex.poc.istio.cashloans.loan.application

import pl.javorex.poc.istio.cashloans.product.application.query.GetLoanCost
import pl.javorex.poc.istio.cashloans.product.application.query.LoanCostFetched
import pl.javorex.poc.istio.cashloans.loan.domain.LoanNoGenerator
import pl.javorex.poc.istio.cashloans.instalment.application.command.CalculateInstalment
import pl.javorex.poc.istio.cashloans.instalment.domain.event.InstalmentCalculated
import pl.javorex.poc.istio.cashloans.loan.application.command.CreateLoan
import pl.javorex.poc.istio.cashloans.loan.application.event.LoanMessageBus
import pl.javorex.poc.istio.cashloans.loan.domain.event.LoanCreated

class LoanCommandFacade(
        private val loanNoGenerator: LoanNoGenerator
) {
    fun createLoan(createLoan: CreateLoan, messageBus: LoanMessageBus) {
        val sourceId = createLoan.sourceId
        val sourceVersion = createLoan.sourceVersion

        messageBus.emit(sourceId, sourceVersion, createLoan)
    }

    fun getLoanCost(createLoan: CreateLoan, messageBus: LoanMessageBus) {
        val sourceId = createLoan.sourceId
        val sourceVersion = createLoan.sourceVersion
        val productName = createLoan.loanProduct

        val getLoanCost = GetLoanCost(productName)

        messageBus.emit(sourceId, sourceVersion, getLoanCost)
    }

    fun calculateInstalment(createLoan: CreateLoan, loanCost: LoanCostFetched, messageBus: LoanMessageBus) {
        val sourceId = createLoan.sourceId
        val sourceVersion = createLoan.sourceVersion

        val calculateInstalment = CalculateInstalment(
                sourceId,
                sourceVersion,
                loanCost.amount,
                createLoan.numberOfInstalment
        )

        messageBus.emit(sourceId, sourceVersion, calculateInstalment)
    }

    fun saveLoan(createLoan: CreateLoan, instalmentCalculated: InstalmentCalculated,
                 messageBus: LoanMessageBus) {
        val loanId = "loan-${System.currentTimeMillis()}"
        val loanNo = loanNoGenerator.generateLoanNo(
                createLoan.loanProduct
        )
        val borrowerName = createLoan.borrowerName
        val instalmentAmount = instalmentCalculated.amount

        val loanCreated = LoanCreated(loanId, createLoan.sourceId, createLoan.sourceVersion, loanNo, borrowerName, instalmentAmount)

        messageBus.emit(loanCreated.loanId, 1, loanCreated)
    }
}