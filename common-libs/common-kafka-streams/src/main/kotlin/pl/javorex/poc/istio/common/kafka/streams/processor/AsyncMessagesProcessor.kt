package pl.javorex.poc.istio.common.kafka.streams.processor

import org.apache.kafka.streams.processor.*
import org.apache.kafka.streams.state.KeyValueStore
import pl.javorex.poc.istio.common.message.async.AsyncMessagesTemplate
import pl.javorex.poc.istio.common.message.async.CurrentMessages
import pl.javorex.poc.istio.common.message.envelope.MessageEnvelope
import pl.javorex.poc.istio.common.message.listener.AsyncMessageCallback
import java.lang.Exception
import java.time.Duration

class AsyncMessagesProcessor(
    private val templateSupplier: () -> AsyncMessagesTemplate,
    private val heartBeatInterval: HeartBeatInterval,
    private val storeName: String,
    private val messageCallback: AsyncMessageCallback,
    private val sinkType: String,
    private val errorSinkType: String,
    private val errorTopic: String
) : Processor<String, MessageEnvelope> {
    private lateinit var store: KeyValueStore<String, CurrentMessages>
    private lateinit var messageBus: ProcessorMessageBus
    private lateinit var context: ProcessorContext
    override fun init(context: ProcessorContext) {
        this.context = context
        store = context
                .getStateStore(storeName) as KeyValueStore<String, CurrentMessages>
        messageBus =
            ProcessorMessageBus(context!!, sinkType, errorSinkType)
        context
                .schedule(heartBeatInterval.duration, PunctuationType.WALL_CLOCK_TIME, this::doHeartBeat)
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

    private fun tryProcess(message: MessageEnvelope) {
        val sourceId = message.sourceId
        val messages = store.get(sourceId) ?: templateSupplier().messages()
        val messaging = templateSupplier().updateMessages(messages)

        if (messaging.isStarted() && context.topic() == errorTopic) {
            messageCallback.onFailure(message.sourceId, message.sourceVersion, "messaging.failure.runtimeError", messageBus)
            deleteFromStore(sourceId)
            return
        }

        if (!messaging.expects(message.messageType)) {
            return
        }

        messaging.mergeMessage(message)
        store.put(sourceId, messages)

        when {
            messaging.isComplete() -> {
                messageCallback.onComplete(sourceId, messaging.messagesVersion(), messaging.messages(), messageBus)
                deleteFromStore(sourceId)
            }
            messaging.hasErrors() -> {
                messaging.takeErrors().forEach{
                    messageCallback.onFailure(it.aggregateId, it.transactionId, it.errorCode, messageBus)
                }
                deleteFromStore(sourceId)
            }
        }
    }

    private fun doHeartBeat(timestamp: Long) {
        val toRemove = arrayListOf<String>()
        store.all().forEachRemaining{
            val sourceId = it.key
            val messages = it.value

            val saga = templateSupplier().updateMessages(messages)
            if (saga.isExpired(timestamp)) {
                toRemove += sourceId
            }

            val timeoutOccurred = saga.isTimeoutOccurred(timestamp)
            if (timeoutOccurred && !saga.isComplete()) {
                fireTimeoutEvent(sourceId, saga)
            }
            if (timeoutOccurred) {
                toRemove += sourceId
            }

        }
        toRemove.forEach {
            deleteFromStore(it)
        }
    }

    private fun fireTimeoutEvent(sourceId: String, saga: AsyncMessagesTemplate) {
        val sagaVersion = saga.messagesVersion()
        messageCallback.onTimeout(sourceId, sagaVersion, saga.messages(), messageBus)
    }

    override fun close() {}

    private fun deleteFromStore(sourceId: String) {
        println("[${store.name()}] deleting $sourceId")
        store.delete(sourceId)
    }
}

class HeartBeatInterval(val duration: Duration) {
    companion object {
        @JvmStatic
        fun ofSeconds(sec: Long) : HeartBeatInterval {
            val duration = Duration.ofSeconds(sec)

            return HeartBeatInterval(duration)
        }
    }
}
