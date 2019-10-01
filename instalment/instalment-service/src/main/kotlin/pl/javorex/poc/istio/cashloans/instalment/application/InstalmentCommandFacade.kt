package pl.javorex.poc.istio.cashloans.instalment.application

import pl.javorex.poc.istio.cashloans.instalment.domain.policy.InstalmentCalculationDefaultPolicy
import pl.javorex.poc.istio.cashloans.instalment.domain.vo.NumberOfInstalment
import pl.javorex.poc.istio.cashloans.instalment.application.command.CalculateInstalment
import pl.javorex.poc.istio.cashloans.instalment.domain.event.InstalmentCalculated
import pl.javorex.poc.istio.cashloans.instalment.domain.event.InstalmentCalculationFailed
import pl.javorex.poc.istio.cashloans.instalment.domain.vo.LoanCost
import pl.javorex.poc.istio.common.message.MessageBus
import pl.javorex.util.functional.Failure
import pl.javorex.util.functional.Success
import pl.javorex.util.functional.Try
import java.math.BigDecimal

class InstalmentCommandFacade {
    fun calculateInstalment(calculateInstalment: CalculateInstalment, messageBus: MessageBus) {
        val sourceId = calculateInstalment.sourceId
        val sourceVersion = calculateInstalment.sourceVersion

        when(val calculationTry = tryCalculateInstalment(calculateInstalment)) {
            is Success -> emitInstalmentCalculated(sourceId, sourceVersion, calculationTry.value, messageBus)
            is Failure -> emitInstalmentCalculationFailed(sourceId, sourceVersion, calculationTry.error, messageBus)
        }
    }

    private fun tryCalculateInstalment(calculateInstalment: CalculateInstalment) : Try<BigDecimal> = Try {
        val numberOfInstalment = NumberOfInstalment(
                calculateInstalment.numberOfInstalment
        )
        val loanCost = LoanCost(
            calculateInstalment.loanCost
        )

        InstalmentCalculationDefaultPolicy.applyTo(numberOfInstalment, loanCost)
    }

    private fun emitInstalmentCalculated(sourceId: String, sourceVersion: Long, calculation: BigDecimal,
                                         messageBus: MessageBus) {
        val instalmentCalculated = InstalmentCalculated(calculation)

        messageBus.emit(sourceId, sourceVersion, instalmentCalculated)
    }

    private fun emitInstalmentCalculationFailed(sourceId: String, sourceVersion: Long,
                                                calculationFailure: String, messageBus: MessageBus) {
        val instalmentCalculationFailed = InstalmentCalculationFailed(calculationFailure)

        messageBus.emitError(sourceId, sourceVersion, calculationFailure)
        messageBus.emitError(sourceId, sourceVersion, instalmentCalculationFailed)
    }
}