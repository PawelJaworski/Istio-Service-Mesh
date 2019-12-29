package pl.javorex.poc.istio.common.message.listener

import pl.javorex.poc.istio.common.message.MessageBus
import pl.javorex.poc.istio.common.message.common.Rollback
import pl.javorex.poc.istio.common.message.envelope.MessageEnvelope
import pl.javorex.poc.istio.common.message.async.CurrentMessages

@FunctionalInterface
interface AsyncMessageCallback {
    fun onComplete(sourceId: String, sourceVersion: Long, currentMessages: CurrentMessages, messageBus: MessageBus)

    @JvmDefault
    fun onError(error: MessageEnvelope, messageBus: MessageBus) {
        val errorMsg = error.payload.toString()

        messageBus.emitError(error.sourceId, error.sourceVersion, Rollback())
        messageBus.emitError(error.sourceId, error.sourceVersion, errorMsg)
    }

    @JvmDefault
    fun onTimeout(sourceId: String, sourceVersion: Long, currentMessages: CurrentMessages, messageBus: MessageBus) {
        val missingMessages = currentMessages.missing().joinToString(",")
        val errorMsg = "Request Timeout. Missing $missingMessages"
        val transactionIdAsString = sourceVersion
                .toString()

        messageBus.emitError(sourceId, sourceVersion, Rollback())

        messageBus.emitError(sourceId, sourceVersion, errorMsg)
    }
}
