package pl.javorex.poc.istio.cashloans.instalment.domain.event

data class InstalmentCalculationFailed(val error: String = "") : InstalmentEvent