package com.example.bank.api.models;

public class ClerkDto {
    private Integer id;
    private String clerkFIO;
    private String email;
    private String password;

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClerkFIO() {
        return clerkFIO;
    }

    public void setClerkFIO(String clerkFIO) {
        this.clerkFIO = clerkFIO;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
