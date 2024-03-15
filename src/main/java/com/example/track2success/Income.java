package com.example.track2success;

public class Income {
    private double amount;
    private String date;

    private String category;

    public Income(double amount, String date, String category) {
        this.amount = amount;
        this.date = date;
        this.category = category;
    }

    // Getters for amount, date and category
    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }


    // Setters for amount, date and category
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    //Providing a string representation of the income, detailing the amount and date. We will parse this later for list sorting.
    public String getDescription() {
        return "Income:  Amount: $" + amount + ", Date: " + date;
    }


}
