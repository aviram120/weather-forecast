package com.example.aviram.ex3;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private final String APIKEY="&APPID=72c7c4a5fcb6e9bc88f5a8e57f372d37";
    private final String SERVER="http://api.openweathermap.org/data/2.5/forecast";
    private Spinner spinner_location;
    private RequestQueue queue;
    private final Double KELVIN_COUNVERTION=273.15;
    private Activity activity;
    private ListView listView;
    private static String[] dateArr;
    private static String[] timeArr;
    private static String[] descArr;
    private static String[] tempArr;
    private static String[] picArr;
    private static String[] cityArr;
    private ProgressDialog dialog;
    private ProgressDialog dialogLocation;
    private LocationManager locationManager;
    private PermissionManager permissionManager;
    private LocationListener mLocationListener;
    private final long SECOND = 1000;
    private final long MIN_DISTANCE = 5;
    private Location mLocation;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity=this;//save the activity

        //init location
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationListener = this;

        initialization();
    }
    private void getWeatherFromServer(String city, final int type){
     //the function get data(weather) and put is on screan to show

        String url="";
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Date...");
        dialog.show();


        if (type==1)//choose city from the spinner
            url=SERVER+"/city?q="+city+APIKEY;
        else//type==2-get location from GPS
            url=SERVER+"?lat="+mLocation.getLatitude()+"&lon="+mLocation.getLongitude()+APIKEY;

        queue = Volley.newRequestQueue(this);

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>(){
                    public void onResponse(JSONObject response) {//callback-get the data from server
                        try {
                            dialog.hide();//stop thr dialog

                            JSONObject array = response.getJSONObject("city");//city from server

                            if (type==2)
                            {
                                Toast.makeText(getApplicationContext(), "your location is: "+array.getString("name"), Toast.LENGTH_SHORT).show();
                            }
                            Log.i("aviramLog", "cityname:" + array.getString("name"));

                            JSONArray list=response.getJSONArray("list");

                            //init array
                            int lenOfList=list.length();
                            dateArr=new String[lenOfList];
                            timeArr=new String[lenOfList];
                            descArr=new String[lenOfList];
                            tempArr=new String[lenOfList];
                            picArr=new String[lenOfList];

                            for (int i=0; i<list.length(); i++)
                            {
                                //time
                                String time=list.getJSONObject(i).getString("dt_txt");

                                //temperature
                                String temp=list.getJSONObject(i).getJSONObject("main").getString("temp");
                                //convert the temperature
                                double totalTemp = Double.parseDouble(temp);
                                totalTemp=totalTemp-KELVIN_COUNVERTION;
                                DecimalFormat df = new DecimalFormat("#.#");
                                String dx=df.format(totalTemp)+"c";

                                //description
                                String desc=list.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description");

                                //icon
                                String pic=list.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon");

                                String[] splited = time.split("\\s+");

                                dateArr[i]=splited[0];//date
                                timeArr[i]=splited[1];//time
                                descArr[i]=desc;
                                tempArr[i]=dx;
                                picArr[i]=pic;

                            }
                            //make the listView-custom
                            listView.setAdapter(new CustomAdapter(activity, dateArr,timeArr,descArr,tempArr,picArr));

                        } catch (JSONException e) {
                            return;
                        }
                    }
                },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                dialog.hide();
                Log.i("aviramLog",""+error);
                Toast.makeText(getApplicationContext(), "ERROR: can't load", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);//add to queue
    }
    public void addItemsOnSpinner(Spinner spinner_id,List<String> list){
    //the function add list to spinner

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_id.setAdapter(dataAdapter);
    }
    private void initialization(){
    //the function initialization all the Variables of the app

        listView=(ListView) findViewById(R.id.listView);

        dialogLocation = new ProgressDialog(this);
        dialogLocation.setMessage("Getting Location...");

        spinner_location = (Spinner) findViewById(R.id.locationSpinner);
        List < String > list_city = new ArrayList<String>();

        //set city for spinner
        list_city.add("Current Location");
        list_city.add("Tel Aviv");
        list_city.add("Jerusalem");
        list_city.add("New York");
        list_city.add("Las Vegas");
        list_city.add("Los Angeles");
        list_city.add("Hawaii");
        list_city.add("Berlin");
        list_city.add("Hong Kong");
        list_city.add("Beijing");

        cityArr=new String[10];
        cityArr[0]="my location";
        cityArr[1]="telaviv";
        cityArr[2]="jerusalem";
        cityArr[3]="newyork";
        cityArr[4]="lasvegas";
        cityArr[5]="losangeles";
        cityArr[6]="hawaii";
        cityArr[7]="berlin";
        cityArr[8]="hongkong";
        cityArr[9]="Beijing";

        addItemsOnSpinner(spinner_location, list_city);//add array to spinner

        //get the position in the spinner
        spinner_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.i("aviramLog", "city " + cityArr[position]);
                if (position==0)
                {
                    getLocationFromSystem();//get the location of the user from GPS/wifi
                }
                else
                    getWeatherFromServer(cityArr[position],1);//get the weather from server
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }
    private void getLocationFromSystem() {
    // the function get the location from GPS and send it to server

        boolean isGPSAvailable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isWIFIAvailable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.i("aviramLog", "in my location gps"+isGPSAvailable);
        Log.i("aviramLog", "in my location wifi"+isWIFIAvailable);
        if (isGPSAvailable) {
            //get run time permission
            permissionManager = new PermissionManager(activity, new PermissionManager.OnPermissionListener() {
                public void OnPermissionChanged(boolean permissionGranted) {
                    Log.d("aviramLog", "permissionGranted: " + permissionGranted);
                    if (permissionGranted) {
                        try{//get location -updates
                            dialogLocation.show();
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, SECOND * 10, MIN_DISTANCE, mLocationListener);
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, SECOND * 10, MIN_DISTANCE, mLocationListener);

                        } catch (SecurityException e) {   }
                    }
                }
            });
        }
        else//if the location not available
        {
            showSettingsAlert();
        }

    }
    private void showSettingsAlert() {
    //the function show a alert dialog-of setting
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    public void onLocationChanged(Location location) {
        //the function get the location and send the data to server to get the weather forecast

        mLocation = location;
        dialogLocation.hide();
        Log.i("aviramLog", "my Location " + location.toString());
        getWeatherFromServer("", 2);//get the weather from server
    }
    protected void onStop() {
        super.onStop();

        try{
            locationManager.removeUpdates(this);

        } catch (SecurityException e) {

        }
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    //provider status changed
    }
    public void onProviderEnabled(String provider) {
    //provider enabled by user
    }
    public void onProviderDisabled(String provider) {
    //provider disabled by user
    }
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }
    public void onResume() {
        super.onResume();
    }
}//end
