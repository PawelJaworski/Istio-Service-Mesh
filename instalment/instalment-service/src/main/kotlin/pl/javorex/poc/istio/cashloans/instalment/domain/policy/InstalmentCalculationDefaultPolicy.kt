package pl.javorex.poc.istio.cashloans.instalment.domain.policy

import pl.javorex.poc.istio.cashloans.instalment.domain.vo.LoanCost
import pl.javorex.poc.istio.cashloans.instalment.domain.vo.NumberOfInstalment
import java.math.BigDecimal
import java.math.RoundingMode

internal object InstalmentCalculationDefaultPolicy {

    fun applyTo(numberOfInstalment: NumberOfInstalment, loanCost: LoanCost): BigDecimal {
        return loanCost.value.divide(
                BigDecimal.valueOf(numberOfInstalment.value.toLong()),
                RoundingMode.HALF_UP
        )
    }
}