package com.example.track2success;

public class MiscellaneousIncome extends Income {
    private String details;

    public MiscellaneousIncome( double amount, String date, String details) {
        super(amount, date, details);
        this.details = details;
    }


    // Overriding the getDescription method to add details specific to MiscellaneousExpense
    @Override
    public String getDescription() {
        return details + super.getDescription();
    }
}