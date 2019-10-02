package pl.javorex.poc.istio.cashloans.loan.adapter.rest

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.javorex.poc.istio.cashloans.loan.application.LoanQueryFacade
import pl.javorex.poc.istio.cashloans.loan.domain.projection.BorrowerLoans

@RestController
@RequestMapping("/borrowerLoan")
class BorrowerLoansRestController(
    private val loanQueryFacade: LoanQueryFacade
) {
    @GetMapping("/all")
    fun getAll(): ResponseEntity<List<BorrowerLoans>> {

        val borrowerLoans = loanQueryFacade.findAllBorrowerLoans()

        return ResponseEntity.ok(borrowerLoans)
    }
}