package pl.javorex.poc.istio.cashloans.offer.application.command

data class AcceptOffer(
    val offerId: String,
    val offerVersion: Long,
    val loanProduct: String,
    val numberOfInstalment: Int
)