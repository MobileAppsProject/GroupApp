package team6.uw.edu.amessage;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

import team6.uw.edu.amessage.utils.SendPostAsyncTask;
import team6.uw.edu.amessage.weather.WeatherDetail;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int MY_PERMISSIONS_LOCATIONS = 8414;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private TextView mLocationTextView;
    private boolean mIteration;
    private ArrayList<Location> mFaves;
    private String mCityName;
    private double mTemp;


    public WeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v =  inflater.inflate(R.layout.fragment_weather, container, false);
        Button b = (Button) v.findViewById(R.id.weatherFragment_map_button);
        //Use a method reference to add the OnClickListener
        b.setOnClickListener(this::onMapButtonClicked);
        Button e = (Button) v.findViewById(R.id.fragWeather_extend_button);
        e.setOnClickListener(this::onHourlyButtonClicked);
        Button ten = (Button) v.findViewById(R.id.fragWeather_10_button);
        ten.setOnClickListener(this::onExtendedButtonClicked);
        ImageButton z = (ImageButton) v.findViewById(R.id.fragWeather_zipSearch_Button);
        z.setOnClickListener(this::onZipSearchButtonClicked);

        Button fav = (Button) v.findViewById(R.id.weatherFrag_fav_button);
        fav.setOnClickListener(this::setAsFave);

        Button vf = (Button) v.findViewById(R.id.weatherFrag_viewFav_button);
        vf.setOnClickListener(this::viewFave);


        return v;
    }

    private void viewFave(View view) {
        Fragment frag = new WeatherFaveFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        if(mIteration) { // this come from map press
            transaction.add(R.id.map, frag)
                    .addToBackStack(null);
        } else {
            transaction.add(R.id.fragmentContainer, frag).addToBackStack(null);
        }
        // Commit the transaction
        transaction.commit();
    }

    private void setAsFave(View view) {
        if(mCurrentLocation != null) {
            mFaves.add(mCurrentLocation);
        }
        String theString = mCityName + " : " + mTemp + " ºF";
        Toast.makeText(getActivity(), "Location added to faves", Toast.LENGTH_LONG).show();
//        if (myFlag) {/
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String defaultValue = sharedPref.getString("myLocationz", null);
        if (defaultValue == null) {
            defaultValue = "";
        } else
        if (!defaultValue.contains(mCityName)){
            defaultValue += "\n";
            defaultValue += theString;
        }

        Log.d("Armoni", "This is value: " + defaultValue);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("myLocationz", defaultValue);
        editor.commit();

    }

    private void onZipSearchButtonClicked(View view) {
        System.out.println("button clicked");
        EditText zipCodeEdit = getActivity().findViewById(R.id.weatherFrag_Zip_EditText);
        String zipCode = zipCodeEdit.getText().toString();
        String uri = new Uri.Builder()
                .scheme("https")
                .appendPath("chat450.herokuapp.com")
                .appendPath("weather")
                .appendPath("currentConditions")
                .build().toString();
        JSONObject zipJson = new JSONObject();
        // send in the zip code
        try {
            zipJson.put("zipcode", zipCode);
            //System.out.println("zip was put; it was " + zipCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(uri, zipJson)
                .onPostExecute(this::handleSendOnPostExecute)
                .onCancelled(error -> Log.e("WRONG", error))
                .build().execute();

    }

    private void handleSendOnPostExecute(final String result) {
        System.out.println("entered postexecute");
        try {
            //this is the result from the web service
            JSONObject root = new JSONObject(result);
            //System.out.println("Response from web service: " + root.toString());
            if(root.has("weatherData")) {
                JSONArray arr = root.getJSONArray("weatherData");
                System.out.println("root json" + arr.toString());
                for(int i = 0; i < arr.length(); i++) {
                    JSONObject arrObj = arr.getJSONObject(i);

                    JSONObject innerWeather = arrObj.getJSONObject("weather");
                    String desc = innerWeather.getString("description");
                    //System.out.println("current activity is: " + getActivity().toString());
                    if(getActivity() != null) { //handle exceptions
                        TextView descTextView = (TextView) getActivity().findViewById(R.id.weatherFrag_Desc_TextView);
                        System.out.println("root json desc is" + desc);
                        descTextView.setText(desc);


                        String icon_url = innerWeather.getString("icon");
                        String img_url = "https://www.weatherbit.io/static/img/icons/" + icon_url + ".png";
                        new DownloadImageTask((ImageView) getActivity().findViewById(R.id.imageView3))
                                .execute(img_url);

                        double temp = convertToFahrenheit(Double.parseDouble(arrObj.getString("temp")));
                        mTemp = temp;
                        TextView currentTextView = (TextView) getActivity().findViewById(R.id.weatherFrag_Curr_TextView);
                        currentTextView.setText(temp + " ºF");
                        System.out.println("root json state is" + arrObj.get("state_code"));
                        System.out.println("root json; i = " + i + " temp is" + temp);

                        String cityName = arrObj.get("city_name").toString();
                        mCityName = cityName;
                        TextView cityTextView = (TextView) getActivity().findViewById(R.id.weatherFrag_City_TextView);
                        cityTextView.setText("Weather for " + cityName);

                        // Update location which will be populated on forecasts
                        String theLon = arrObj.get("lon").toString();
                        System.out.print("lon: " + theLon + " response from web service");
                        mCurrentLocation.setLongitude(Double.parseDouble(theLon));
                        String theLat = arrObj.get("lat").toString();
                        System.out.print("lat: " + theLat + " response from web service");
                        mCurrentLocation.setLatitude(Double.parseDouble(theLat));


                        WeatherDetail.Builder weatherObj = new WeatherDetail.Builder("date", arrObj.getString("temp"), desc);
                    }
                }


            } else {
                Log.e("ERROR!", "No response");
            }
        } catch(JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
        }
    }

    private double convertToFahrenheit(double v) {
        return (v * 9/5) + 32;
    }

    private void onHourlyButtonClicked(View view) {
        String uri = new Uri.Builder()
                .scheme("https")
                .appendPath("chat450.herokuapp.com")
                .appendPath("weather")
                .appendPath("24HourForecast")
                .build().toString();

        JSONObject extendJson = new JSONObject();
        // send in the lon and lat
        System.out.println("on hourly lon: " + mCurrentLocation.getLongitude() + " response from web service");
        System.out.println("on hourly lat: " + mCurrentLocation.getLatitude() + " response from web service");

        try {
            extendJson.put("lon", mCurrentLocation.getLongitude());
            extendJson.put("lat", mCurrentLocation.getLatitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(uri, extendJson)
                .onPostExecute(this::handleSendOnPostExecuteHourly)
                .onCancelled(error -> Log.e("WRONG", error))
                .build().execute();
    }

    private void onExtendedButtonClicked(View view) {
        String uri = new Uri.Builder()
                .scheme("https")
                .appendPath("chat450.herokuapp.com")
                .appendPath("weather")
                .appendPath("10DayForecast")
                .build().toString();

        JSONObject extendJson = new JSONObject();
        // send in the lon and lat
        try {
            extendJson.put("lon", mCurrentLocation.getLongitude());
            extendJson.put("lat", mCurrentLocation.getLatitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(uri, extendJson)
                .onPostExecute(this::handleSendOnPostExecuteExtend)
                .onCancelled(error -> Log.e("WRONG", error))
                .build().execute();
    }

    private void handleSendOnPostExecuteExtend(String s) {
        System.out.println("entered EXTEND postexecute");
        try {
            //this is the result from the web service
            JSONObject root = new JSONObject(s);
            System.out.println("extend Response from web service: " + root.toString());

            if(root.has("weatherData")) {
                JSONObject weatherObj = root.getJSONObject("weatherData");
                System.out.println("extend weatherObj" + weatherObj.toString());
                JSONArray weatherArr = weatherObj.getJSONArray("data");
                System.out.println("extend weatherArr" + weatherArr.toString());

                ArrayList<WeatherDetail> theList = new ArrayList<>();
                for(int i = 0; i < weatherArr.length(); i++) {
                    JSONObject theHrWeather = weatherArr.getJSONObject(i);
                    System.out.println("extend arr get" + i + " , " + theHrWeather.toString());
                    String date = theHrWeather.getString("valid_date");
                    date = date.substring(date.indexOf("-")+ 1);
                    String temp = Integer.toString((int) convertToFahrenheit(Double.parseDouble(theHrWeather.getString("temp")))) + " ºF";
                    JSONObject innerWeather = theHrWeather.getJSONObject("weather");
                    String desc = innerWeather.getString("icon"); // "description"
                    WeatherDetail wd = new WeatherDetail.Builder(date, temp, desc).build();
                    theList.add(wd);
                }
                WeatherDetail [] weatherAsArray = new WeatherDetail[theList.size()];
                weatherAsArray = theList.toArray(weatherAsArray);
                Bundle args = new Bundle();
                args.putSerializable(WeatherForecastFragment.ARG_HR_LIST, weatherAsArray);
                Fragment frag = new WeatherForecastFragment();
                frag.setArguments(args);

                if(mIteration){ // this means the location was set on a map
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.map, frag)
                            .addToBackStack(null);
                    // Commit the transaction
                    transaction.commit();
                } else { // this means location was not set on a map
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.fragmentContainer, frag)
                            .addToBackStack(null);
                    // Commit the transaction
                    transaction.commit();
                }


            } else {
                Log.e("ERROR!", "No response");
            }
        } catch(JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
        }
    }

    private void handleSendOnPostExecuteHourly(String s) {
        System.out.println("entered HOURLY postexecute");
        try {
            //this is the result from the web service
            JSONObject root = new JSONObject(s);
            //System.out.println("hourly Response from web service: " + root.toString());

            if(root.has("weatherData")) {
                JSONObject weatherObj = root.getJSONObject("weatherData");
                System.out.println("hourly weatherObj" + weatherObj.toString());
                JSONArray weatherArr = weatherObj.getJSONArray("data");
                System.out.println("hourly weatherArr" + weatherArr.toString());

                ArrayList<WeatherDetail> theList = new ArrayList<>();
                for(int i = 0; i < weatherArr.length(); i++) {
                    JSONObject theHrWeather = weatherArr.getJSONObject(i);
                    System.out.println("hourly arr get" + i + " , " + theHrWeather.toString());
                    String date = theHrWeather.getString("timestamp_local");
                    date = date.substring(date.indexOf("T")+ 1);
                    String temp = Integer.toString((int) convertToFahrenheit(Double.parseDouble(theHrWeather.getString("temp")))) + " ºF";
                    JSONObject innerWeather = theHrWeather.getJSONObject("weather");
                    String desc = innerWeather.getString("icon"); // "description"
                    WeatherDetail wd = new WeatherDetail.Builder(date, temp, desc).build();
                    theList.add(wd);
                }
                WeatherDetail [] weatherAsArray = new WeatherDetail[theList.size()];
                weatherAsArray = theList.toArray(weatherAsArray);
                Bundle args = new Bundle();
                args.putSerializable(WeatherForecastFragment.ARG_HR_LIST, weatherAsArray);
                Fragment frag = new WeatherForecastFragment();
                frag.setArguments(args);

                if(mIteration){ // this means the location was set on a map
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.map, frag)
                            .addToBackStack(null);
                    // Commit the transaction
                    transaction.commit();
                } else { // this means location was not set on a map
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.fragmentContainer, frag)
                            .addToBackStack(null);
                    // Commit the transaction
                    transaction.commit();
                }



            } else {
                Log.e("ERROR!", "No response");
            }
        } catch(JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
        }
    }

    private void onMapButtonClicked(View view) {
        Toast.makeText(getActivity(), "Click anywhere on map to get location's weather",
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getActivity(), MapsActivity.class);
        intent.putExtra("LOCATION", mCurrentLocation);
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFaves = new ArrayList<Location>();
        if(getArguments() != null) { //this came from the map
            mIteration = true;
            Location loc = new Location("dummy");
            loc.setLatitude(getArguments().getDouble("newLat"));
            loc.setLongitude(getArguments().getDouble("newLon"));
            System.out.println(" received" + getArguments().getDouble("newLat") + "," + getArguments().getDouble("newLon"));
            callWebService(loc);
        } else {
            mIteration = false;
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                                , Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_LOCATIONS);
            } else {
                //The user has already allowed the use of Locations. Get the current location.
                requestLocation();
            }


            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        // Update UI with location data
                        // ...
                        setLocation(location);
                        Log.d("LOCATION UPDATE!", location.toString());
                    }
                }
            };
            createLocationRequest();
        }

        //Send this to Success Activity
       /* SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String defaultValue = sharedPref.getString("myCurrWeather", null);
        if (defaultValue == null) {
            defaultValue = "";
        } else
        if (!defaultValue.contains(mCityName)){
            defaultValue += "\n";
            defaultValue += "The weather in " + mCityName + " is " + mTemp ;
        }

        Log.d("Armoni", "This is value: " + defaultValue);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("myCurrWeather", defaultValue);
        editor.commit();*/

    }

    private void callWebService(Location loc) {
        setLocation(loc);
        Log.d("LOCATION", loc.toString());
                                    /*String URL1 = "http://api.openweathermap.org/data/2.5/weather?lat=" + mCurrentLocation.getLatitude() + "&lon=" + mCurrentLocation.getLongitude()+"&APPID=003942e5fb86cbeeffc6856a9f5ccf26&units=imperial";
                                    new ReadPlacesFeedTask().execute(URL1);*/
        String uri = new Uri.Builder()
                .scheme("https")
                .appendPath("chat450.herokuapp.com")
                .appendPath("weather")
                .appendPath("currentConditions")
                .build().toString();
        JSONObject zipJson = new JSONObject();
        // send in the zip code
        try {
            zipJson.put("lon", mCurrentLocation.getLongitude());
            zipJson.put("lat", mCurrentLocation.getLatitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(uri, zipJson)
                .onPostExecute(this::handleSendOnPostExecute)
                .onCancelled(error -> Log.e("WRONG", error))
                .build().execute();
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("REQUEST LOCATION", "User did NOT allow permission to request location!");
        } else {
            if(mFusedLocationClient != null) {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    setLocation(location);
                                    Log.d("LOCATION", location.toString());
                                    /*String URL1 = "http://api.openweathermap.org/data/2.5/weather?lat=" + mCurrentLocation.getLatitude() + "&lon=" + mCurrentLocation.getLongitude()+"&APPID=003942e5fb86cbeeffc6856a9f5ccf26&units=imperial";
                                    new ReadPlacesFeedTask().execute(URL1);*/
                                    String uri = new Uri.Builder()
                                            .scheme("https")
                                            .appendPath("chat450.herokuapp.com")
                                            .appendPath("weather")
                                            .appendPath("currentConditions")
                                            .build().toString();
                                    JSONObject zipJson = new JSONObject();
                                    // send in the zip code
                                    try {
                                        zipJson.put("lon", mCurrentLocation.getLongitude());
                                        zipJson.put("lat", mCurrentLocation.getLatitude());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    new SendPostAsyncTask.Builder(uri, zipJson)
                                            .onPostExecute(this::handleSendOnPostExecute)
                                            .onCancelled(error -> Log.e("WRONG", error))
                                            .build().execute();
                                }
                            }

                            /**
                             * Call the outer class' handleSendOnPostExecute function
                             * @param s: passed onto corresponding outer class function
                             */
                            private void handleSendOnPostExecute(String s) {
                                WeatherFragment.this.handleSendOnPostExecute(s);
                            }
                        });
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    /**
     * Create and configure a Location Request used when retrieving location updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void setLocation(final Location location) {
        mCurrentLocation = location;

        System.out.println("location is " + mCurrentLocation.getLatitude() + " " + mCurrentLocation.getLongitude());
        //mLocationTextView = getActivity().findViewById(R.id.weatherFrag_Coord_TextView);
        System.out.println(" setLocation getLat" + mCurrentLocation.getLatitude());
        /*if(mLocationTextView != null) {
            mLocationTextView.setText("Coords: " + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
        }*/
    }

}