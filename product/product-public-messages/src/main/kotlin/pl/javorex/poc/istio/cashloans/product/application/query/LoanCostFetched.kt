package pl.javorex.poc.istio.cashloans.product.application.query

import java.math.BigDecimal

data class LoanCostFetched(val amount: BigDecimal = BigDecimal.ZERO)