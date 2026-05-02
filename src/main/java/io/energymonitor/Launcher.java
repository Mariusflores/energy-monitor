package io.energymonitor;

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
                    String price = monitor.getCurrentPrice();
                    System.out.println("Current hour's price: " + price);
                } catch (Exception e) {
                    System.err.println("Error fetching current price: " + e.getMessage());
                }
            }
            default -> {
                System.out.println("Unknown command: " + command);
                help();
            }
        }
    }
}
