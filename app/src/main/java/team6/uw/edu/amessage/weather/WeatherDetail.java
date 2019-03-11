package team6.uw.edu.amessage.weather;

import java.io.Serializable;

/**
 * Class to encapsulate a ContactDetail. Building an Object requires a publish date and title.
 *
 * Optional fields include URL, teaser, and Author.

 */
public class WeatherDetail implements Serializable {

    private final String mDate;
    private final String mTemp;
    private final String mDescription;

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
         */
        public Builder(String date, String temp, String description) {
            this.mDate = date;
            this.mTemp = temp;
            this.mDescription = description;
        }

        public WeatherDetail build() {
            return new WeatherDetail(this);
        }

    }

    private WeatherDetail(final Builder builder) {
        this.mDate = builder.mDate;
        this.mTemp = builder.mTemp;
        this.mDescription = builder.mDescription;
    }

    public String getDate() {
        return mDate;
    }

    public String getTemp() {
        return mTemp;
    }

    public String getDescription() {
        return mDescription;
    }



}
