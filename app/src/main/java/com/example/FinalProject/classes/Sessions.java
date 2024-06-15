package com.example.FinalProject.classes;

public class Sessions {

    private int id, nbrSessions, price;
    private String type;

    public int getNbrSessions() {
        return nbrSessions;
    }

    public void setNbrSessions(int nbrSessions) {
        this.nbrSessions = nbrSessions;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
