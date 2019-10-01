package pl.javorex.poc.istio.cashloans.loan.application.event

interface LoanMessageBus {
    fun emit(sourceId: String, sourceVersion: Long, event: Any)
    fun emitError(sourceId: String, sourceVersion: Long, event: Any)
}