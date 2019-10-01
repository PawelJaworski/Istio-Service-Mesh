package pl.javorex.poc.istio.cashloans.instalment.application.command

import java.math.BigDecimal

data class CalculateInstalment(
        val sourceId: String = "",
        val sourceVersion: Long = 0,
        val loanCost: BigDecimal = BigDecimal.ZERO,
        val numberOfInstalment: Int = 0
) : InstalmentCommand