package com.example.sweldon1.mapproject;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Info extends ActionBarActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_info);




        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);

        try {
            sampleStation();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out, R.anim.slide_in);
    }

    public void sampleStation() throws IOException
    {
        // Declaring Text Views
        TextView txtStationId = (TextView)findViewById(R.id.txtStationId);
        TextView txtLatitude = (TextView)findViewById(R.id.txtLatitude);
        TextView txtLongitude = (TextView)findViewById(R.id.txtLongitude);
        TextView txtClassification = (TextView)findViewById(R.id.txtClassifcation);
        TextView txtArea = (TextView)findViewById(R.id.txtArea);
        TextView txtFieldNotes = (TextView)findViewById(R.id.txtFieldNotes);
        TextView txtTown = (TextView)findViewById(R.id.txtTown);

        TextView txtAddress = (TextView)findViewById(R.id.txtAddress);
        TextView txtCity = (TextView)findViewById(R.id.txtCity);
        TextView txtState = (TextView)findViewById(R.id.txtState);

        // Grabbing current location of user at the moment
        Location location = null;
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(location != null) {

            // Gets location based off latitude and longitude
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            String lat = Double.toString(latitude);
            String lon = Double.toString(longitude);

            // Assigning Text to Text Views
            // Strings could be set in 'Strings' section
            txtStationId.setText("060-29.0");
            txtLatitude.setText(lat);
            txtLongitude.setText(lon);
            txtClassification.setText("RR");
            txtArea.setVisibility(View.INVISIBLE); // Value is null so maybe hide it
            txtFieldNotes.setText("Here is some sample text");
            txtTown.setText("GUILFORD");

            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();

            txtAddress.setText(address);
            txtCity.setText(city);
            txtState.setText(state);
        }
    }


}
