package pl.javorex.poc.istio.common.kafka.streams.processor

import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.processor.To
import pl.javorex.poc.istio.common.message.MessageBus
import pl.javorex.poc.istio.common.message.envelope.MessageEnvelope
import pl.javorex.poc.istio.common.message.envelope.pack
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.Exception

class ProcessorMessageBus(
        private val context: ProcessorContext,
        private val sinkName: String,
        private val errorSinkName: String

) : MessageBus {
    fun emitProcessFailure(message: MessageEnvelope, ex: Exception) {
        val processFailure =
            ProcessorFailure(message, ex.asStackTraceString())

        emitError(message.sourceId, message.sourceVersion, processFailure)
    }

    override fun emitError(sourceId: String, sourceVersion: Long, message: Any) {
        val messageEnvelope = pack(sourceId, sourceVersion, message)
        context.forward(sourceId, messageEnvelope, To.child(errorSinkName))
    }

    override fun emit(sourceId: String, sourceVersion: Long, message: Any) {
        val messageEnvelope = pack(sourceId, sourceVersion, message)
        context.forward(sourceId, messageEnvelope, To.child(sinkName))}

}

private fun Exception.asStackTraceString() : String {
    val sw = StringWriter()
    this.printStackTrace(PrintWriter(sw))

    return sw.toString()
}