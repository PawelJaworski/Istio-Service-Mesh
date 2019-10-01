package pl.javorex.poc.istio.cashloans.loan.adapter.event

import pl.javorex.poc.istio.cashloans.loan.application.event.LoanMessageBus
import pl.javorex.poc.istio.common.message.MessageBus

class LoanMessageBusAdapter(private val messageBus: MessageBus) : LoanMessageBus {
    override fun emitError(sourceId: String, sourceVersion: Long, event: Any) {
        messageBus.emitError(sourceId, sourceVersion, event)
    }

    override fun emit(sourceId: String, sourceVersion: Long, event: Any) {
        messageBus.emit(sourceId, sourceVersion, event)
    }
}