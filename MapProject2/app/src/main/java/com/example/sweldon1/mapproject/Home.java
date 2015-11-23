package com.example.sweldon1.mapproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Home extends ActionBarActivity {


    private TextView location;
    private TextView body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_layout);


        location = (TextView) findViewById(R.id.location);
        body = (TextView) findViewById(R.id.body);

        body.setText("Select an option to display data!\n");


        final Button button = (Button) findViewById(R.id.syncmap);


    }

    public void connectDB(View view)
    {

        Intent intent = new Intent(Home.this, DatabaseActivity.class);
        Home.this.startActivity(intent);

    }


    public void findme(View view) {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                String result = "";

                try {
                    String[] test = {"http://data.fcc.gov/api/block/2010/find?latitude=" + lat + "&longitude=" + lng, "http://alerts.weather.gov/cap/us.php?x=1"};
                    new WebRetrieval().execute(test);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("ERORRRRR");
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


    }

    //Starting new activity
    public void plotme(View view) {
        Intent intent = new Intent(Home.this, MapsActivity.class);
        Home.this.startActivity(intent);
    }

    private class WebRetrieval extends AsyncTask<String, Void, ArrayList> {

        private Exception exception;

        ArrayList<String> output = new ArrayList<>();
        String result = "";
        String alertResult = "";

        @Override
        protected ArrayList doInBackground(String... inputURL) {
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
                output.add(result);

                URL alerts = new URL(inputURL[1]);
                URLConnection yd = alerts.openConnection();
                BufferedReader io = new BufferedReader(new InputStreamReader(
                        yd.getInputStream()));
                String alertLine;
                while ((alertLine = io.readLine()) != null)
                    //System.out.println(inputLine);
                    alertResult += alertLine;
                io.close();
                output.add(alertResult);




            } catch (Exception e) {
                this.exception = e;
                return null;
            }

            return output;

        }

        @Override
        protected void onPostExecute(ArrayList output) {

            String userLocation = output.get(0).toString();

            String alertString = output.get(1).toString();


            org.jsoup.nodes.Document infoXML = Jsoup.parse(userLocation, "", Parser.xmlParser());
            org.jsoup.nodes.Document alertXML = Jsoup.parse(alertString, "", Parser.xmlParser());
            location.setText("General Location: "+infoXML.select("County").attr("name") + ", " + infoXML.select("State").attr("name"));

            Elements elements = alertXML.select("id");
            body.setText("");
            for (Element element : elements) {

                body.append(element.text()+"\n");


            }


        }


    }
}

