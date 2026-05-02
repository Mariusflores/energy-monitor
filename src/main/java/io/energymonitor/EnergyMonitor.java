package io.energymonitor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import io.babyredis.client.BabyRedisClient;
import io.babyredis.error.BabyRedisException;

public class EnergyMonitor {

    private final BabyRedisClient client;
    private final ApiClient apiClient = new ApiClient();
    public EnergyMonitor(BabyRedisClient client) {
        this.client = client;
    }

    public String getCurrentPrice() {
        LocalDateTime currentDate = LocalDateTime.now();
        String price;
        try {
            price = client.get("price:" + currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd:hh")));
        
        } catch (BabyRedisException e) {
            System.err.println("Error fetching from cache: " + e.getMessage());
            System.out.println("fetching from API...");
            List<EnergyPrice> prices = apiClient.fetchCurrentPrice(currentDate.format(DateTimeFormatter.ofPattern("yyyy/MM-dd")));
            updateCache(prices);
            price = client.get("price:" + currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd:hh")));

        }
        return price;
    }

    private void updateCache(List<EnergyPrice> prices) {
        for (int i = 0; i < prices.size(); i++) {
            EnergyPrice price = prices.get(i);
            String dateString = price.getTime_start().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd:hh"));
            String key = "price:" + dateString;
            String value = String.format("%.4f EUR/kWh", price.getEUR_per_kWh());

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


}
