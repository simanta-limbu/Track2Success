package com.example.track2success;

public class Salary extends Income {
    public Salary(double amount, String date, String category) {
        super(amount, date, category);
    }

    @Override
    public String getDescription() {
        return "Biweekly Pay: " +  super.getDescription() ;
    }
}
