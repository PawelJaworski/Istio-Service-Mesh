package pl.javorex.poc.istio.cashloans.instalment.domain.event

import java.math.BigDecimal

data class InstalmentCalculated(val amount: BigDecimal = BigDecimal.ZERO) : InstalmentEvent