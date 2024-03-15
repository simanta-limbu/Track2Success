/**
 * This class manages financial records including expenses, incomes, and daily savings. The main objective
 * for me to build this class to have a separate class that will do most of the operations such as creating totals
 * , finding net amounts, keeping list of expenses and income. Sorting, reading and writing files.
 */

package com.example.track2success;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Accountant {

    // Storing expenses and incomes lists, and mapping daily savings
    private List<Expense> expenses;
    private List<Income> incomes;
    private Map<LocalDate, Double> dailySavings;

    public Accountant() {

        //Initializing the Accountant with empty lists and map
        expenses = new ArrayList<>();
        incomes = new ArrayList<>();
        dailySavings = new HashMap<>();
    }

    //Calculating and returning total daily expenses.

    public Map<LocalDate, Double> getDailyExpenses() {
        Map<LocalDate, Double> dailyExpenses = new HashMap<>();
        for (Expense expense : expenses) {
            LocalDate date = LocalDate.parse(expense.getDate());
            dailyExpenses.put(date, dailyExpenses.getOrDefault(date, 0.0) + expense.getAmount());
        }
        return dailyExpenses;
    }

    // Calculating and returning total daily incomes
    public Map<LocalDate, Double> getDailyIncomes() {
        Map<LocalDate, Double> dailyIncomes = new HashMap<>();
        for (Income income : incomes) {
            LocalDate date = LocalDate.parse(income.getDate());
            dailyIncomes.put(date, dailyIncomes.getOrDefault(date, 0.0) + income.getAmount());
        }
        return dailyIncomes;
    }

    //Adding an income record and updating daily savings
    public void addExpense(Expense expense) {
        expenses.add(expense);
        LocalDate date = LocalDate.parse(expense.getDate());
        updateDailySavings(date, -expense.getAmount());
    }

    //Adding an income record and updating daily savings
    public void addIncome(Income income) {
        incomes.add(income);
        LocalDate date = LocalDate.parse(income.getDate());
        updateDailySavings(date, income.getAmount());
    }

    //Updating the daily savings map for a given date
    private void updateDailySavings(LocalDate date, double amount) {
        dailySavings.put(date, dailySavings.getOrDefault(date, 0.0) + amount);
    }

    //Retrieving the daily savings map
    public Map<LocalDate, Double> getDailySavings() {
        return dailySavings;
    }

    // Removing an expense based on its description
    public void removeExpense(String description) {

        // Parsing the description to extract date and amount
        String[] parts = description.split(", ");
        String dateString = parts[2].substring(parts[2].indexOf(": ") + 2);
        LocalDate date = LocalDate.parse(dateString);
        String amountString = parts[1].substring(parts[1].indexOf("$") + 1);
        double amount = Double.parseDouble(amountString);

        expenses.removeIf(expense ->
                expense.getDate().equals(date.toString()) && expense.getAmount() == amount);
        updateDailySavings(date, amount); // Adjust the savings since the expense is removed

    }

    //  Removing an income based on its description
    public void removeIncome(String description) {
        // Similar parsing as in removeExpense
        String[] parts = description.split(", ");
        String dateString = parts[2].substring(parts[2].indexOf(": ") + 2);
        LocalDate date = LocalDate.parse(dateString);
        String amountString = parts[1].substring(parts[1].indexOf("$") + 1);
        double amount = Double.parseDouble(amountString);

        incomes.removeIf(income ->
                income.getDate().equals(date.toString()) && income.getAmount() == amount);
        updateDailySavings(date, -amount); // Adjust the savings since the income is removed

    }

    //Generating a map of total expenses by category
    public Map<String, Double> getTotalExpensesByCategory() {
        Map<String, Double> totals = new HashMap<>();
        for (Expense expense : expenses) {
            String category = expense.getCategory(); // Assumes a getCategory() method in Expense
            totals.put(category, totals.getOrDefault(category, 0.0) + expense.getAmount());
        }
        return totals;
    }

    //Generating a map of total income by category
    public Map<String, Double> getTotalIncomeByCategory() {
        Map<String, Double> totals = new HashMap<>();
        for (Income income : incomes) {
            String category = income.getCategory(); // Assumes a getCategory() method in Income
            totals.put(category, totals.getOrDefault(category, 0.0) + income.getAmount());
        }
        return totals;
    }

    //Grouping expenses by the week of their date
    private Map<LocalDate, List<Expense>> groupExpensesByWeek() {
        return expenses.stream()
                .collect(Collectors.groupingBy(e -> adjustToWeekStart(LocalDate.parse(e.getDate()))));
    }

    //Grouping incomes by the week of their date
    private Map<LocalDate, List<Income>> groupIncomesByWeek() {
        return incomes.stream()
                .collect(Collectors.groupingBy(i -> adjustToWeekStart(LocalDate.parse(i.getDate()))));
    }

    //Generating weekly reports and writing them to files
    public void generateWeeklyReports() {
        Map<LocalDate, List<Expense>> weeklyExpenses = groupExpensesByWeek();
        Map<LocalDate, List<Income>> weeklyIncomes = groupIncomesByWeek();

        weeklyExpenses.keySet().forEach(weekStart -> {
            LocalDate weekEnd = weekStart.plusDays(6);
            String filename = "Weekly_Report_" + weekStart.format(DateTimeFormatter.ofPattern("dd MMM")) + ".txt";
            try (FileWriter writer = new FileWriter(filename)) {
                writer.write("Weekly Report: " + weekStart.format(DateTimeFormatter.ofPattern("dd MMM")) + " - " + weekEnd.format(DateTimeFormatter.ofPattern("dd MMM")) + "\n\n");
                writeExpensesAndIncomes(writer, weeklyExpenses.get(weekStart), weeklyIncomes.getOrDefault(weekStart, Collections.emptyList()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    //Adjusting a date to the start of its week.
    private LocalDate adjustToWeekStart(LocalDate date) {
        return date.minusDays(date.getDayOfWeek().getValue() % 7);
    }


    //Writing grouped expenses and incomes to a file writer.
    private void writeExpensesAndIncomes(FileWriter writer, List<Expense> weekExpenses, List<Income> weekIncomes) throws IOException {
        // Writing expenses grouped by category
        writer.write("Expenses:\n");
        Map<String, Double> expensesByCategory = weekExpenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));
        for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
            writer.write(entry.getKey() + ": $" + entry.getValue() + "\n");
        }

        // Writing incomes grouped by category
        writer.write("\nIncomes:\n");
        Map<String, Double> incomesByCategory = weekIncomes.stream()
                .collect(Collectors.groupingBy(Income::getCategory, Collectors.summingDouble(Income::getAmount)));
        for (Map.Entry<String, Double> entry : incomesByCategory.entrySet()) {
            writer.write(entry.getKey() + ": $" + entry.getValue() + "\n");
        }
    }


}

