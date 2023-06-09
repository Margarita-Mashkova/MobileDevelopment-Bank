package com.example.bank.api.models;

import java.util.Date;
import java.util.Map;

public class ClientDto {
    private Integer id;
    private String clientFIO;
    private String passportData;
    private String telephoneNumber;
    private String dateVisit;
    private int clerkId;
    private Map<Integer, String> clientLoanPrograms;

    public String getClientFIO() {
        return clientFIO;
    }

    public void setClientFIO(String clientFIO) {
        this.clientFIO = clientFIO;
    }

    public String getPassportData() {
        return passportData;
    }

    public void setPassportData(String passportData) {
        this.passportData = passportData;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getDateVisit() {
        String date = dateVisit.split("T")[0];
        String time = dateVisit.split("T")[1].substring(0,8);
        dateVisit = date + " " + time;
        return dateVisit;
    }

    public void setDateVisit(String dateVisit) {
        this.dateVisit = dateVisit;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getClerkId() {
        return clerkId;
    }

    public void setClerkId(int clerkId) {
        this.clerkId = clerkId;
    }

    public Map<Integer, String> getClientLoanPrograms() {
        return clientLoanPrograms;
    }

    public void setClientLoanPrograms(Map<Integer, String> clientLoanPrograms) {
        this.clientLoanPrograms = clientLoanPrograms;
    }
}
