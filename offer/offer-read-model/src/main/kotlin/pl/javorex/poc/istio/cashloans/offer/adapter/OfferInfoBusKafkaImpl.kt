package pl.javorex.poc.istio.cashloans.offer.adapter

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import pl.javorex.poc.istio.cashloans.offer.application.OfferInfoBus
import pl.javorex.poc.istio.common.kafka.streams.message.MessageEnvelopeSerde
import pl.javorex.poc.istio.common.message.common.Info
import pl.javorex.poc.istio.common.message.envelope.MessageEnvelope
import pl.javorex.poc.istio.common.message.envelope.pack
import java.util.*

class OfferInfoBusKafkaImpl(
    bootstrapServers: String,
    private val offerTopic: String
) : OfferInfoBus {

    private val kafkaProducer = createProducer(bootstrapServers)

    override fun emit(info: Info, offerId: String, offerVersion: Long) {
        val messageEnvelope = pack(offerId, offerVersion, info)

        kafkaProducer.send(
            ProducerRecord(offerTopic, offerId, messageEnvelope)
        )
    }
}

private fun createProducer(bootstrapServers: String): Producer<String, MessageEnvelope> {
    val props = Properties()
    props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
    props[ProducerConfig.CLIENT_ID_CONFIG] = "OfferInfoProducer"
    props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
    props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = MessageEnvelopeSerde().serializer()::class.java.name

    return KafkaProducer(props)
}