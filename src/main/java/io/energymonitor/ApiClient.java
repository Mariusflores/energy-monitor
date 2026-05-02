package io.energymonitor;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


public class ApiClient {
    private final HttpClient httpClient;

    public ApiClient() {
        this.httpClient = HttpClient.newHttpClient();
    }


    public List<EnergyPrice> fetchCurrentPrice(String dateString) {

        var request = HttpRequest.newBuilder()                                          // DateString format 2026/05-02
                .uri(java.net.URI.create("https://www.hvakosterstrommen.no/api/v1/prices/" + dateString + "_NO1.json"))
                .header("accept", "application/json")
                .build();


        
        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            List<EnergyPrice> prices = mapper.readValue(response.body(), new TypeReference<List<EnergyPrice>>() {});

            System.out.println(prices.get(0).getNOK_per_kWh());
                return prices;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Placeholder for actual API call logic
        return new ArrayList<EnergyPrice>(); // Example price
    }
}
