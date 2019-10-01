package pl.javorex.poc.istio.cashloans.loan.adapter

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier
import org.apache.kafka.streams.state.Stores
import pl.javorex.poc.istio.cashloans.offer.domain.event.OfferAccepted
import pl.javorex.poc.istio.common.kafka.streams.message.MessageEnvelopeSerde
import pl.javorex.poc.istio.common.kafka.streams.processor.uniqueMessageVersionProcessor
import pl.javorex.poc.istio.common.message.listener.MessageUniquenessCallback
import java.util.*

class OfferAcceptedKStream(
    bootstrapServers: String,
    private val offerTopic: String,
    private val loanTopic: String,
    private val loanErrorTopic: String,
    private val createLoan: MessageUniquenessCallback
) {
    private val props = Properties()

    init {
        props[StreamsConfig.APPLICATION_ID_CONFIG] = "Offer-Accepted-Stream"
        props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.StringSerde::class.java
        props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = MessageEnvelopeSerde::class.java
        props[StreamsConfig.RETRIES_CONFIG] = Integer.MAX_VALUE
        props[StreamsConfig.RETRY_BACKOFF_MS_CONFIG] = 5000

        init()
    }

    private fun init() {
        val topology = createTopology()
        val streams = KafkaStreams(topology, props)
        try {
            streams.cleanUp()
        } catch (e: Exception) {
        }
        streams.start()

        Runtime.getRuntime()
            .addShutdownHook(
                Thread(Runnable { streams.close() })
            )
    }

    private fun createTopology() = StreamsBuilder().build()
        .addSource(sourceFrom(offerTopic), offerTopic)
        .addProcessor(
            OFFER_ACCEPTED_UNIQUE_EVENT_PROCESSOR,
            uniqueMessageVersionProcessor{
                storeName(UNIQUE_OFFER_ACCEPTED_STORE)
                onComplete(createLoan)
                sink(sinkFrom(loanTopic))
                errorSink(sinkFrom(loanErrorTopic))
                expectsUniquenessOf(OfferAccepted::class.java)
            },
            sourceFrom(offerTopic)
        )
        .addStateStore(
            offerAcceptedUniqueEventStoreBuilder,
            OFFER_ACCEPTED_UNIQUE_EVENT_PROCESSOR
        )
        .addSink(sinkFrom(loanTopic), loanTopic, OFFER_ACCEPTED_UNIQUE_EVENT_PROCESSOR)
        .addSink(sinkFrom(loanErrorTopic), loanErrorTopic, OFFER_ACCEPTED_UNIQUE_EVENT_PROCESSOR)

    private fun sourceFrom(topic: String) = "$topic-source"
    private fun sinkFrom(topic: String) = "$topic-sink"
}

private const val OFFER_ACCEPTED_UNIQUE_EVENT_PROCESSOR = "Offer-Accepted-Unique-Event-Processor"
private const val UNIQUE_OFFER_ACCEPTED_STORE = "Unique-Offer-Accepted-Store"

private val offerAcceptedUniqueEventStoreSupplier: KeyValueBytesStoreSupplier =
    Stores.persistentKeyValueStore(UNIQUE_OFFER_ACCEPTED_STORE)
private val offerAcceptedUniqueEventStoreBuilder =
    Stores.keyValueStoreBuilder(
        offerAcceptedUniqueEventStoreSupplier, Serdes.String(),
        MessageEnvelopeSerde()
    )
