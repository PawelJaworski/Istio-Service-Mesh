package pl.javorex.poc.istio.cashloans.loan.domain.event

import java.math.BigDecimal

data class LoanCreated(
        val loanId: String = "",
        val sourceId: String,
        val sourceVersion: Long,
        val loanNumber: String = "",
        val borrowerName: String = "",
        val instalmentAmount: BigDecimal = BigDecimal.ZERO
)