
package org.example;
import yahoofinance.YahooFinance;
import yahoofinance.Stock;
import java.io.IOException;
import java.math.BigDecimal;

public class App {
    public static BigDecimal queryStock() throws IOException {
        Stock dowJones = YahooFinance.get("^DJI");
        BigDecimal price = dowJones.getQuote().getPrice();

        return price;
    }

    public static void main(String[] args) throws IOException {
        BigDecimal price = queryStock();

        System.out.println("Dow Jones Price: " + price);
    }
}
