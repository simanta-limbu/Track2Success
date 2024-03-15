package com.example.track2success;


public class Entertainment extends Expense {
    private String type;

    public Entertainment(String name, double amount, String date, String type, String category) {
        super(name, amount, date, category);
        this.type = type;
    }


    // Overriding the getDescription method to add details specific to Entertainment
    @Override
    public String getDescription() {
        // Call the base version and then add extra info
        return type + super.getDescription();
    }
}
