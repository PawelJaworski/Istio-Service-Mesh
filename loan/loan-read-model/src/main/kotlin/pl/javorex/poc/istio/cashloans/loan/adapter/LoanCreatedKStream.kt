package pl.javorex.poc.istio.cashloans.loan.adapter

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.processor.ProcessorSupplier
import pl.javorex.poc.istio.cashloans.loan.application.LoanCreatedListener
import pl.javorex.poc.istio.cashloans.loan.domain.event.LoanCreated
import pl.javorex.poc.istio.cashloans.loan.domain.projection.BorrowerLoansRepository
import pl.javorex.poc.istio.common.kafka.streams.message.MessageEnvelopeSerde
import pl.javorex.poc.istio.common.kafka.streams.processor.MessageListenerProcessor
import java.util.*

private const val LOAN_CREATED_PROCESSOR = "Loan-Created-Processor"

class LoanCreatedKStream(
    bootstrapServers: String,
    private val loanTopic: String,
    private val loanErrorTopic: String,
    borrowerLoansRepository: BorrowerLoansRepository
) {
    private val props = Properties()
    private lateinit var streams: KafkaStreams

    private val onLoanCreated = LoanCreatedListener(borrowerLoansRepository)

    init {
        props[StreamsConfig.APPLICATION_ID_CONFIG] = "Loan-Created-Stream"
        props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.StringSerde::class.java
        props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = MessageEnvelopeSerde::class.java
        props[StreamsConfig.RETRIES_CONFIG] = Integer.MAX_VALUE
        props[StreamsConfig.RETRY_BACKOFF_MS_CONFIG] = 5000

        start()
    }

    private fun start() {
        val topology = createTopology()
        streams = KafkaStreams(topology, props)
        streams.start()

        Runtime.getRuntime()
            .addShutdownHook(
                Thread(Runnable { streams.close() })
            )
    }

    private fun createTopology() = StreamsBuilder().build()
        .addSource(sourceFrom(loanTopic), loanTopic)
        .addProcessor(
            LOAN_CREATED_PROCESSOR,
            ProcessorSupplier(this::loanCreatedProcessor),
            sourceFrom(loanTopic)
        )
        .addSink(sinkFrom(loanTopic), loanTopic, LOAN_CREATED_PROCESSOR)
        .addSink(sinkFrom(loanErrorTopic), loanErrorTopic, LOAN_CREATED_PROCESSOR)


    private fun loanCreatedProcessor() = MessageListenerProcessor(
        LoanCreated::class.java,
        sinkFrom(loanTopic),
        sinkFrom(loanErrorTopic),
        onLoanCreated
    )

    private fun sourceFrom(topic: String) = "$topic-source"
    private fun sinkFrom(topic: String) = "$topic-sink"
}