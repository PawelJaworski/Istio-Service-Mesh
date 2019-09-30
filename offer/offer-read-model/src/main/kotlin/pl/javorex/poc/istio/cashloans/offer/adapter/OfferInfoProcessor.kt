package pl.javorex.poc.istio.cashloans.offer.adapter

import org.apache.kafka.streams.processor.Processor
import org.apache.kafka.streams.processor.ProcessorContext
import pl.javorex.poc.istio.cashloans.offer.application.OfferInfoMessenger
import pl.javorex.poc.istio.cashloans.offer.domain.event.OfferAccepted
import pl.javorex.poc.istio.common.kafka.streams.processor.ProcessorMessageBus
import pl.javorex.poc.istio.common.message.envelope.MessageEnvelope
import java.lang.Exception

class OfferInfoProcessor(
    private val sinkType: String,
    private val errorSinkType: String
) : Processor<String, MessageEnvelope> {
    private lateinit var messageBus: ProcessorMessageBus

    override fun init(context: ProcessorContext) {
        messageBus =
            ProcessorMessageBus(context!!, sinkType, errorSinkType)
    }

    override fun process(sourceId: String, message: MessageEnvelope?) {
        if (message == null) {
            return
        }

        try {
            tryProcess(message)
        } catch(ex: Exception) {
            messageBus.emitProcessFailure(message, ex)
        }
    }

    override fun close() {}

    private fun tryProcess(message: MessageEnvelope) {
        val sourceId = message.sourceId
        val sourceVersion = message.sourceVersion
        when {
            message.isTypeOf(OfferAccepted::class.java) -> {
                val offerAccepted = message.unpack(OfferAccepted::class.java)

                OfferInfoMessenger.publish(sourceId, sourceVersion, offerAccepted, messageBus)
            }
        }

    }
}