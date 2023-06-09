package com.example.bank.api.models;

import java.util.List;

public class AddClientsDto {
    public int depositId;
    public List<Integer> clientsId;

    public int getDepositId() {
        return depositId;
    }

    public void setDepositId(int depositId) {
        this.depositId = depositId;
    }

    public List<Integer> getClientsId() {
        return clientsId;
    }

    public void setClientsId(List<Integer> clientsId) {
        this.clientsId = clientsId;
    }
}
