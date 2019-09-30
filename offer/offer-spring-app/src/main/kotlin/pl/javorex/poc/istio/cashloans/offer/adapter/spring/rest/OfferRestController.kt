package pl.javorex.poc.istio.cashloans.offer.adapter.spring.rest

import org.reactivestreams.Publisher
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.javorex.poc.istio.cashloans.offer.application.OfferCommandFacade
import pl.javorex.poc.istio.cashloans.offer.application.OfferMessageBus
import pl.javorex.poc.istio.cashloans.offer.application.OfferMessagePublisher
import pl.javorex.poc.istio.cashloans.offer.application.command.AcceptOffer
import pl.javorex.util.functional.Try

@RestController
@RequestMapping("/offer")
internal class OfferRestController(
    private val offerCommandFacade: OfferCommandFacade,
    private val offerMessagePublisher: OfferMessagePublisher,
    private val messageBus: OfferMessageBus
) {
    @PostMapping("/accept")
    fun acceptOffer(@RequestBody acceptOffer: AcceptOffer)=
        Try { offerCommandFacade.acceptOffer(acceptOffer, messageBus) }
             .map { ResponseEntity.ok("OK") }
             .valueOnFailure {
                ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(it)
             }

    @GetMapping(path = ["/info/{offerId}/{offerVersion}"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getLoanCreated(@PathVariable("offerId") offerId: String, @PathVariable("offerVersion") offerVersion: Long)
            : Publisher<String> {
        return offerMessagePublisher.ofInfo(offerId, offerVersion)
    }
}