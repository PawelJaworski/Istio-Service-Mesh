package pl.javorex.poc.istio.cashloans.loan.adapter

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import pl.javorex.poc.istio.cashloans.product.application.query.LoanCostFetched
import pl.javorex.poc.istio.cashloans.instalment.domain.event.InstalmentCalculated
import pl.javorex.poc.istio.cashloans.instalment.domain.event.InstalmentCalculationFailed
import pl.javorex.poc.istio.cashloans.loan.application.command.CreateLoan
import pl.javorex.poc.istio.common.kafka.streams.message.MessageEnvelopeSerde
import pl.javorex.poc.istio.common.kafka.streams.processor.AsyncMessagesSaga
import pl.javorex.poc.istio.common.kafka.streams.processor.HeartBeatInterval
import pl.javorex.poc.istio.common.message.listener.AsyncMessageCallback
import java.time.Duration
import java.util.*

class CreateLoanKStream(
    bootstrapServers: String,
    private val loanTopic: String,
    private val loanErrorTopic: String,
    private val instalmentTopic: String,
    private val instalmentErrorTopic: String,
    private val productTopic: String,
    private val productErrorTopic: String,
    private val calculateInstalment: AsyncMessageCallback,
    private val saveLoan: AsyncMessageCallback
) {
    private val props= Properties()

    init {
        props[StreamsConfig.APPLICATION_ID_CONFIG] = "Create-Loan-Stream"
        props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.StringSerde::class.java
        props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = MessageEnvelopeSerde::class.java
        props[StreamsConfig.RETRIES_CONFIG] = Integer.MAX_VALUE
        props[StreamsConfig.RETRY_BACKOFF_MS_CONFIG] = 5000

        start()
    }

    private fun start() {
        val topology = createTopology()

        val saga = AsyncMessagesSaga()
        saga.timeout(Duration.ofSeconds(5))
        saga.topic(loanTopic, loanErrorTopic)
        saga.startsWith(CreateLoan::class.java)

        saga.step()
             .named("fetch-loan-cost")
                .heartBeat(HeartBeatInterval.ofSeconds(2))
                .sources(sourceFrom(loanTopic), sourceFrom(productTopic), sourceFrom(productErrorTopic))
                .requires(LoanCostFetched::class.java)
                .onComplete(calculateInstalment)

        saga.step()
            .named("calculate-instalment")
            .heartBeat(HeartBeatInterval.ofSeconds(2))
            .sources(sourceFrom(loanTopic), sourceFrom(instalmentTopic), sourceFrom(instalmentErrorTopic))
            .requires(InstalmentCalculated::class.java)
            .expectsError(InstalmentCalculationFailed::class.java)
            .onComplete(saveLoan)

        saga.joinInto(topology)

        val streams = KafkaStreams(topology, props)
        try {
            streams.cleanUp()
        } catch(e: Exception){}
        streams.start()

        Runtime.getRuntime()
            .addShutdownHook(
                Thread(Runnable { streams.close() })
            )
    }

    private fun createTopology() = StreamsBuilder().build()
        .addSource(sourceFrom(loanTopic), loanTopic)
        .addSource(sourceFrom(instalmentTopic), instalmentTopic)
        .addSource(sourceFrom(instalmentErrorTopic), instalmentErrorTopic)
        .addSource(sourceFrom(productTopic), productTopic)
        .addSource(sourceFrom(productErrorTopic), productErrorTopic)

    private fun sourceFrom(topic: String) = "$topic-source"
}