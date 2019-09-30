package pl.javorex.poc.istio.cashloans.offer.adapter.spring.integration

import org.springframework.web.client.RestTemplate
import pl.javorex.poc.istio.cashloans.offer.application.integration.UserReadClient

private const val USER_REST_ENDPOINT = "http://user-app"
class UserReadClientRestImpl(private val restTemplate: RestTemplate) : UserReadClient {
    override fun getUserFullName(): String =
        restTemplate
                .getForEntity("$USER_REST_ENDPOINT/user/fullName", String::class.java)
                .body!!
}