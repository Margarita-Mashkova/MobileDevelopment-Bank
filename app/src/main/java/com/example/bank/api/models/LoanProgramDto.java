package com.example.bank.api.models;

public class LoanProgramDto {
    private int id;
    private String loanProgramName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLoanProgramName() {
        return loanProgramName;
    }

    public void setLoanProgramName(String loanProgramName) {
        this.loanProgramName = loanProgramName;
    }
}
