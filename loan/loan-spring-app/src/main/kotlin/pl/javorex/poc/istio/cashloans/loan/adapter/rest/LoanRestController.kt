package pl.javorex.poc.istio.cashloans.loan.adapter.rest

import org.reactivestreams.Publisher
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import pl.javorex.poc.istio.cashloans.loan.application.LoanCommandFacade
import pl.javorex.poc.istio.cashloans.loan.application.LoanMessagePublisher

@RestController
@RequestMapping("/loan")
class LoanRestController(
    private val loanCommandFacade: LoanCommandFacade,
    private val loanMessagePublisher: LoanMessagePublisher
) {

    @GetMapping(
        path = ["/info/{sourceId}/{sourceVersion}"],
        produces = [MediaType.TEXT_EVENT_STREAM_VALUE]
    )
    fun getLoanInfo(
        @PathVariable("sourceId") sourceId: String,
        @PathVariable("sourceVersion") sourceVersion: Long
    ): Publisher<String> {
        return loanMessagePublisher.ofInfo(sourceId, sourceVersion)
    }

    @GetMapping(
        path = ["/error/{sourceId}/{sourceVersion}"],
        produces = [MediaType.TEXT_EVENT_STREAM_VALUE]
    )
    fun getLoanErrors(
        @PathVariable("sourceId") sourceId: String,
        @PathVariable("sourceVersion") sourceVersion: Long
    ): Publisher<String> {
        return loanMessagePublisher.ofError(sourceId, sourceVersion)
    }
}