package pl.javorex.poc.istio.cashloans.offer.adapter.spring.config

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import pl.javorex.poc.istio.cashloans.offer.adapter.spring.integration.RestTemplateResponseErrorHandler
import pl.javorex.poc.istio.cashloans.offer.adapter.spring.integration.UserReadClientRestImpl

@Configuration
class RestConfig {

    @Bean
    fun userReadClient(restTemplateBuilder: RestTemplateBuilder) =
        UserReadClientRestImpl(
            restTemplateBuilder
                .errorHandler(RestTemplateResponseErrorHandler())
                .build()
        )

    @Bean
    fun restTemplate() = RestTemplate()
}