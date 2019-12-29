package pl.javorex.poc.istio.common.message

interface MessageBus {
    fun emit(messageKey: String, transactionId: Long, message: Any)
    fun emitError(messageKey: String, transactionId: Long, message: Any)
}
