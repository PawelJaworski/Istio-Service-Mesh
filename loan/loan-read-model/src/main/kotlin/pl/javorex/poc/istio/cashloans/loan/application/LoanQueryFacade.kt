package pl.javorex.poc.istio.cashloans.loan.application

import pl.javorex.poc.istio.cashloans.loan.domain.projection.BorrowerLoans
import pl.javorex.poc.istio.cashloans.loan.domain.projection.BorrowerLoansRepository

class LoanQueryFacade(
    private val borrowerLoansRepository: BorrowerLoansRepository
) {
    fun findAllBorrowerLoans() : List<BorrowerLoans> = borrowerLoansRepository.findAll()
}