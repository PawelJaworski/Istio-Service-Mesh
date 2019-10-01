package pl.javorex.poc.istio.cashloans.instalment.domain.vo

import java.math.BigDecimal

data class LoanCost(val value: BigDecimal) {
    init {
        check(value > BigDecimal.ZERO) {
            "Loan-cost have to be greater than 0."
        }
    }
}