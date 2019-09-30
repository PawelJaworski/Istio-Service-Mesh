package pl.javorex.poc.istio.common.kafka.streams.processor

import pl.javorex.poc.istio.common.message.envelope.MessageEnvelope

data class ProcessorFailure(
    val processed: MessageEnvelope? = null,
    val reason: String = ""
)