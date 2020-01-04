package pl.javorex.poc.istio.common.kafka.streams.processor

import org.apache.kafka.streams.processor.*
import org.apache.kafka.streams.state.KeyValueStore
import pl.javorex.poc.istio.common.kafka.streams.message.getLong
import pl.javorex.poc.istio.common.kafka.streams.message.getString
import pl.javorex.poc.istio.common.message.async.AsyncMessagesTemplate
import pl.javorex.poc.istio.common.message.async.CurrentMessages
import pl.javorex.poc.istio.common.message.envelope.MessageEnvelope
import pl.javorex.poc.istio.common.message.envelope.pack
import pl.javorex.poc.istio.common.message.listener.AsyncMessageCallback
import java.lang.Exception
import java.time.Duration

const val HEADER_MESSAGE_TYPE = "messageType"
const val HEADER_TRANSACTION_ID = "transactionId"

class AsyncMessagesProcessor<M>(
    private val templateSupplier: () -> AsyncMessagesTemplate<M>,
    private val heartBeatInterval: HeartBeatInterval,
    private val storeName: String,
    private val messageCallback: AsyncMessageCallback<M>,
    private val sinkType: String,
    private val errorSinkType: String,
    private val errorTopic: String
) : Processor<String, M> {
    private lateinit var store: KeyValueStore<String, CurrentMessages<M>>
    private lateinit var messageBus: ProcessorMessageBus<M>
    private lateinit var context: ProcessorContext
    override fun init(context: ProcessorContext) {
        this.context = context
        store = context
                .getStateStore(storeName) as KeyValueStore<String, CurrentMessages<M>>
        messageBus =
            ProcessorMessageBus(context!!, sinkType, errorSinkType)
        context
                .schedule(heartBeatInterval.duration, PunctuationType.WALL_CLOCK_TIME, this::doHeartBeat)
    }

    override fun process(key: String, message: M?) {
        if (message == null) {
            return
        }

        try {
            tryProcess(key, message)
        } catch(ex: Exception) {
            ex.printStackTrace()
            val transactionId = context.headers().getLong(HEADER_TRANSACTION_ID)
            messageCallback.onFailure(key, transactionId, "messaging.failure.processorError", messageBus)
        }
    }

    private fun tryProcess(key: String, message: M) {
        val messageType = context.headers()
                .getString(HEADER_MESSAGE_TYPE)
        val transactionId = context.headers()
                .getLong(HEADER_TRANSACTION_ID)

        val messages = store.get(key) ?: templateSupplier().messages()
        val messaging = templateSupplier().updateMessages(messages)

        if(context.topic() == errorTopic) {
            if (messaging.messagesVersion() == transactionId) {
                messageCallback.onFailure(key, transactionId, "messaging.failure.runtimeError", messageBus)
                deleteFromStore(key)
            }
            return
        }


        if (!messaging.expects(messageType)) {
            return
        }

        val messageEnvelope: MessageEnvelope<M> = pack(key, transactionId, messageType, message)
        messaging.mergeMessage(messageEnvelope)
        store.put(key, messages)

        when {
            messaging.isComplete() -> {
                messageCallback.onComplete(key, messaging.messagesVersion(), messaging.messages(), messageBus)
                deleteFromStore(key)
            }
            messaging.hasErrors() -> {
                messaging.takeErrors().forEach{
                    val key = it.aggregateId
                    val transactionId = it.transactionId
                    messageCallback.onFailure(key, transactionId, it.errorCode, messageBus)
                }
                deleteFromStore(key)
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

    private fun fireTimeoutEvent(sourceId: String, saga: AsyncMessagesTemplate<M>) {
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
