package pl.javorex.poc.istio.cashloans.offer.adapter

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import pl.javorex.poc.istio.cashloans.offer.application.OfferMessagePublisher
import pl.javorex.poc.istio.common.kafka.streams.message.MessageEnvelopeDeserializer
import pl.javorex.poc.istio.common.message.common.Info
import pl.javorex.poc.istio.common.message.envelope.MessageEnvelope
import reactor.core.publisher.ConnectableFlux
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.receiver.ReceiverRecord
import java.util.HashMap

class OfferMessagePublisherKafkaImpl(
    private val bootstrapServers: String,
    offerTopic: String
) : OfferMessagePublisher {

    private val infoFlux: ConnectableFlux<ReceiverRecord<String, MessageEnvelope>> = KafkaReceiver
        .create<String, MessageEnvelope>(
            subscriptionOf("Offer-Messages")
                .subscription(listOf(offerTopic))
        )
        .receive()
        .replay(0)

    override fun ofInfo(offerId: String, offerVersion: Long) = infoFlux
        .autoConnect()
        .filter{it.value().isTypeOf(Info::class.java)}
        .map{ it.value()}
        .filter{ it.sourceId == offerId && it.sourceVersion == offerVersion}
        .map{ it.unpack(Info::class.java) }
        .map{ it.message }!!

    private fun subscriptionOf(client: String): ReceiverOptions<String, MessageEnvelope> {
        val props = HashMap<String, Any>()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[ConsumerConfig.GROUP_ID_CONFIG] = "$client-groupId"
        props[ConsumerConfig.CLIENT_ID_CONFIG] = "$client-clientId"
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "latest"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = MessageEnvelopeDeserializer::class.java

        return ReceiverOptions.create<String, MessageEnvelope>(props)
    }
}