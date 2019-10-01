package pl.javorex.poc.istio.cashloans.loan.domain

interface LoanNoGenerator {
    fun generateLoanNo(product: String): String
}