package pl.javorex.poc.istio.cashloans.instalment.adapter.spring.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class KafkaConfig(
        @Value("\${kafka.bootstrap-servers}") internal val bootstrapServers: String,
        @Value("\${kafka.topic.instalment-events}") internal val instalmentTopic: String,
        @Value("\${kafka.topic.instalment-error-events}") internal val instalmentErrorTopic: String,
        @Value("\${kafka.topic.loan-events}") internal val loanTopic: String
)