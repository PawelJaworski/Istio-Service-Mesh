package pl.javorex.poc.istio.cashloans.instalment.adapter.spring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class InstalmentSpringApplication

fun main(args: Array<String>) {
	runApplication<InstalmentSpringApplication>(*args)
}
