package pl.javorex.poc.istio.cashloans.offer.application.integration

interface UserReadClient {
    fun getUserFullName(): String
}