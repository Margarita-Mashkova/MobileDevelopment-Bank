package com.example.bank;

import android.app.Application;

import com.example.bank.api.BankService;
import com.example.bank.api.models.ClerkDto;

public class App extends Application {

    private BankService bankService;

    public static ClerkDto Clerk;

    public ClerkDto getClerk() {
        return Clerk;
    }

    public void setClerk(ClerkDto clerk) {
        Clerk = clerk;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bankService = new BankService();
    }

    public BankService getBankService() {
        return bankService;
    }
}
