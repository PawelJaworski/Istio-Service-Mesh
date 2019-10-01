package pl.javorex.poc.istio.cashloans.product.domain

interface ProductRepository {
    fun findProductByName(productName: String): LoanProduct
}