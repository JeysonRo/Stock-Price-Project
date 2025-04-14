package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import yahoofinance.YahooFinance;
import yahoofinance.Stock;
import yahoofinance.quotes.stock.StockQuote;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App extends Application {
    private static final String SYMBOL = "AAPL";  // Example: Apple Inc.
    private static final int WINDOW_SIZE = 20;    // Number of data points to display
    private XYChart.Series<Number, Number> series;
    private int xSeriesData = 0;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Real-time Stock Price: " + SYMBOL);

        // Create axis
        final NumberAxis xAxis = new NumberAxis(0, WINDOW_SIZE, 1);
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time (seconds)");
        yAxis.setLabel("Price ($)");
        xAxis.setForceZeroInRange(false);
        yAxis.setAutoRanging(true);

        // Create chart
        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Stock Price Over Time");
        lineChart.setAnimated(false);

        // Add data series
        series = new XYChart.Series<>();
        series.setName(SYMBOL + " Price");
        lineChart.getData().add(series);

        // Setup scene
        Scene scene = new Scene(lineChart, 800, 600);
        stage.setScene(scene);
        stage.show();

        // Setup periodic data collection
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            // Update the chart
            Platform.runLater(() -> updateChart());
        }, 0, 10, TimeUnit.SECONDS);

        // Cleanup on window close
        stage.setOnCloseRequest(e -> {
            scheduledExecutorService.shutdown();
            Platform.exit();
        });
    }

    private void updateChart() {
        try {
            Stock stock = YahooFinance.get(SYMBOL);
            if (stock != null) {
                StockQuote quote = stock.getQuote(true);
                BigDecimal price = quote.getPrice();
                
                if (price != null) {
                    System.out.println("Current price of " + SYMBOL + ": $" + price);
                    
                    // Add new data to chart
                    series.getData().add(new XYChart.Data<>(xSeriesData++, price.doubleValue()));

                    // Remove old data to keep the chart clean
                    if (series.getData().size() > WINDOW_SIZE) {
                        series.getData().remove(0);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error fetching stock data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
