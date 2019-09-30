package pl.javorex.poc.istio.cashloans.offer.adapter.spring.integration

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.Series.CLIENT_ERROR
import org.springframework.http.HttpStatus.Series.SERVER_ERROR
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.ResponseErrorHandler
import java.lang.IllegalStateException
import java.net.URI

class RestTemplateResponseErrorHandler : ResponseErrorHandler {
    override fun hasError(httpResponse: ClientHttpResponse): Boolean {
        return (
                httpResponse.statusCode.series() == CLIENT_ERROR
                        || httpResponse.statusCode.series() == SERVER_ERROR)
    }

    override fun handleError(url: URI, method: HttpMethod, response: ClientHttpResponse) {
        println("handling error")
        throw IllegalStateException("$url request error: ${response.statusText}")

    }

    override fun handleError(httpResponse: ClientHttpResponse) {
        throw NotImplementedError()
    }
}
