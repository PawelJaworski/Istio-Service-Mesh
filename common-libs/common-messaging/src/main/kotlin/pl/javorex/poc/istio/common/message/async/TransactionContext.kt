package pl.javorex.poc.istio.common.message.async

data class TransactionContext(val aggregateId: String, val transactionId: Long)
