package pl.javorex.poc.istio.common.message.listener

import pl.javorex.poc.istio.common.message.MessageBus
import pl.javorex.poc.istio.common.message.envelope.MessageEnvelope
import pl.javorex.poc.istio.common.message.async.CurrentMessages
import pl.javorex.poc.istio.common.message.async.TransactionContext

@FunctionalInterface
interface AsyncMessageCallback<M> {
    fun onComplete(sourceId: String, sourceVersion: Long, currentMessages: CurrentMessages<M>, messageBus: MessageBus<M>)

    fun onFailure(key: String, transactionId: Long, errorCode: String, messageBus: MessageBus<M>)

    fun onTimeout(sourceId: String, sourceVersion: Long, currentMessages: CurrentMessages<M>, messageBus: MessageBus<M>)
}
