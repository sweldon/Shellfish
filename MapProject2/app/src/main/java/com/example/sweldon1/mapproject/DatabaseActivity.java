package com.example.sweldon1.mapproject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DatabaseActivity extends ListActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> zonesList;

    // url to get all zones list
    private static String url_zones = "http://192.168.56.1/shellfish/get_zones.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ZONES = "zones";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_STATUS = "status";

    // zones JSONArray
    JSONArray zones = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        // Hashmap for ListView
        zonesList = new ArrayList<HashMap<String, String>>();

        // Loading zones in Background Thread
        new LoadAllZones().execute();

        // Get listview
        ListView lv = getListView();

        // on seleting single zone
        // launching Edit Zone Screen
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.id)).getText()
                        .toString();
                System.out.println(pid);
//                // Starting new intent
//                Intent in = new Intent(getApplicationContext(),
//                        EditZoneActivity.class);
//                // sending pid to next activity
//                in.putExtra(TAG_ID, pid);
//
//                // starting new activity and expecting some response back
//                startActivityForResult(in, 100);
            }
        });

    }

    // Response from Edit Zone Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted zone
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

    /**
     * Background Async Task to Load all zone by making HTTP Request
     * */
    class LoadAllZones extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DatabaseActivity.this);
            pDialog.setMessage("Loading zones. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * getting All zones from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_zones, "GET", params);

//            try {
//                json = new JSONObject("{\"zones\":[{\"id\":\"1\",\"name\":\"test_area_1\",\"status\":\"OPEN\"},{\"id\":\"2\",\"name\":\"test_area_2\",\"status\":\"CLOSED\"}],\"success\":1}");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

            System.out.println(json);

            // Check your log cat for JSON reponse
            Log.d("All Zones: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // zones found
                    // Getting Array of Zones
                    zones = json.getJSONArray(TAG_ZONES);

                    // looping through All Zones
                    for (int i = 0; i < zones.length(); i++) {
                        JSONObject c = zones.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_NAME);
                        String status = c.getString(TAG_STATUS);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_ID, id);
                        map.put(TAG_NAME, name);
                        map.put(TAG_STATUS, status);

                        // adding HashList to ArrayList
                        zonesList.add(map);
                    }
                } else {
                    // no zones found
                    // Launch Add New zone Activity
                    System.out.println("No zones found");
//                    Intent i = new Intent(getApplicationContext(),
//                            NewZoneActivity.class);
//                    // Closing all previous activities
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all zones
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            DatabaseActivity.this, zonesList,
                            R.layout.activity_database, new String[] { TAG_ID,
                            TAG_NAME},
                            new int[] { R.id.id, R.id.name });
                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }

    }
}