/**
 * Main application class for Track2Success using JavaFX.
 * Manages the user interface for tracking expenses and incomes.
 */

package com.example.track2success;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.Map;

public class Track2Success extends Application {

    // Initializing the main Accountant instance to manage financial data
    private Accountant accountant = new Accountant();

    // Creating the UI components for displaying expenses and incomes
    private ListView<String> expenseListView = new ListView<>();
    private ListView<String> incomeListView = new ListView<>();

    @Override
    public void start(Stage primaryStage) {
        SplitPane root = new SplitPane();
        root.setPadding(new Insets(15));



        setupListViewContextMenu(expenseListView, true);
        setupListViewContextMenu(incomeListView, false);

        // Setting up the left side (Expenses)
        VBox leftVBox = new VBox(10);
        leftVBox.setStyle("-fx-background-color: #FFCCCC;"); // Light red background
        Label expenseLoggerLabel = new Label("Expense Logger");
        expenseLoggerLabel.setFont(new Font("Arial", 20)); // Set font size and family as desired
        leftVBox.getChildren().add(expenseLoggerLabel);
        leftVBox.getChildren().add(createExpenseForm());
        leftVBox.getChildren().add(expenseListView);
        VBox.setVgrow(expenseListView, Priority.ALWAYS); // Make the ListView grow as needed

        // Setting up the right side (Incomes)
        VBox rightVBox = new VBox(10);
        rightVBox.setStyle("-fx-background-color: #CCFFCC;"); // Light green background
        Label incomeLoggerLabel = new Label("Income Logger");
        incomeLoggerLabel.setFont(new Font("Arial", 20)); // Set font size and family as desired
        rightVBox.getChildren().add(incomeLoggerLabel);
        rightVBox.getChildren().add(createIncomeForm());
        rightVBox.getChildren().add(incomeListView);
        VBox.setVgrow(incomeListView, Priority.ALWAYS); // Make the ListView grow as needed

        // Ensuring the Generate Graph button is at the bottom of the right VBox
        rightVBox.getChildren().add(new Region()); // This will push the button to the bottom
        Button generateGraphButton = new Button("Generate Graph");
        generateGraphButton.setOnAction(e -> generateGraph(primaryStage));
        rightVBox.getChildren().add(generateGraphButton);

        // Creating an HBox layout with spacing of 10 pixels between children
        HBox buttonBox = new HBox(10);

        // Creating the "Generate Report" button and setting its action
        Button generateReportButton = new Button("Generate Report");
        generateReportButton.setOnAction(e -> generateReport());

        // Creating the "Save Report" button and set its action
        Button saveReportButton = new Button("Save Report");
        saveReportButton.setOnAction(e -> accountant.generateWeeklyReports());

        // Adding the buttons to the HBox
        buttonBox.getChildren().addAll(generateReportButton, saveReportButton);

        // Adding the HBox to the left VBox
        leftVBox.getChildren().add(buttonBox);



        // Ensuring equal width for both VBoxes
        leftVBox.prefWidthProperty().bind(root.widthProperty().multiply(0.5));
        rightVBox.prefWidthProperty().bind(root.widthProperty().multiply(0.5));

        // Adding both VBoxes to the SplitPane
        root.getItems().addAll(leftVBox, rightVBox);
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Track2Success -- Simanta Limbu: 50300556");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //Sorting a given ListView based on dates in its items
    private void sortListView(ListView<String> listView) {
        Comparator<String> byDate = Comparator.comparing(s -> {
            try {
                return LocalDate.parse(s.substring(s.lastIndexOf(": ") + 2));
            } catch (DateTimeParseException e) {
                System.err.println("Error parsing date: " + e.getMessage());
                return LocalDate.MIN;
            }
        });
        listView.getItems().sort(byDate);
    }

    //Creating and returning a grid pane for the expense form
    private GridPane createExpenseForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);


        //Adding form elements to the grid pane
        Label categoryLabel = new Label("Category:");
        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll("Groceries", "Utilities", "Entertainment", "Miscellaneous");

        Label nameLabel = new Label("Type:");
        TextField typeField = new TextField();

        Label detailsLabel = new Label("Details:");
        TextField detailsField = new TextField();

        Label amountLabel = new Label("Amount:");
        TextField amountField = new TextField();

        Label dateLabel = new Label("Date:");
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());

        Button addButton = new Button("Add Expense");
        addButton.setOnAction(e -> {
            try {
                String category = categoryComboBox.getValue();
                String name = typeField.getText();
                double amount = Double.parseDouble(amountField.getText());
                LocalDate date = datePicker.getValue();
                String dateString = date.toString();

                Expense expense;
                switch (category) {
                    case "Groceries":
                        expense = new Groceries(name, amount, dateString, category);
                        break;
                    case "Utilities":
                        String type = typeField.getText();
                        expense = new Utilities(name, amount, dateString, type, category);
                        break;
                    case "Entertainment":
                        String type2 = typeField.getText();
                        expense = new Entertainment(name, amount, dateString, type2, category);
                        break;
                    case "Miscellaneous":
                        String details = detailsField.getText();
                        expense = new MiscellaneousExpense(name, amount, dateString, details, category);
                        break;
                    default:
                        expense = new Expense(name, amount, dateString, category);
                        break;
                }
                accountant.addExpense(expense);

                expenseListView.getItems().add(expense.getDescription());
                sortListView(expenseListView);

                typeField.clear();
                detailsField.clear();
                amountField.clear();
                datePicker.setValue(LocalDate.now());
                categoryComboBox.getSelectionModel().clearSelection();


                typeField.clear();
                detailsField.clear();

            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid number for the amount.");
            }
        });

        grid.add(categoryLabel, 0, 0);
        grid.add(categoryComboBox, 1, 0);
        grid.add(nameLabel, 0, 1);
        grid.add(typeField, 1, 1);
        grid.add(detailsLabel, 0, 2);
        grid.add(detailsField, 1, 2);
        grid.add(amountLabel, 0, 3);
        grid.add(amountField, 1, 3);
        grid.add(dateLabel, 0, 4);
        grid.add(datePicker, 1, 4);
        grid.add(addButton, 1, 5);

        return grid;
    }

    // Grid pane creation, similar to the expense form
    private GridPane createIncomeForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        Label categoryLabel = new Label("Category:");
        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll("Salary", "Stock Growth", "Miscellaneous");

        Label amountLabel = new Label("Amount:");
        TextField amountField = new TextField();

        Label dateLabel = new Label("Date:");
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());

        Label detailsLabelInc = new Label("Details:");
        TextField detailsField = new TextField();


        Button addButton = new Button("Add Income");
        addButton.setOnAction(e -> {
            try {
                String category = categoryComboBox.getValue();
                double amount = Double.parseDouble(amountField.getText());
                LocalDate date = datePicker.getValue();
                String dateString = date.toString();

                Income income;
                switch (category) {
                    case "Salary":
                        income = new Salary(amount, dateString, category);
                        break;
                    case "Stock Growth":
                        income = new StockGrowth(amount, dateString, category);
                        break;
                    case "Miscellaneous":
                        String detailsIncome = detailsField.getText();
                        income = new MiscellaneousIncome(amount, dateString, category);
                        break;
                    default:
                        income = new Income(amount, dateString, category);
                        break;
                }
                accountant.addIncome(income);

                incomeListView.getItems().add(income.getDescription());
                sortListView(incomeListView);

                amountField.clear();
                datePicker.setValue(LocalDate.now());
                categoryComboBox.getSelectionModel().clearSelection();



            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid number for the amount.");
            }
        });

        grid.add(categoryLabel, 0, 0);
        grid.add(categoryComboBox, 1, 0);
        grid.add(detailsLabelInc, 0, 2);
        grid.add(detailsField, 1, 2);
        grid.add(amountLabel, 0, 4);
        grid.add(amountField, 1, 4);
        grid.add(dateLabel, 0, 5);
        grid.add(datePicker, 1, 5);
        grid.add(addButton, 1, 6);

        return grid;
    }
    //Graph generation
    private void generateGraph(Stage stage) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Date");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount ($)");
        yAxis.setAutoRanging(true);

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Financial Trends Simanta Limbu: 50300556");

        // Series for Income, Expenses, and Net Savings
        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");
        populateSeriesWithData(incomeSeries, accountant.getDailyIncomes());

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expenses");
        populateSeriesWithData(expenseSeries, accountant.getDailyExpenses());

        XYChart.Series<String, Number> netSavingsSeries = new XYChart.Series<>();
        netSavingsSeries.setName("Net Savings");
        populateSeriesWithData(netSavingsSeries, accountant.getDailySavings());

        lineChart.getData().addAll(incomeSeries, expenseSeries, netSavingsSeries);

        Scene graphScene = new Scene(lineChart, 800, 600);
        Stage graphStage = new Stage();
        graphStage.setTitle("Financial Graph: Simanta Limbu: 50300556");
        graphStage.setScene(graphScene);
        graphStage.show();
    }

    //Data population for the graph
    private void populateSeriesWithData(XYChart.Series<String, Number> series, Map<LocalDate, Double> data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");
        data.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // Sorting entries by date
                .forEach(entry -> {
                    LocalDate date = entry.getKey();
                    Double value = entry.getValue();
                    series.getData().add(new XYChart.Data<>(date.format(formatter), value));
                });
    }

    //Shows error alert when the user inputs non numeric amount for the amount field
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //Context menu creation for the list view of income and expenses
    private void setupListViewContextMenu(ListView<String> listView, boolean isExpense) {
        listView.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem deleteItem = new MenuItem();
            deleteItem.textProperty().bind(Bindings.format("Delete \"%s\"", cell.itemProperty()));
            deleteItem.setOnAction(event -> {
                String item = cell.getItem();
                listView.getItems().remove(item);
                if (isExpense) {
                    accountant.removeExpense(item);
                } else {
                    accountant.removeIncome(item);
                }
            });

            contextMenu.getItems().add(deleteItem);

            cell.textProperty().bind(cell.itemProperty());
            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell;
        });
    }

    //Report generation
    private void generateReport() {
        Stage reportStage = new Stage();
        VBox reportLayout = new VBox(10);
        reportLayout.setPadding(new Insets(10));
        reportLayout.setStyle("-fx-background-color: white;"); // Set a neutral background

        // Create a table for expenses
        TableView<Map.Entry<String, Double>> expenseTable = createCategoryTable("Expense Breakdown", accountant.getTotalExpensesByCategory());
        // Create a table for income
        TableView<Map.Entry<String, Double>> incomeTable = createCategoryTable("Income Breakdown", accountant.getTotalIncomeByCategory());

        // Calculate net savings
        double totalIncome = accountant.getTotalIncomeByCategory().values().stream().mapToDouble(Double::doubleValue).sum();
        double totalExpenses = accountant.getTotalExpensesByCategory().values().stream().mapToDouble(Double::doubleValue).sum();
        double netSavings = totalIncome - totalExpenses;

        // Create labels for totals with dynamic color for net savings
        Label totalIncomeLabel = new Label(String.format("Total Income: $%.2f", totalIncome));
        Label totalExpensesLabel = new Label(String.format("Total Expenses: $%.2f", totalExpenses));
        Label netSavingsLabel = new Label(String.format("Net Savings: $%.2f", netSavings));

        // Style for net savings label
        String savingsColor = netSavings >= 0 ? "green" : "red";
        netSavingsLabel.setStyle(String.format("-fx-border-color: %s; -fx-border-width: 2px; -fx-border-radius: 5; " +
                "-fx-background-radius: 5; -fx-background-color: %1$s; -fx-text-fill: white; " +
                "-fx-padding: 5;", savingsColor));

        // Add tables and total labels to the report layout
        reportLayout.getChildren().addAll(expenseTable, totalExpensesLabel, incomeTable, totalIncomeLabel, netSavingsLabel);

        Scene reportScene = new Scene(reportLayout);
        reportStage.setTitle("Financial Report by Simanta Limbu: 50300556");
        reportStage.setScene(reportScene);
        reportStage.show();

    }

    // Helper method to create a styled table for categories for the reporting
    private TableView<Map.Entry<String, Double>> createCategoryTable(String title, Map<String, Double> data) {
        TableView<Map.Entry<String, Double>> table = new TableView<>();
        table.setEditable(false);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Make columns fill the space
        table.setPrefHeight(150); // Set preferred height

        // Defining columns
        TableColumn<Map.Entry<String, Double>, String> categoryColumn = new TableColumn<>(title);
        categoryColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getKey()));

        TableColumn<Map.Entry<String, Double>, Number> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().getValue()));

        // Adding columns to table
        table.getColumns().add(categoryColumn);
        table.getColumns().add(amountColumn);

        // Adding data to table
        table.getItems().addAll(data.entrySet());

        return table;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
