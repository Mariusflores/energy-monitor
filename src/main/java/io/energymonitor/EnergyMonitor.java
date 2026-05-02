package io.energymonitor;

import io.babyredis.client.BabyRedisClient;
import io.babyredis.error.BabyRedisException;

public class EnergyMonitor {

    private final BabyRedisClient client;
    public EnergyMonitor(BabyRedisClient client) {
        this.client = client;
    }

    public String getCurrentPrice() {
        try {
            String price = client.get("price:current");
            return price;
        } catch (BabyRedisException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


}
