package pl.javorex.poc.istio.util.kafka.streams.processor

import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.processor.To
import pl.javorex.poc.istio.common.message.MessageBus
import pl.javorex.poc.istio.common.message.envelope.MessageEnvelope
import pl.javorex.poc.istio.common.message.envelope.pack
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.Exception

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
