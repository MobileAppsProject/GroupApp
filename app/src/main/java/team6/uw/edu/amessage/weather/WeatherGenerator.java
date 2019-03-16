package team6.uw.edu.amessage.weather;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to generate Weather forecasts.
 */
public final class WeatherGenerator {

    public static final List<WeatherDetail> WEATHER_FORECASTS; // list of weather forecasts
    public static final int COUNT = 10; // initial count for number of days


    static {
        WEATHER_FORECASTS = new ArrayList<>();
        for (int i = 0; i < COUNT; i++) {
            WEATHER_FORECASTS.add(i, new WeatherDetail
                    .Builder("Day " + i,
                    "0", "cloudy")
                    .build());
        }
    }


    private WeatherGenerator() {
    }


}
