package pl.javorex.poc.istio.cashloans.offer.domain.event

data class OfferAccepted(
        val loanProduct: String = "",
        val numberOfInstalment: Int = 0,
        val borrowerName: String = ""
) : OfferMessage