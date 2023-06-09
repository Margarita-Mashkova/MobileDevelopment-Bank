package com.example.bank.api.models;

import java.util.Map;

public class DepositDto {
    private Integer id;
    private String depositName;
    private double depositInterest;
    private int clerkId;
    private Map<Integer, String> depositClients;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDepositName() {
        return depositName;
    }

    public void setDepositName(String depositName) {
        this.depositName = depositName;
    }

    public double getDepositInterest() {
        return depositInterest;
    }

    public void setDepositInterest(double depositInterest) {
        this.depositInterest = depositInterest;
    }

    public int getClerkId() {
        return clerkId;
    }

    public void setClerkId(int clerkId) {
        this.clerkId = clerkId;
    }

    public Map<Integer, String> getDepositClients() {
        return depositClients;
    }

    public void setDepositClients(Map<Integer, String> depositClients) {
        this.depositClients = depositClients;
    }
}
