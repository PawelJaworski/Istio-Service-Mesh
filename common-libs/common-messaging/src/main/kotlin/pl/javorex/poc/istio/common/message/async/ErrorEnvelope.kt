package pl.javorex.poc.istio.common.message.async

data class ErrorEnvelope(val aggregateId: String, val transactionId: Long, val errorCode: String)
