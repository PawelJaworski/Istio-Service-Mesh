package pl.javorex.poc.istio.cashloans.loan.adapter

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import pl.javorex.poc.istio.cashloans.loan.application.LoanMessagePublisher
import pl.javorex.poc.istio.common.kafka.streams.message.MessageEnvelopeDeserializer
import pl.javorex.poc.istio.common.message.common.Info
import pl.javorex.poc.istio.common.message.envelope.MessageEnvelope
import reactor.core.publisher.ConnectableFlux
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.receiver.ReceiverRecord
import java.util.HashMap

class LoanMessagePublisherKafkaImpl(
    private val bootstrapServers: String,
    loanTopic: String,
    loanErrorTopic: String
) : LoanMessagePublisher {

    private val errorFlux: ConnectableFlux<ReceiverRecord<String, MessageEnvelope>> = KafkaReceiver
        .create<String, MessageEnvelope>(
            subscriptionOf("LoanErrors")
                .subscription(listOf(loanErrorTopic))
        )
        .receive()
        .replay(0)

    private val infoFlux: ConnectableFlux<ReceiverRecord<String, MessageEnvelope>> = KafkaReceiver
        .create<String, MessageEnvelope>(
            subscriptionOf("LoanInfo")
                .subscription(listOf(loanTopic))
        )
        .receive()
        .replay(0)

    override fun ofInfo(sourceId: String, sourceVersion: Long) = infoFlux
        .autoConnect()
        .filter{it.value().isTypeOf(Info::class.java)}
        .map{ it.value()}
        .filter{ it.isVersionOf(sourceId, sourceVersion) }
        .map{ it.unpack(Info::class.java) }
        .map{ it.message }!!

    override fun ofError(sourceId: String, sourceVersion: Long) = errorFlux
        .autoConnect()
        .filter{ it.value().isTypeOf(String::class.java) }
        .map{ it.value() }
        .filter{ it.isVersionOf(sourceId, sourceVersion) }
        .map{ it.unpack(String::class.java) }!!

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