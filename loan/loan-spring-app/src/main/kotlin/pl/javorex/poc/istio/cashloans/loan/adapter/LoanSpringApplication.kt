package pl.javorex.poc.istio.cashloans.loan.adapter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LoanSpringApplication

fun main(args: Array<String>) {
	runApplication<LoanSpringApplication>(*args)
}
