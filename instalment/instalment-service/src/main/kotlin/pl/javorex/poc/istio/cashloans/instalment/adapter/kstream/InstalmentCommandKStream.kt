package pl.javorex.poc.istio.cashloans.instalment.adapter.kstream

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.processor.ProcessorSupplier
import pl.javorex.poc.istio.cashloans.instalment.application.CalculateInstalmentCommandListener
import pl.javorex.poc.istio.cashloans.instalment.application.command.CalculateInstalment
import pl.javorex.poc.istio.common.kafka.streams.message.MessageEnvelopeSerde
import pl.javorex.poc.istio.common.kafka.streams.processor.MessageListenerProcessor
import java.util.*

class InstalmentCommandKStream(
    bootstrapServers: String,
    private val instalmentTopic: String,
    private val instalmentErrorTopic: String,
    private val loanTopic: String,
    private val calculateInstalmentCommandListener: CalculateInstalmentCommandListener
) {
    private val props = Properties()

    init {
        props[StreamsConfig.APPLICATION_ID_CONFIG] = "instalment-command-handler"
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
            CALCULATE_INSTALMENT_COMMAND_PROCESSOR,
            ProcessorSupplier(this::calculateInstalmentCommandProcessor),
            sourceFrom(loanTopic)
        )
        .addSink(sinkFrom(instalmentTopic), instalmentTopic,
            CALCULATE_INSTALMENT_COMMAND_PROCESSOR
        )
        .addSink(sinkFrom(instalmentErrorTopic), instalmentErrorTopic,
            CALCULATE_INSTALMENT_COMMAND_PROCESSOR
        )


    private fun calculateInstalmentCommandProcessor() =
        MessageListenerProcessor(
            CalculateInstalment::class.java,
            sinkFrom(instalmentTopic),
            sinkFrom(instalmentErrorTopic),
            calculateInstalmentCommandListener
        )

    private fun sourceFrom(topic: String) = "$topic-source"
    private fun sinkFrom(topic: String) = "$topic-sink"
}

private const val CALCULATE_INSTALMENT_COMMAND_PROCESSOR = "Calculate-Instalment-Command-Processor"