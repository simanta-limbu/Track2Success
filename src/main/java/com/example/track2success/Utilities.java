package com.example.track2success;

public class Utilities extends Expense {
    private String utilityType;


    public Utilities(String name, double amount, String date, String utilityType, String category) {
        super(name, amount, date, category);
        this.utilityType = utilityType;

    }

    @Override
    public String getDescription() {
        String baseDescription = super.getDescription();
        return ", Utility Type: " + utilityType + " " + baseDescription;
    }


}
