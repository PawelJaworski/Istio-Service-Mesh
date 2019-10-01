package pl.javorex.poc.istio.cashloans.loan.adapter

import pl.javorex.poc.istio.cashloans.loan.domain.projection.BorrowerLoans
import pl.javorex.poc.istio.cashloans.loan.domain.projection.BorrowerLoansRepository

class BorrowerLoansRepositoryInMemoryImpl : BorrowerLoansRepository {
    private val records = hashMapOf<String, BorrowerLoans>()

    override fun save(borrowerLoans: BorrowerLoans) {
        records[borrowerLoans.borrowerName] = borrowerLoans
    }

    override fun findByInsuredName(borrowerName: String): BorrowerLoans? =
        records[borrowerName]

    override fun findAll(): List<BorrowerLoans> =
        records.values
            .sortedBy { it.borrowerName }
}