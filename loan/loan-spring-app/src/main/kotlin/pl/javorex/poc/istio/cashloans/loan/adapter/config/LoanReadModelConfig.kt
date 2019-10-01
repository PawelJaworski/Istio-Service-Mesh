package pl.javorex.poc.istio.cashloans.loan.adapter.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.javorex.poc.istio.cashloans.loan.adapter.BorrowerLoansRepositoryInMemoryImpl
import pl.javorex.poc.istio.cashloans.loan.adapter.LoanCreatedKStream
import pl.javorex.poc.istio.cashloans.loan.adapter.LoanMessagePublisherKafkaImpl
import pl.javorex.poc.istio.cashloans.loan.application.LoanQueryFacade

@Configuration
class LoanReadModelConfig(
    @Value("\${kafka.bootstrap-servers}") private val bootstrapServers: String,
    @Value("\${kafka.topic.loan-events}") private val loanTopic: String,
    @Value("\${kafka.topic.loan-error-events}") private val loanErrorTopic: String
) {
    @Bean
    fun loanMessagePublisher() = LoanMessagePublisherKafkaImpl(bootstrapServers, loanTopic,
        loanErrorTopic)

    @Bean
    fun loanCreatedKStream() = LoanCreatedKStream(bootstrapServers, loanTopic, loanErrorTopic,
        borrowerLoansRepository())

    @Bean
    fun loanQueryFacade() = LoanQueryFacade(borrowerLoansRepository())

    @Bean
    fun borrowerLoansRepository() = BorrowerLoansRepositoryInMemoryImpl()
}