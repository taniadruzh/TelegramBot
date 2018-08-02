package com.Bot;

import lombok.Data;

@Data
public class ExchangeRate {
    private String baseCurrency;
    private String currency;
    private String saleRateNB;
    private String purchaseRateNB;
}
