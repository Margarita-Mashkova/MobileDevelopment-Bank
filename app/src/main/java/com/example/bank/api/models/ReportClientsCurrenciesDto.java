package com.example.bank.api.models;

import java.util.List;

public class ReportClientsCurrenciesDto {

    public String clientFIO;
    public List<String> currencies;

    public String getClientFIO() {
        return clientFIO;
    }

    public void setClientFIO(String clientFIO) {
        this.clientFIO = clientFIO;
    }

    public List<String> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<String> currencies) {
        this.currencies = currencies;
    }
}
