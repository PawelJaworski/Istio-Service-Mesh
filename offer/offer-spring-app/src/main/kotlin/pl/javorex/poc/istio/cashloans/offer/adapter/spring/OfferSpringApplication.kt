package pl.javorex.poc.istio.cashloans.offer.adapter.spring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OfferSpringApplication

fun main(args: Array<String>) {
	runApplication<OfferSpringApplication>(*args)
}
