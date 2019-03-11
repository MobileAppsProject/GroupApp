package team6.uw.edu.amessage.weather;

import java.util.ArrayList;
import java.util.List;

public final class WeatherGenerator {

    public static final List<WeatherDetail> WEATHER_FORECASTS;
    public static final int COUNT = 10;


    static {
        WEATHER_FORECASTS = new ArrayList<>();
        for (int i = 0; i < COUNT; i++) {
            WEATHER_FORECASTS.add(i, new WeatherDetail
                    .Builder("Day " + i,
                    "0","cloudy")
                    .build());
        }
    }


    private WeatherGenerator() {
    }


}
