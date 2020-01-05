package pl.javorex.poc.istio.util.messaging

interface MessageBus<M> {
    fun emit(messageKey: String, message: M)
    fun emitError(messageKey: String, message: M)
}
