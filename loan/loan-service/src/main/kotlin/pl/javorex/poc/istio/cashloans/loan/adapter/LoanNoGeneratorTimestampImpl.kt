package pl.javorex.poc.istio.cashloans.loan.adapter

import pl.javorex.poc.istio.cashloans.loan.domain.LoanNoGenerator

class LoanNoGeneratorTimestampImpl : LoanNoGenerator {
    var start = System.currentTimeMillis()

    override fun generateLoanNo(product: String) = "$product/${start++}"
}