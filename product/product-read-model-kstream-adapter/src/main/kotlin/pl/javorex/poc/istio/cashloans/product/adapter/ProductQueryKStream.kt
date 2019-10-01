package pl.javorex.poc.istio.cashloans.product.adapter

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import pl.javorex.poc.istio.cashloans.product.application.query.GetLoanCost
import pl.javorex.poc.istio.common.kafka.streams.message.MessageEnvelopeSerde
import pl.javorex.poc.istio.common.kafka.streams.processor.messageProcessor
import pl.javorex.poc.istio.common.message.listener.MessageListener
import java.util.*

class ProductQueryKStream(
    bootstrapServers: String,
    private val productTopic: String,
    private val productErrorTopic: String,
    private val loanTopic: String,
    private val loanCostFetched: MessageListener<GetLoanCost>
) {
    private val props = Properties()

    init {
        props[StreamsConfig.APPLICATION_ID_CONFIG] = "product-query-handler"
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
        .addSource(sourceFrom(loanTopic), loanTopic)
        .addProcessor(
            GET_LOAN_COST_PROCESSOR,
            messageProcessor<GetLoanCost> {
                messageType(GetLoanCost::class.java)
                sink(sinkFrom(productTopic))
                errorSink(sinkFrom(productErrorTopic))
                callback(loanCostFetched)
            },
            sourceFrom(loanTopic)
        )
        .addSink(sinkFrom(productTopic), productTopic,
            GET_LOAN_COST_PROCESSOR
        )
        .addSink(sinkFrom(productErrorTopic), productErrorTopic,
            GET_LOAN_COST_PROCESSOR
        )

    private fun sourceFrom(topic: String) = "$topic-source"
    private fun sinkFrom(topic: String) = "$topic-sink"
}

private const val GET_LOAN_COST_PROCESSOR = "Get-Loan-Cost-Processor"