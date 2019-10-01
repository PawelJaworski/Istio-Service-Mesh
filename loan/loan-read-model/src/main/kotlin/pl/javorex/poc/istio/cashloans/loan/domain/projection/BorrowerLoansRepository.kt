package pl.javorex.poc.istio.cashloans.loan.domain.projection

interface BorrowerLoansRepository {
    fun save(borrowerLoans: BorrowerLoans)
    fun findByInsuredName(borrowerName: String) : BorrowerLoans?
    fun findAll() : List<BorrowerLoans>
}