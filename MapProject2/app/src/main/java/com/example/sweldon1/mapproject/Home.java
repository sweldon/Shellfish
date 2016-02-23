package com.example.sweldon1.mapproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.json.*;


public class Home extends ActionBarActivity  implements OnMapReadyCallback
{
    private EditText locationView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        MapFragment mapFragment  = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(myToolbar);
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        map.getUiSettings().setMyLocationButtonEnabled(false);
        boolean gps_enabled;
        boolean network_enabled;
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        map.setMyLocationEnabled(true);

        LatLng me;
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        System.out.println("NETWORK IS "+network_enabled);
        System.out.println("GPS IS "+gps_enabled);

        Location location = null;
        if(gps_enabled && !network_enabled)
        {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        else if(network_enabled && !gps_enabled)
        {
            location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        else if(gps_enabled && network_enabled)
        {
            // might as well use network to save data
            location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        }
        else
        {
            //ask to enable one of them

        }

        if(location != null) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            me = new LatLng(latitude, longitude);
        }
        else
        {
            System.out.println("LOCATION NOT DETECTED! :(");
            me = new LatLng(0, 0);
        }
        // normal, hybrid, terrain, satellite

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 14));
    }

    public void connectDB(View view)
    {
        Intent intent = new Intent(Home.this, DatabaseActivity.class);
        Home.this.startActivity(intent);
    }

    public void pinpointMe(View view)
    {

        locationView = (EditText) findViewById(R.id.location_search);
        final String locationText = locationView.getText().toString();
        System.out.println(locationText);

        try
        {
            String test = "https://maps.googleapis.com/maps/api/geocode/json?address=" + locationText;
            test = test.replace(" ","%20");
            new WebRetrieval().execute(test);
            System.out.println("EXECUTING WEB RETRIEVAL: "+test);

        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("ERROR");
        }

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        boolean gps_enabled = false;
        boolean network_enabled = false;

        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        System.out.println("NETWORK IS "+network_enabled);
        System.out.println("GPS IS "+gps_enabled);

        Location location = null;
        if(gps_enabled && !network_enabled)
        {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        else if(network_enabled && !gps_enabled)
        {
            location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        else if(gps_enabled && network_enabled)
        {
            // might as well use network to save data
            location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        }
        else
        {
            //ask to enable one of them

            location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        }


        LocationListener locationListener = new LocationListener()
        {
            public void onProviderEnabled(String provider)
            {

            }

            @Override
            // onlocationchanged needs to change, causing so many gps references. data + battery = rip
            public void onLocationChanged(Location location)
            {

            }

            public void onStatusChanged(String provider, int status, Bundle extras)
            {

            }

            public void onProviderDisabled(String provider)
            {
            }
        };


        // requestLocationUpdates params: provider, mintime, mindistance, intent (which is locationListener)

        //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 600000, 500, locationListener);
        //locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener);


    }

    //Starting new activity
//    public void plotme(View view) {
//        Intent intent = new Intent(Home.this, MapsActivity.class);
//        Home.this.startActivity(intent);
//    }
//
    private class WebRetrieval extends AsyncTask<String, Void, String>
    {
        private ProgressDialog pDialog;

        private Exception exception;
        String result = "";

        @Override
        protected void onPreExecute() {
            hideSoftKeyboard();

            super.onPreExecute();
            pDialog = new ProgressDialog(Home.this);
            pDialog.setMessage("Searching...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... inputURL) {

            try {

                URL oracle = new URL(inputURL[0]);
                URLConnection yc = oracle.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        yc.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    //System.out.println(inputLine);
                    result += inputLine;
                in.close();


                System.out.println(result);

            } catch (Exception e) {
                this.exception = e;
                return null;
            }
            System.out.println("RESULT VALUE: "+result);
            result = result.replace(" ","");
            return result;

        }
        //
        @Override
        protected void onPostExecute(String output) {


            // DRAG IN EDIT TEXT, USE TO GET COORDINATES, MOVE MAP
            MapFragment mapFragment  = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            //mapFragment.getMapAsync(this);

            GoogleMap map = mapFragment.getMap();

            System.out.println("DATA RETRIEVED! -> "+output);

            // PARSE JSON TO GET LAT AND LONG, THEN U CAN PLOT
            JSONObject obj;
            String latitude;
            String longitude;
            double lat;
            double lng;

            try {
                if (output == null)
                {
                    Context context = getApplicationContext();
                    CharSequence text = "Please turn on WiFi or Data";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    pDialog.dismiss();
                }
                else
                {
                    System.out.println("PARSING JSON");

                    obj = new JSONObject(output);

                    String status = obj.getString("status");

                    System.out.println(status);

                    if(status.equals("ZERO_RESULTS"))
                    {
                        System.out.println("IT IS NOTHING");
                        latitude = "47.610377";
                        longitude = "-122.2006786";
                        lat = Double.parseDouble(latitude);
                        lng = Double.parseDouble(longitude);
                    }
                    else
                    {
                        JSONArray arr = obj.getJSONArray("results");

                        latitude = arr.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lat");
                        longitude = arr.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lng");

                        lat = Double.parseDouble(latitude);
                        lng = Double.parseDouble(longitude);
                    }

                    System.out.println("LAT AND LNG: " + lat + " " + lng);

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 14));

                    pDialog.dismiss();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    // Close the soft keyboard
    public void hideSoftKeyboard()
    {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
    //Add the Actions to the Action Bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_settings:

                return true;
            case R.id.action_search:

               //
                return true;
            case R.id.action_boat:

                startActivity(new Intent(Home.this, Info.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
//
//                Intent intent = new Intent(Home.this, Info.class);
//
//
//                Home.this.startActivity(intent);

                //
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }




}