package pl.javorex.poc.istio.cashloans.offer.adapter.spring.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.javorex.poc.istio.cashloans.offer.adapter.OfferInfoBusKafkaImpl
import pl.javorex.poc.istio.cashloans.offer.adapter.OfferInfoKStream
import pl.javorex.poc.istio.cashloans.offer.adapter.OfferMessagePublisherKafkaImpl

@Configuration
internal class OfferReadModelConfig(
    private val kafka: KafkaConfig
) {
    @Bean
    fun offerMessagePublisher() = OfferMessagePublisherKafkaImpl(kafka.bootstrapServers, kafka.offerTopic)

    @Bean
    fun offerInfoKStream() = OfferInfoKStream(kafka.bootstrapServers, kafka.offerTopic)

    @Bean
    fun offerInfoBus() = OfferInfoBusKafkaImpl(kafka.bootstrapServers, kafka.offerTopic)
}