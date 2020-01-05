package pl.javorex.poc.istio.util.messaging

data class ErrorEnvelope(val aggregateId: String, val transactionId: Long, val errorCode: String)
