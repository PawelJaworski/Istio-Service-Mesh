package pl.javorex.poc.istio.cashloans.product.adapter;

import org.jetbrains.annotations.NotNull;
import pl.javorex.poc.istio.cashloans.product.application.ProductQueryFacade;
import pl.javorex.poc.istio.cashloans.product.application.query.GetLoanCost;
import pl.javorex.poc.istio.cashloans.product.application.query.LoanCostFetched;
import pl.javorex.poc.istio.common.message.MessageBus;
import pl.javorex.poc.istio.common.message.listener.MessageListener;

import java.math.BigDecimal;

public class LoanCostFetchedCallback implements MessageListener<GetLoanCost> {
    private final ProductQueryFacade productQuery;

    public LoanCostFetchedCallback(ProductQueryFacade productQuery) {
        this.productQuery = productQuery;
    }

    @Override
    public void onMessage(@NotNull String sourceId, long sourceVersion, GetLoanCost command,
                          @NotNull MessageBus messageBus) {
        BigDecimal loanCost = productQuery.getLoanCost(command);
        LoanCostFetched loanCostFetched = new LoanCostFetched(loanCost);

        messageBus.emit(sourceId, sourceVersion, loanCostFetched);
    }
}
