package pl.javorex.poc.istio.cashloans.loan.adapter.config

import pl.javorex.poc.istio.cashloans.loan.adapter.OfferAcceptedKStream
import pl.javorex.poc.istio.cashloans.loan.adapter.CreateLoanKStream
import pl.javorex.poc.istio.cashloans.loan.adapter.event.CalculateInstalmentCallback
import pl.javorex.poc.istio.cashloans.loan.adapter.event.CreateLoanCallback
import pl.javorex.poc.istio.cashloans.loan.adapter.event.SaveLoanCallback
import pl.javorex.poc.istio.cashloans.loan.application.LoanCommandFacade

class LoanKStreamAdapterConfig(
    private val bootstrapServers: String,
    private val offerTopic: String,
    private val loanTopic: String,
    private val loanErrorTopic: String,
    private val instalmentTopic: String,
    private val instalmentErrorTopic: String,
    private val productTopic: String,
    private val productErrorTopic: String,
    private val loanCommandFacade: LoanCommandFacade
) {
    fun offerAcceptedKStream() = OfferAcceptedKStream(
            bootstrapServers,
            offerTopic,
            loanTopic,
            loanErrorTopic,
            uniqueOfferAcceptedEventVersionListener()
    )

    fun loanCreationKStream() = CreateLoanKStream(bootstrapServers, loanTopic,
            loanErrorTopic, instalmentTopic, instalmentErrorTopic, productTopic, productErrorTopic,
            calculateInstalmentCallback(), saveLoanCallback())

    private fun uniqueOfferAcceptedEventVersionListener() = CreateLoanCallback(loanCommandFacade)
    private fun calculateInstalmentCallback() = CalculateInstalmentCallback(loanCommandFacade)
    private fun saveLoanCallback() = SaveLoanCallback(loanCommandFacade)
}