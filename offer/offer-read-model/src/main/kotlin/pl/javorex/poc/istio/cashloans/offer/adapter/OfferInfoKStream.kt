package pl.javorex.poc.istio.cashloans.offer.adapter

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.processor.ProcessorSupplier
import pl.javorex.poc.istio.common.kafka.streams.message.MessageEnvelopeSerde
import java.util.*

class OfferInfoKStream(
    bootstrapServers: String,
    private val offerTopic: String
) {
    private val props= Properties()

    init {
        props[StreamsConfig.APPLICATION_ID_CONFIG] = "offer-info-kstream"
        props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.StringSerde::class.java
        props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = MessageEnvelopeSerde::class.java
        props[StreamsConfig.RETRIES_CONFIG] = Integer.MAX_VALUE
        props[StreamsConfig.RETRY_BACKOFF_MS_CONFIG] = 5000

        start()
    }

    private fun start() {
        val topology = createTopology()
        val streams = KafkaStreams(topology, props)

        streams.start()

        Runtime.getRuntime()
            .addShutdownHook(
                Thread(Runnable { streams.close() })
            )
    }

    private fun createTopology() = StreamsBuilder().build()
        .addSource(sourceFrom(offerTopic), offerTopic)
        .addProcessor(
            OFFER_INFO_PROCESSOR,
            ProcessorSupplier(this::offerInfoProcessor),
            sourceFrom(offerTopic)
        )
        .addSink(sinkFrom(offerTopic), offerTopic, OFFER_INFO_PROCESSOR)

    private fun offerInfoProcessor() = OfferInfoProcessor(
        sinkFrom(offerTopic),
        sinkFrom(offerTopic)
    )

    private fun sourceFrom(topic: String) = "$topic-source"
    private fun sinkFrom(topic: String) = "$topic-sink"
}

private const val OFFER_INFO_PROCESSOR = "Offer-Info-Processor"