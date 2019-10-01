package pl.javorex.poc.istio.cashloans.loan.domain.projection

import pl.javorex.poc.istio.cashloans.loan.domain.event.LoanCreated

internal class BorrowerLoansProjection(private val borrowerLoansRepository: BorrowerLoansRepository) {

    fun accept(loanCreated: LoanCreated) {
        val insuredName = loanCreated.borrowerName

        val borrowerLoans = borrowerLoansRepository
            .findByInsuredName(insuredName) ?: BorrowerLoans(insuredName)

        borrowerLoans.accept(loanCreated)

        borrowerLoansRepository.save(borrowerLoans)
    }

}