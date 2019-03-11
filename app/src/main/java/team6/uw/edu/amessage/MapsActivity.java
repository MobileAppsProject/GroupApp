package team6.uw.edu.amessage;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import team6.uw.edu.amessage.weather.WeatherDetail;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, WeatherForecastFragment.OnListFragmentInteractionListener {

    private GoogleMap mMap;
    private Location mCurrentLocation;
    private Marker mMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.mapActivity_fab);
        fab.setOnClickListener(this::onFabClicked);*/
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mCurrentLocation = (Location) getIntent().getParcelableExtra("LOCATION");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void onFabClicked(View view) {
        double lon = mCurrentLocation.getLongitude();
        double lat = mCurrentLocation.getLatitude();
        if(mMarker != null) {
            System.out.println("fab clicked lon:" + mMarker.getPosition().longitude);
            System.out.println("fab clicked lat:" + mMarker.getPosition().latitude);
            lon = mMarker.getPosition().longitude;
            lat = mMarker.getPosition().latitude;
        } else {
            System.out.println("fab clicked lon:" + mCurrentLocation.getLongitude());
            System.out.println("fab clicked lat:" + mCurrentLocation.getLatitude());
        }
        /*Bundle args = new Bundle();
        args.putSerializable("newLon", lon);
        args.putSerializable("newLat", lat);
        Fragment frag = new WeatherFragment();
        frag.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mapActivity_ll, frag)
                .addToBackStack(null);

        // Commit the transaction
        transaction.commit();*/

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in the current device location and move the camera
        if(mCurrentLocation != null) {
            LatLng current = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(current).title("Current Location"));
            //Zoom levels are from 2.0f (zoomed out) to 21.f (zoomed in)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15.0f));
            mMap.setOnMapClickListener(this);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d("LAT/LONG", latLng.toString());
        mMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("New Marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));

        ///////////////////////////////
        Bundle args = new Bundle();
        args.putSerializable("newLon", mMarker.getPosition().longitude);
        args.putSerializable("newLat", mMarker.getPosition().latitude);
        Fragment frag = new WeatherFragment();
        frag.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.map, frag);
                //.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
        ///////////////////////////
    }

    @Override
    public void onWeatherListFragmentInteraction(WeatherDetail item) {

    }
}
