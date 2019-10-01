package pl.javorex.poc.istio.cashloans.loan.adapter.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.javorex.poc.istio.cashloans.loan.adapter.LoanNoGeneratorTimestampImpl
import pl.javorex.poc.istio.cashloans.loan.application.LoanCommandFacade

@Configuration
internal class SpringConfig(
    @Value("\${kafka.bootstrap-servers}") private val bootstrapServers: String,
    @Value("\${kafka.topic.offer-events}") private val offerTopic: String,
    @Value("\${kafka.topic.loan-events}") private val loanTopic: String,
    @Value("\${kafka.topic.loan-error-events}") private val loanErrorTopic: String,
    @Value("\${kafka.topic.instalment-events}") private val instalmentTopic: String,
    @Value("\${kafka.topic.instalment-error-events}") private val instalmentErrorTopic: String,
    @Value("\${kafka.topic.product-events}") private val productTopic: String,
    @Value("\${kafka.topic.product-error-events}") private val productErrorTopic: String
) {
    @Bean
    fun loanKStreamConfig() = LoanKStreamAdapterConfig(bootstrapServers, offerTopic, loanTopic,
            loanErrorTopic, instalmentTopic, instalmentErrorTopic, productTopic, productErrorTopic,
            loanCommandFacade())

    @Bean
    fun offerAcceptedKStream() = loanKStreamConfig().offerAcceptedKStream()

    @Bean
    fun loanCreationKStream() = loanKStreamConfig().loanCreationKStream()

    @Bean
    fun loanCommandFacade() = LoanCommandFacade(loanNoGenerator())

    @Bean
    fun loanNoGenerator() = LoanNoGeneratorTimestampImpl()
}