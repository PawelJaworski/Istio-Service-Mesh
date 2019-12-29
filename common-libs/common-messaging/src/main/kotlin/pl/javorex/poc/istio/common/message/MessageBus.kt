package pl.javorex.poc.istio.common.message

interface MessageBus {
    fun emit(sourceId: String, sourceVersion: Long, message: Any)
    fun emitError(sourceId: String, sourceVersion: Long, message: Any)
}
