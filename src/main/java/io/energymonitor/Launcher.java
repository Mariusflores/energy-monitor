package io.energymonitor;

import java.util.Map;
import java.util.Map.Entry;

import io.babyredis.client.BabyRedisClient;

public class Launcher {

    public static void help() {
        System.out.println("==== Energy Monitor ====");
        System.out.println("Usage:");
        System.out.println("  now              current hour's price");
        System.out.println("  today            all 24 hourly prices");
        System.out.println("  cheapest         cheapest hours today");
        System.out.println("  average          today's average price");
        System.out.println("  help             Show this message");
        System.out.println("=======================");
    }

    public static void main(String[] args) {
        BabyRedisClient client = new BabyRedisClient("localhost", 6379);
        EnergyMonitor monitor = new EnergyMonitor(client);
        if (args.length == 0) {
            help();
            return;
        }

        String command = args[0].toLowerCase();
        switch (command) {
            case "now" -> {
                try {
                    double price = monitor.getCurrentPrice();
                    System.out.println(String.format(
                            "Current hour's price: %.4f NOK/kWh", price));
                } catch (Exception e) {
                    System.err.println("Error fetching current price: " + e.getMessage());
                }
            }
            case "today" -> {
                try {
                    Map<String, Double> prices = monitor.getTodaysPrices();

                    prices.forEach((key, value) -> {
                        System.out.println(String.format("Price: %.4f at %s", value, key));
                    });

                } catch (Exception e) {
                    System.err.println("Error fetching todays prices: " + e.getMessage());
                }
            }
            case "cheapest" -> {
                try {
                    Entry<String, Double> cheapest = monitor.getTodaysCheapestHour();

                    System.out.println(
                            String.format("Cheapest price: %.4f - at %s:00", cheapest.getValue(), cheapest.getKey()));
                } catch (Exception e) {
                    System.err.println("Error fetching cheapest price: " + e.getMessage());
                }
            }

            case "average" -> {
                try{
                    double average = monitor.getTodaysAverage();

                    System.out.println(
                        String.format("Todays average price: %.4f", average)
                    );
                }catch (Exception e){
                    System.err.println("Error fetching Average Price: " + e.getMessage());
                }
            }
            default -> {
                System.out.println("Unknown command: " + command);
                help();
            }
        }
    }
}
