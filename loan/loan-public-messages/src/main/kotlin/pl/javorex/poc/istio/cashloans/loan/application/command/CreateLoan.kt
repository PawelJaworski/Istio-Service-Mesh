package pl.javorex.poc.istio.cashloans.loan.application.command

class CreateLoan(
    val sourceId: String = "",
    val sourceVersion: Long = 0,
    val loanProduct: String = "",
    val numberOfInstalment: Int = 0,
    val borrowerName: String = ""
) : LoanCommand