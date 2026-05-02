package io.energymonitor;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EnergyPrice {

    @JsonProperty("NOK_per_kWh")
    private double NOK_per_kWh;
    @JsonProperty("EUR_per_kWh")
    private double EUR_per_kWh;
    @JsonProperty("EXR")
    private double EXR;
    @JsonProperty("time_start")
    private OffsetDateTime time_start;
    @JsonProperty("time_end")
    private OffsetDateTime time_end;

    public double getNOK_per_kWh() {
        return NOK_per_kWh;
    }

    public void setNOK_per_kWh(double nOK_per_kWh) {
        NOK_per_kWh = nOK_per_kWh;
    }

    public double getEUR_per_kWh() {
        return EUR_per_kWh;
    }

    public void setEUR_per_kWh(double eUR_per_kWh) {
        EUR_per_kWh = eUR_per_kWh;
    }

    public double getEXR() {
        return EXR;
    }

    public void setEXR(double eXR) {
        EXR = eXR;
    }
    
    public OffsetDateTime getTime_start() {
        return time_start;
    }
    
    public void setTime_start(OffsetDateTime time_start) {
        this.time_start = time_start;
    }
    
    public OffsetDateTime getTime_end() {
        return time_end;
    }

    public void setTime_end(OffsetDateTime time_end) {
        this.time_end = time_end;
    }
}
