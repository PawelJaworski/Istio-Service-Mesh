package pl.javorex.util.functional

data class Success<T>(val value: T) : Try<T>() {
    override fun <U> map(mapper: (t: T) -> U): Try<U> = Success(mapper(value))
    override fun valueOnFailure(mapper: (String) -> T): T = this.value

    override fun isSuccess(): Boolean = true
    override fun isFailure(): Boolean = false
}

data class Failure<T>(val error: String) : Try<T>() {
    override fun valueOnFailure(mapper: (String) -> T): T = mapper(error)
    override fun <U> map(mapper: (t: T) -> U): Try<U> = Failure(error)

    override fun isSuccess(): Boolean = false
    override fun isFailure(): Boolean = true
}

sealed class Try<T> {
    abstract fun isSuccess(): Boolean
    abstract fun isFailure(): Boolean
    abstract fun <U>map(mapper: (t: T) -> U): Try<U>
    abstract fun valueOnFailure(mapper: (String) -> T): T

    companion object {
        operator fun <T> invoke(body: () -> T): Try<T> {
            return try {
                Success(body())
            } catch (e: Exception) {
                Failure(e.localizedMessage)
            }
        }
    }
}