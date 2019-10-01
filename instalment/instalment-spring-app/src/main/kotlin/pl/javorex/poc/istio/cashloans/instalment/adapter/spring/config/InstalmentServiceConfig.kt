package pl.javorex.poc.istio.cashloans.instalment.adapter.spring.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.javorex.poc.istio.cashloans.instalment.adapter.kstream.InstalmentCommandKStream
import pl.javorex.poc.istio.cashloans.instalment.application.CalculateInstalmentCommandListener
import pl.javorex.poc.istio.cashloans.instalment.application.InstalmentCommandFacade

@Configuration
class InstalmentServiceConfig(
    private val kafkaConfig: KafkaConfig
) {

    @Bean
    fun instalmentCommandKStream() = InstalmentCommandKStream(
            kafkaConfig.bootstrapServers,
            kafkaConfig.instalmentTopic,
            kafkaConfig.instalmentErrorTopic,
            kafkaConfig.loanTopic,
            instalmentCommandListener()
    )

    @Bean
    fun instalmentCommandListener() = CalculateInstalmentCommandListener(
            instalmentCommandFacade()
    )

    @Bean
    fun instalmentCommandFacade() = InstalmentCommandFacade()
}