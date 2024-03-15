package com.example.track2success;

public class StockGrowth extends Income {
    public StockGrowth(double amount, String date, String category) {
        super(amount, date, category);
    }

    @Override
    public String getDescription() {
        return "Stock Growth positive " + super.getDescription();
    }
}
