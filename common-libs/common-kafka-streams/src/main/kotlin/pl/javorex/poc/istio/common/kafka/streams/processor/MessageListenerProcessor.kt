package pl.javorex.poc.istio.common.kafka.streams.processor

import org.apache.kafka.streams.processor.Processor
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.processor.ProcessorSupplier
import pl.javorex.poc.istio.common.message.envelope.MessageEnvelope
import pl.javorex.poc.istio.common.message.listener.MessageListener
import java.lang.Exception

fun <T>messageProcessor(build: MessageProcessorBuilder<T>.() -> Unit): ProcessorSupplier<String, MessageEnvelope> {
    val builder = MessageProcessorBuilder<T>()
    builder.build()

    return builder.supply()
}

class MessageProcessorBuilder<T> {
    private lateinit var messageType: Class<T>
    private lateinit var sink: String
    private lateinit var errorSink: String
    private lateinit var callback: MessageListener<T>

    fun messageType(messageType: Class<T>): MessageProcessorBuilder<T> {
        this.messageType = messageType
        return this
    }

    fun sink(sink: String): MessageProcessorBuilder<T> {
        this.sink = sink
        return this
    }

    fun errorSink(errorSink: String): MessageProcessorBuilder<T> {
        this.errorSink = errorSink
        return this
    }

    fun callback(callback: MessageListener<T>): MessageProcessorBuilder<T> {
        this.callback = callback
        return this
    }

    fun supply() =
            ProcessorSupplier {
                MessageListenerProcessor(
                    messageType,
                    sink,
                    errorSink,
                    callback
                )
            }
}

class MessageListenerProcessor<T>(
    private val messageType: Class<T>,
    private val sinkType: String,
    private val errorSinkType: String,
    private val messageListener: MessageListener<T>
) : Processor<String, MessageEnvelope> {
    private lateinit var messageBus: ProcessorMessageBus

    override fun init(context: ProcessorContext) {
        messageBus =
            ProcessorMessageBus(context!!, sinkType, errorSinkType)
    }

    override fun process(sourceId: String, message: MessageEnvelope?) {
        if (message == null || !message.isTypeOf(messageType)) {
            return
        }

        try {
            tryProcess(message)
        } catch(ex: Exception) {
            System.err.println("[MessageListenerProcessor] error: $ex")
            messageBus.emitProcessFailure(message, ex)
        }
    }

    override fun close() {}

    private fun tryProcess(message: MessageEnvelope) {
        messageListener.onMessage(
            message.sourceId,
            message.sourceVersion,
            message.unpack(messageType),
            messageBus
        )
    }
}