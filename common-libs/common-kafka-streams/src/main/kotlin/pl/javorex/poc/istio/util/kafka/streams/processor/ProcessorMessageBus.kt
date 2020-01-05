package pl.javorex.poc.istio.util.kafka.streams.processor

import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.processor.To
import pl.javorex.poc.istio.util.messaging.MessageBus

class ProcessorMessageBus<M>(
        private val context: ProcessorContext,
        private val sinkName: String,
        private val errorSinkName: String

) : MessageBus<M> {

    override fun emitError(messageKey: String, message: M) {
        context.forward(messageKey, message, To.child(errorSinkName))
    }

    override fun emit(messageKey: String, message: M) {
        context.forward(messageKey, message, To.child(sinkName))}

}
