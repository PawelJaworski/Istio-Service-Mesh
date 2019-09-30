package pl.javorex.poc.istio.cashloans.offer.adapter.spring.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.stereotype.Component

@RefreshScope
@Component
class KafkaConfig(
        @Value("\${kafka.bootstrap-servers}") val bootstrapServers: String,
        @Value("\${kafka.topic.offer-messages}") val offerTopic: String
)