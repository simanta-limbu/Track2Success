package com.example.track2success;

public class Expense {
    private String name;
    private double amount;
    private String date;

    private String category;

    public Expense(String name, double amount, String date, String category) {
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }

    // Getters
    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }


    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    //Providing a string representation of the Expense, detailing the amount and date. We will parse this later for list sorting.
    public String getDescription() {
        return "Expense: " + name + ", Amount: $" + amount + ", Date: " + date;
    }
}
