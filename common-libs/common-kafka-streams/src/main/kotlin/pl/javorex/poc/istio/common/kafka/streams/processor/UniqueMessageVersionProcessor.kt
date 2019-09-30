package pl.javorex.poc.istio.common.kafka.streams.processor

import org.apache.kafka.streams.processor.Processor
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.processor.ProcessorSupplier
import org.apache.kafka.streams.state.KeyValueStore
import pl.javorex.poc.istio.common.message.envelope.MessageEnvelope
import pl.javorex.poc.istio.common.message.listener.MessageUniquenessCallback
import java.lang.Exception

fun uniqueMessageVersionProcessor(build: UniqueMessageVersionProcessorBuilder.() -> Unit)
        : ProcessorSupplier<String, MessageEnvelope> {
    val builder = UniqueMessageVersionProcessorBuilder()
    builder.build()

    return builder.supply()
}

class UniqueMessageVersionProcessorBuilder {
    private lateinit var storeName: String
    private lateinit var onComplete: MessageUniquenessCallback
    private lateinit var sink: String
    private lateinit var errorSink: String
    private lateinit var unique: Class<*>

    fun storeName(storeName: String): UniqueMessageVersionProcessorBuilder {
        this.storeName = storeName
        return this
    }

    fun onComplete(onComplete: MessageUniquenessCallback): UniqueMessageVersionProcessorBuilder {
        this.onComplete = onComplete
        return this
    }

    fun sink(sink: String): UniqueMessageVersionProcessorBuilder {
        this.sink = sink
        return this
    }

    fun errorSink(errorSink: String): UniqueMessageVersionProcessorBuilder {
        this.errorSink = errorSink
        return this
    }

    fun expectsUniquenessOf(unique: Class<*>): UniqueMessageVersionProcessorBuilder {
        this.unique = unique
        return this
    }

    fun supply() = ProcessorSupplier{
        UniqueMessageVersionProcessor(
            storeName,
            onComplete,
            sink,
            errorSink,
            unique
        )
    }

}
class UniqueMessageVersionProcessor(
        private val storeName: String,
        private val messageCallback: MessageUniquenessCallback,
        private val sinkName: String,
        private val errorSinkName: String,
        private val messageClazz: Class<*>
) : Processor<String, MessageEnvelope> {
    private lateinit var store: KeyValueStore<String, MessageEnvelope>
    private lateinit var messageBus: ProcessorMessageBus

    override fun init(context: ProcessorContext) {
        store = context
                .getStateStore(storeName) as KeyValueStore<String, MessageEnvelope>
        messageBus =
            ProcessorMessageBus(context!!, sinkName, errorSinkName)
    }

    override fun process(sourceId: String, message: MessageEnvelope?) {
        if (message == null || !message.isTypeOf(messageClazz)) {
            return
        }

        try {
            tryProcess(message)
        } catch(ex: Exception) {
            messageBus.emitProcessFailure(message, ex)
        }
    }

    override fun close(){}

    private fun tryProcess(message: MessageEnvelope) {
        val key = uniqueKeyOf(message)
        val previous = store.get(key)
        if (previous != null) {
            messageCallback.onUniqueViolated(message, messageBus)
        } else {
            store.put(key, message)
            messageCallback.onFirst(message, messageBus)
        }
    }

    private fun uniqueKeyOf(message: MessageEnvelope) = "${message.sourceId}-${message.sourceVersion}"


}