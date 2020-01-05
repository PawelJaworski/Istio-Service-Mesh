package pl.javorex.poc.istio.util.messaging

@FunctionalInterface
interface AsyncMessageCallback<M> {
    fun onComplete(sourceId: String, sourceVersion: Long, currentMessages: CurrentMessages<M>, messageBus: MessageBus<M>)

    fun onFailure(key: String, transactionId: Long, errorCode: String, messageBus: MessageBus<M>)

    fun onTimeout(sourceId: String, sourceVersion: Long, currentMessages: CurrentMessages<M>, messageBus: MessageBus<M>)
}
