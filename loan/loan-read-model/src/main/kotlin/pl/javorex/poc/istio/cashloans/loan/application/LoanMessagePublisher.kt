package pl.javorex.poc.istio.cashloans.loan.application

import org.reactivestreams.Publisher

interface LoanMessagePublisher {
    fun ofInfo(sourceId: String, sourceVersion: Long) : Publisher<String>
    fun ofError(sourceId: String, sourceVersion: Long) : Publisher<String>
}