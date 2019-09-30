package pl.javorex.poc.istio.cashloans.offer.adapter

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import pl.javorex.poc.istio.cashloans.offer.application.OfferMessageBus
import pl.javorex.poc.istio.cashloans.offer.domain.event.OfferMessage
import java.util.*

import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.common.serialization.StringSerializer
import pl.javorex.poc.istio.common.kafka.streams.message.MessageEnvelopeSerde
import pl.javorex.poc.istio.common.message.envelope.MessageEnvelope
import pl.javorex.poc.istio.common.message.envelope.pack

class OfferMessageBusKafkaImpl(
    bootstrapServers: String,
    private val offerTopic: String
) : OfferMessageBus {

    private val producer = createProducer(bootstrapServers)

    override fun emit(offerMessage: OfferMessage, offerId: String, offerVersion: Long) {
        val messageEnvelope = pack(offerId, offerVersion, offerMessage)

        producer.send(
            ProducerRecord(offerTopic, offerId, messageEnvelope)
        )
    }
}

private fun createProducer(bootstrapServers: String): Producer<String, MessageEnvelope> {
    val props = Properties()
    props[BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
    props[CLIENT_ID_CONFIG] = "OfferMessageProducer"
    props[KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
    props[VALUE_SERIALIZER_CLASS_CONFIG] = MessageEnvelopeSerde().serializer()::class.java.name

    return KafkaProducer(props)
}