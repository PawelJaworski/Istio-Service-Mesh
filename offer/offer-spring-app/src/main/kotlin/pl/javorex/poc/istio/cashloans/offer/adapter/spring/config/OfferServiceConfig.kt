package pl.javorex.poc.istio.cashloans.offer.adapter.spring.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.javorex.poc.istio.cashloans.offer.adapter.OfferMessageBusKafkaImpl
import pl.javorex.poc.istio.cashloans.offer.application.OfferCommandFacade
import pl.javorex.poc.istio.cashloans.offer.application.integration.UserReadClient

@Configuration
internal class OfferServiceConfig(
    val userReadClient: UserReadClient,
    val kafka: KafkaConfig
) {
    @Bean
    fun offerCommandFacade() = OfferCommandFacade(userReadClient)

    @Bean
    fun offerMessageBus() = OfferMessageBusKafkaImpl(kafka.bootstrapServers, kafka.offerTopic)
}