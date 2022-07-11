package com.example.location;

import static com.example.location.R.*;
import static com.example.location.R.drawable.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    String cityName = "Hanoi";
    SearchView searchView;
    TextView txtTemp, txtCity, txtDesc,txtDate;
    public ImageView imgWeather;
    String url;
    double lat,lon;
    int id;
    View v;
    long time;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        txtTemp = findViewById(R.id.txtTemp);
        txtCity = findViewById(R.id.txtCity);
        txtDesc = findViewById(R.id.txtDesc);
        txtDate = findViewById(R.id.txtTime);
        DateFormat df = new SimpleDateFormat("EEEE, d/M/yyyy");
        String date = df.format(Calendar.getInstance().getTime());
//        DateFormat dh = new SimpleDateFormat("h");
//        int hour = Integer.valueOf(dh.format(Calendar.getInstance().getTime()));
//        if(hour>12){
//            v.setBackgroundResource(R.drawable.darklight);
//        } else {
//            v.setBackgroundResource(drawable.img);
//        }
        txtDate.setText(""+date);
        imgWeather = findViewById(R.id.imgWeather);
        getJsonWeather(cityName);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        @SuppressLint("MissingPermission")
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                Log.d("AAA","dong85: "+lat+lon);
                                client.getLastLocation()
                                        .addOnSuccessListener(this, location -> {
                                            if (location != null) {
                                                lat = location.getLatitude();
                                                lon = location.getLongitude();
                                                Log.d("AAA","dong92"+lat+lon);
                                            }
                                        });
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                client.getLastLocation()
                                        .addOnSuccessListener(this, location -> {
                                            if (location != null) {
                                                lat = location.getLatitude();
                                                lon = location.getLongitude();
                                                Log.d("AAA","dong101"+lat+lon);
                                            }
                                        });
                            } else {
                                Toast.makeText(MainActivity.this,"Từ chối truy cập vị trí",Toast.LENGTH_SHORT).show();
                            }
                        }
                );
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }
    public void getJsonWeather(String city){
        url = "https://api.openweathermap.org/data/2.5/weather?&appid=3afe510e277278ac5135bef1b5cd38ee&units=metric&lang=vi&q="+city;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject desc = array.getJSONObject(0);
                    String descc = desc.getString("description");
                    id = desc.getInt("id");
                    JSONObject main = response.getJSONObject("main");
                    String temp = main.getString("temp");
                    txtTemp.setText(temp+"°C");
                    txtCity.setText(response.getString("name"));
                    txtDesc.setText(descc);
                    setImageWeather(id);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"Lỗi truy vấn "+e.toString(),Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"Lỗi truy vấn "+error.toString(),Toast.LENGTH_LONG).show();
                    }
                }
        );
        requestQueue.add(objectRequest);
    }
    public void setImageWeather(int conditionID){
            if(conditionID>200 && conditionID<232){
                imgWeather.setBackgroundResource(R.drawable.cloud_storm);
            } else if (conditionID>300 && conditionID<321){
                imgWeather.setBackgroundResource(R.drawable.cloud_rain);
            } else if(conditionID>500 && conditionID<531) {
                imgWeather.setBackgroundResource(R.drawable.cloud_heavy_rain);
            } else if(conditionID>600 && conditionID<622){
                imgWeather.setBackgroundResource(R.drawable.cloud_snow);
            } else if(conditionID>701 && conditionID<781){
                imgWeather.setBackgroundResource(R.drawable.cloud_fog);
            }else if(conditionID==800){
                imgWeather.setBackgroundResource(R.drawable.sun_max);
            }else if(conditionID>801 && conditionID<804){
                imgWeather.setBackgroundResource(cloud_storm);
            } else {
                imgWeather.setBackgroundResource(R.drawable.cloud);
            }
    }
    public void getJsonWeather(double lat,double lon){
        url = "https://api.openweathermap.org/data/2.5/weather?&appid=3afe510e277278ac5135bef1b5cd38ee&units=metric&lang=vi&lat=" + lat + "&lon=" + lon;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject desc = array.getJSONObject(0);
                    String descc = desc.getString("description");
                    JSONObject main = response.getJSONObject("main");
                    String temp = main.getString("temp");
                    time = response.getLong("dt");
                    id = desc.getInt("id");
                    txtTemp.setText(temp + "°C");
                    txtCity.setText(response.getString("name"));
                    txtDesc.setText(descc);
                    cityName = response.getString("name");
                    setImageWeather(id);
                    Toast.makeText(MainActivity.this,"Vị trí hiện tại: "+cityName,Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"Lỗi truy vấn "+e.toString(),Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"Lỗi truy vấn "+error.toString(),Toast.LENGTH_LONG).show();

                    }
                }
        );
        requestQueue.add(objectRequest);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String txt) {
                return false;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                String text = searchView.getQuery().toString();
                cityName = text;
                getJsonWeather(cityName);
                return false;
            }

        });
        return super.onCreateOptionsMenu(menu);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuLocation:
                getJsonWeather(lat,lon);
                break;
            case R.id.menuSearch:
                searchView = (SearchView) item.getActionView();
                searchView.setIconifiedByDefault(true);
                searchView.setFocusable(true);
                searchView.requestFocusFromTouch();
                searchView.setQueryHint("Nhập tên thành phố");
                searchView.setMaxWidth(android.R.attr.width);
                searchView.setBackgroundResource(shapebgsearch);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}