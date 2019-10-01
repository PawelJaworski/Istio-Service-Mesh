package pl.javorex.poc.istio.cashloans.product.domain

import java.math.BigDecimal

data class LoanProduct(
    val loanCost: BigDecimal = BigDecimal.ZERO
)