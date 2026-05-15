package io.energymonitor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import io.babyredis.client.BabyRedisClient;
import io.babyredis.error.BabyRedisException;

public class EnergyMonitor {

    private final BabyRedisClient client;
    private final ApiClient apiClient = new ApiClient();

    public EnergyMonitor(BabyRedisClient client) {
        this.client = client;
    }

    public double getCurrentPrice() {
        LocalDateTime currentDate = LocalDateTime.now();
        String price;
        try {
            price = client.get("price:" + currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd:HH")));

        } catch (BabyRedisException e) {
            System.err.println("Error fetching from cache: " + e.getMessage());
            System.out.println("fetching from API...");
            List<EnergyPrice> prices = apiClient
                    .fetchCurrentPrice(currentDate.format(DateTimeFormatter.ofPattern("yyyy/MM-dd")));
            updateCache(prices);
            price = client.get("price:" + currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd:HH")));

        }
        return Double.parseDouble(price);
    }

    public Map<String, Double> getTodaysPrices() {
        LocalDateTime currentDate = LocalDateTime.now();

        Map<String, Double> todaysPrices;

        try {
            // Check whether current price exists, should throw error if not existing
            client.get("price:" + currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd:HH")));

            // Exists: fetch all prices
            todaysPrices = fetchTodaysPrices(currentDate);

        } catch (BabyRedisException e) {
            System.err.println("Some prices not found, fetching from API");
            List<EnergyPrice> apiPrices = apiClient
                    .fetchCurrentPrice(currentDate.format(DateTimeFormatter.ofPattern("yyyy/MM-dd")));
            updateCache(apiPrices);
            todaysPrices = fetchTodaysPrices(currentDate);

        }
        return todaysPrices;
    }

    public Entry<String, Double> getTodaysCheapestHour() {
        LocalDateTime currentDate = LocalDateTime.now();
        Map<String, Double> todaysPrices;

        try {
            // Check whether current price exists, should throw error if not existing
            client.get("price:" + currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd:HH")));

            // Exists: fetch all prices
            todaysPrices = fetchTodaysPrices(currentDate);

        } catch (BabyRedisException e) {
            System.err.println("Some prices not found, fetching from API");
            List<EnergyPrice> apiPrices = apiClient
                    .fetchCurrentPrice(currentDate.format(DateTimeFormatter.ofPattern("yyyy/MM-dd")));
            updateCache(apiPrices);
            todaysPrices = fetchTodaysPrices(currentDate);

        }

        return Collections.min(
                todaysPrices.entrySet(), Entry.comparingByValue());
    }

    public double getTodaysAverage(){
        LocalDateTime currentDate = LocalDateTime.now();
        Map<String, Double> todaysPrices;
        double average = 0;


        try{
            // Check whether current price exists, should throw error if not existing
            client.get("price:" + currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd:HH")));

            // Exists: fetch all prices
            todaysPrices = fetchTodaysPrices(currentDate);

            for(Map.Entry<String, Double> entry: todaysPrices.entrySet()){
                double sum = 0;
                int counter = 0;

                sum += entry.getValue();
                counter++;

                average = sum / counter;
            
            }

        }catch (Exception e){
            System.err.println("Error fetching average: " + e);

        }
        return average;

    }

    private void updateCache(List<EnergyPrice> prices) {
        for (int i = 0; i < prices.size(); i++) {
            EnergyPrice price = prices.get(i);
            String dateString = price.getTime_start().toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd:HH"));
            String key = "price:" + dateString;
            String value = String.format("%.4f", price.getNOK_per_kWh());

            try {
                client.set(key, value);
                if (i == 0) {
                    client.set("price:current", value);
                }
            } catch (BabyRedisException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private Map<String, Double> fetchTodaysPrices(LocalDateTime currentDate) {
        Map<String, Double> todaysPrices = new TreeMap<>();
        String[] pricesKeys = client.keys(
                String.format("price:%s*", currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

        for (String priceKey : pricesKeys) {
            String hour = priceKey.split(":")[2];
            todaysPrices.put(hour, Double.parseDouble(client.get(priceKey)));
        }

        return todaysPrices;
    }

}
