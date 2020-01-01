package pl.javorex.poc.istio.common.message.listener

import pl.javorex.poc.istio.common.message.MessageBus
import pl.javorex.poc.istio.common.message.envelope.MessageEnvelope
import pl.javorex.poc.istio.common.message.async.CurrentMessages

@FunctionalInterface
interface AsyncMessageCallback {
    fun onComplete(sourceId: String, sourceVersion: Long, currentMessages: CurrentMessages, messageBus: MessageBus)

    @JvmDefault
    fun onFailure(aggregateId: String, transactionId: Long, errorCode: String, messageBus: MessageBus) {

    }

    @JvmDefault
    fun onTimeout(sourceId: String, sourceVersion: Long, currentMessages: CurrentMessages, messageBus: MessageBus) {
        val missingMessages = currentMessages.missing().joinToString(",")
        val errorMsg = "Request Timeout. Missing $missingMessages"

        messageBus.emitError(sourceId, sourceVersion, errorMsg)
    }
}
