package com.example.bank.api.models;

public class ReplenishmentDto {
    private Integer id;
    private int amount;
    private String dateReplenishment;
    private int depositId;
    private String depositName;
    private int clerkId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDateReplenishment() {
        String date = dateReplenishment.split("T")[0];
        String time = dateReplenishment.split("T")[1].substring(0,8);
        dateReplenishment = date + " " + time;
        return dateReplenishment;
    }

    public void setDateReplenishment(String dateReplenishment) {
        this.dateReplenishment = dateReplenishment;
    }

    public int getDepositId() {
        return depositId;
    }

    public void setDepositId(int depositId) {
        this.depositId = depositId;
    }

    public String getDepositName() {
        return depositName;
    }

    public void setDepositName(String depositName) {
        this.depositName = depositName;
    }

    public int getClerkId() {
        return clerkId;
    }

    public void setClerkId(int clerkId) {
        this.clerkId = clerkId;
    }
}
