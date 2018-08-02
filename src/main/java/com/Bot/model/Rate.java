package com.Bot;

import lombok.Data;

import java.util.List;

@Data
public class Rate {

    private String date;
    private String bank;
    private String baseCurrency;
    private String baseCurrencyLit;
    private List<ExchangeRate> exchangeRate;

}
