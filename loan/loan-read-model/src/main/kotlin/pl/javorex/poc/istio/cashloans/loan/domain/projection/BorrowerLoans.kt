package pl.javorex.poc.istio.cashloans.loan.domain.projection

import pl.javorex.poc.istio.cashloans.loan.domain.event.LoanCreated

data class BorrowerLoans(
    val borrowerName: String,
    val loanNumbers: MutableList<String> = arrayListOf()
) : DomainProjection {

    fun accept(loanCreated: LoanCreated) {
        loanNumbers.add(loanCreated.loanNumber)
    }
}