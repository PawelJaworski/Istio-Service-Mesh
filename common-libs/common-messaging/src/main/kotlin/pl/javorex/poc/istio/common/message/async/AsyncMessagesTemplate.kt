package pl.javorex.poc.istio.common.message.async

import pl.javorex.poc.istio.common.message.envelope.MessageEnvelope
import pl.javorex.poc.istio.common.message.envelope.pack

val LACK_OF_MESSAGE = null

data class AsyncMessagesTemplate(
    internal val timeout: Long = 0
) {
    private var messages: CurrentMessages =
        CurrentMessages()
    private val errors: ArrayList<ErrorEnvelope> = arrayListOf()

    fun updateMessages(messages: CurrentMessages) : AsyncMessagesTemplate {
        this.messages = messages

        return this
    }

    fun mergeMessage(message: MessageEnvelope) {
        val messageType = message.messageType
        val sourceId = message.sourceId
        val sourceVersion = message.sourceVersion

        when {
            !messages.expects(messageType) ->
                return
            messages.isVersionDiffers(sourceVersion) -> {
                putError(sourceId, sourceVersion, "messaging.failure.concurrentModification")
                putError(sourceId, messages.version,"messaging.failure.concurrentModification")
            }
            messages.alreadyContains(messageType) ->
                putError(sourceId, sourceVersion, "messaging.failure.doubleMessage")
            messages.expects(messageType) ->
                messages.collect(message)
        }
    }

    fun expects(messageType: String) =
        messages.starting.containsKey(messageType)
                || messages.required.containsKey(messageType)

    fun messages() = messages

    fun messagesVersion() = messages.version

    fun isTimeoutOccurred(timestamp: Long) =
            messages.isStarted() && messages.startedTimestamp + timeout < timestamp

    fun isExpired(timestamp: Long) = messages.creationTimestamp + 2 * timeout < timestamp

    fun isStarted() = messages.isStarted()

    fun isComplete() = messages.containsAllRequired()

    fun hasErrors() = errors.isNotEmpty()

    fun takeErrors(): List<ErrorEnvelope> {
        val takenErrors = errors.toMutableList()

        errors.clear()
        return takenErrors
    }

    private fun putError(sourceId: String, sourceVersion: Long, errorCode: String) {
        errors += ErrorEnvelope(sourceId, sourceVersion, errorCode)
    }
}

private const val NO_VERSION = Long.MIN_VALUE

private const val MESSAGE_HAVENT_ARRIVED_YET = Long.MAX_VALUE

fun messagesOf() = CurrentMessages()
fun messagesOf(otherMessages: CurrentMessages) : CurrentMessages {
    val messages = CurrentMessages()
    messages.starting.putAll(otherMessages.starting)
    messages.required.putAll(otherMessages.required)

    return messages
}

data class CurrentMessages(
    @PublishedApi
    internal val starting: HashMap<String, MessageEnvelope?> = hashMapOf(),
    @PublishedApi
    internal val required: HashMap<String, MessageEnvelope?> = hashMapOf(),
    @PublishedApi
    internal val creationTimestamp: Long = System.currentTimeMillis(),
    @PublishedApi
    internal var startedTimestamp: Long = MESSAGE_HAVENT_ARRIVED_YET,
    @PublishedApi
    internal var version: Long = NO_VERSION
) {
    fun collect(message: MessageEnvelope) {
        val messageType = message.messageType
        when {
            starting.contains(messageType) -> {
                starting[messageType] = message
                startedTimestamp = message.timestamp
                version = message.sourceVersion
            }
            required.contains(messageType) -> {
                required[messageType] = message
            }
        }
    }

    inline operator fun <reified T>get(message: Class<T>): T {
            val messageType = message.simpleName
            return when {
                starting.contains(messageType) ->
                    starting[messageType]!!.unpack(T::class.java)
                required.contains(messageType) ->
                    required[messageType]!!.unpack(T::class.java)
                else -> throw IllegalStateException("Cannot get message of type $messageType")
            }
    }

    fun missing() =
            required.filter { e -> e.value == LACK_OF_MESSAGE }
                    .map { it.key }

    internal fun isStarted() = startedTimestamp != MESSAGE_HAVENT_ARRIVED_YET

    internal fun isVersionDiffers(otherVersion: Long) = version != NO_VERSION && version != otherVersion

    internal fun expects(messageType: String) = starting.contains(messageType)
            || required.contains(messageType)

    internal fun alreadyContains(messageType: String) =
            starting[messageType] != LACK_OF_MESSAGE || required[messageType] != LACK_OF_MESSAGE

    internal fun containsAllRequired() =
        starting.none { it.value == LACK_OF_MESSAGE } && required.none { it.value == LACK_OF_MESSAGE }
}

data class ConcurrentModification(val error: String)
data class DoubleMessage(val messageType: String)
