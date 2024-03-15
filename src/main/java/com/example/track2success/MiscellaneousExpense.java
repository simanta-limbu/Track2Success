package com.example.track2success;

public class MiscellaneousExpense extends Expense {
    private String details; // An additional field for miscellaneous details

    public MiscellaneousExpense(String name, double amount, String date, String details, String category) {
        super(name, amount, date, category);
        this.details = details;
    }


    // Override the getDescription method to add details specific to MiscellaneousExpense
    @Override
    public String getDescription() {
        // Include the details in the description, or simply indicate it's a miscellaneous expense
        return "Details: " + details + super.getDescription();
    }
}
