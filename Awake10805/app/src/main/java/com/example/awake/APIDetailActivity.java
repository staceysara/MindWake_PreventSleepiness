package com.example.awake;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class APIDetailActivity extends AppCompatActivity {
    String lat;
    String lon;
    String datentime;
    String weather;
    String temperature;
    String fullName;
    int airmise;
    int airchomise;
    int airno;
    int airso2;
    int airo3;
    int airco;


    TextView tv_datentime;
    TextView tv_temperature;
    TextView tv_fullName;
    TextView tv_airmise;
    TextView tv_airchomise;
    TextView tv_airno;
    TextView tv_airso2;
    TextView tv_airo3;
    TextView tv_airco;
    ImageView iv_weather;
    GoogleMap map;
    Location location;
    double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apidetail);
        init();
    }
    void init(){
        lat = getIntent().getStringExtra("lat");
        lon = getIntent().getStringExtra("lon");
        datentime=getIntent().getStringExtra("datentime");
        weather = getIntent().getStringExtra("weather");
        temperature=getIntent().getStringExtra("temperature");
        fullName = getIntent().getStringExtra("fullName");
        airmise=getIntent().getIntExtra("airmise", 1);
        airchomise=getIntent().getIntExtra("airchomise", 1);
        airno = getIntent().getIntExtra("airno",1);
        airso2=getIntent().getIntExtra("airso2",1);
        airo3=getIntent().getIntExtra("airo3",1);
        airco=getIntent().getIntExtra("airco",1);

       tv_datentime=(TextView)findViewById(R.id.tv_datentime1);
        tv_temperature=(TextView)findViewById(R.id.tv_temperature1);
         tv_fullName=(TextView)findViewById(R.id.tv_fullName1);
        tv_airmise=(TextView)findViewById(R.id.tv_airmise1);
        tv_airchomise=(TextView)findViewById(R.id.tv_airchomise1);
        tv_airno=(TextView)findViewById(R.id.tv_airno1);
         tv_airso2=(TextView)findViewById(R.id.tv_airso21);
       tv_airo3=(TextView)findViewById(R.id.tv_airo31);
        tv_airco=(TextView)findViewById(R.id.tv_airco1);
        iv_weather=(ImageView)findViewById(R.id.iv_weather1);

        tv_datentime.setTextColor(Color.BLACK);
        tv_temperature.setTextColor(Color.BLACK);
        tv_fullName.setTextColor(Color.BLACK);
        tv_airmise.setTextColor(Color.BLACK);
        tv_airchomise.setTextColor(Color.BLACK);
        tv_airno.setTextColor(Color.BLACK);
        tv_airso2.setTextColor(Color.BLACK);
        tv_airo3.setTextColor(Color.BLACK);
        tv_airco.setTextColor(Color.BLACK);

        tv_datentime.setText(datentime);
        tv_temperature.setText(temperature);
        tv_fullName.setText(fullName);

        tv_airmise.setText(aqiToKorean(airmise));
        tv_airchomise.setText(aqiToKorean(airchomise));
        tv_airno.setText(aqiToKorean(airno));
        tv_airso2.setText(aqiToKorean(airso2));
        tv_airo3.setText(aqiToKorean(airo3));
        tv_airco.setText(aqiToKorean(airco));
       /* tv_airmise.setText(String.valueOf(airmise));
        tv_airchomise.setText(String.valueOf(airchomise));
        tv_airno.setText(String.valueOf(airno));
        tv_airso2.setText(String.valueOf(airso2));
        tv_airo3.setText(String.valueOf(airo3));
        tv_airco.setText(String.valueOf(airco));*/

        if(weather.equals("01d")||weather.equals("01n")){
            iv_weather.setImageResource(R.drawable.d01d);
        }else if(weather.equals("02d")||weather.equals("02n")){
            iv_weather.setImageResource(R.drawable.d02d);
        }
        else if(weather.equals("03d")||weather.equals("03n")){
            iv_weather.setImageResource(R.drawable.d03d);
        }else if(weather.equals("04d")||weather.equals("04n")){
            iv_weather.setImageResource(R.drawable.d04d);
        }else if(weather.equals("09d")||weather.equals("09n")){
            iv_weather.setImageResource(R.drawable.d09d);
        }else if(weather.equals("10d")||weather.equals("10n")){
            iv_weather.setImageResource(R.drawable.d10d);
        }else if(weather.equals("11d")||weather.equals("11n")){
            iv_weather.setImageResource(R.drawable.d11d);
        }else if(weather.equals("13d")||weather.equals("13n")){
            iv_weather.setImageResource(R.drawable.d13d);
        }else if(weather.equals("50d")||weather.equals("50n")){
            iv_weather.setImageResource(R.drawable.d50d);
        }
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
      //  location=lm.getLastKnownLocation(locationProvider);
        latitude = Double.parseDouble(lat);
        longitude = Double.parseDouble(lon);
        set(latitude,longitude);

    }   void set(double paramlatitude,double paramlongitude){

        final LatLng Loc= new LatLng(paramlatitude, paramlongitude);
        MarkerOptions options = new MarkerOptions();
        if(location!=null){
            options.position(Loc);
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            options.title("졸음발생지역");
            map.addMarker(options);
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(Loc, 16));
    }
    String aqiToKorean(int aqi){
        String aqiStr="";
        if(aqi<=50){//좋음
            return "좋음";
        }else if(aqi>50 && aqi<=100){//보통
            return "보통";
        }else if(aqi>100 && aqi<=150){//민감군영향
            return "민감군영향";
        }else if(aqi>150 && aqi<=200){//나쁨
            return "나쁨";
        }else if(aqi>200 && aqi<=300){//매우나쁨
            return "매우나쁨";
        }else{//위험
            return "위험";
        }
    }
}
