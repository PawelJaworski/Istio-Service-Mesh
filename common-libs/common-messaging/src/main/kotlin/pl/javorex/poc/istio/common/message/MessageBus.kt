package pl.javorex.poc.istio.common.message

interface MessageBus<M> {
    fun emit(messageKey: String, message: M)
    fun emitError(messageKey: String, message: M)
}
