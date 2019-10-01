package pl.javorex.poc.istio.cashloans.instalment.domain.vo

val ALLOWED_NUMBER_OF_INSTALMENT = setOf(2, 6, 12)
internal data class NumberOfInstalment(val value: Int) : ValueObject {
    init {
        if (!ALLOWED_NUMBER_OF_INSTALMENT.contains(value)){
            throw IllegalStateException("$value is not allowed as number of instalment")
        }
    }
}