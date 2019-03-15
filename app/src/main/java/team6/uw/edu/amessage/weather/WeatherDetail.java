package team6.uw.edu.amessage.weather;

import java.io.Serializable;

/**
 * Class to encapsulate a WeatherDetail.
 * Building an Object requires a date, temp, and description.
 *
 */
public class WeatherDetail implements Serializable {

    private final String mDate; // the date of weather
    private final String mTemp; // the weather temperature
    private final String mDescription; // the description of weather

    /**
     * Helper class for building Weather.
     *
     */
    public static class Builder {
        private final String mDate;
        private final String mTemp;
        private final String mDescription;


        /**
         * Constructs a new Builder.
         *
         * @param date the day
         * @param temp the temperature that day
         * @param description the description of the weather
         */
        public Builder(String date, String temp, String description) {
            this.mDate = date;
            this.mTemp = temp;
            this.mDescription = description;
        }

        /**
         * @return a new WeatherDetail
         */
        public WeatherDetail build() {
            return new WeatherDetail(this);
        }

    }

    /**
     * Private constructor that sets corresponding fields.
     * @param builder built from above.
     */
    private WeatherDetail(final Builder builder) {
        this.mDate = builder.mDate;
        this.mTemp = builder.mTemp;
        this.mDescription = builder.mDescription;
    }

    /**
     * Retrieve date.
     * @return date
     */
    public String getDate() {
        return mDate;
    }

    /**
     * Retrieve temperature.
     * @return temp
     */
    public String getTemp() {
        return mTemp;
    }

    /**
     * Retrieve description of weather.
     * @return description
     */
    public String getDescription() {
        return mDescription;
    }



}
