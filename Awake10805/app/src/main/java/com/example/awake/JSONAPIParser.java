package com.example.awake;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 지연 on 2016-08-01.
 */
public class JSONAPIParser {
    public static ArrayList<APIItem> parser(String apiJsonStr){
        ArrayList<APIItem> data = new ArrayList<APIItem>();
        JSONArray arr = null;
        JSONObject json = null;
        APIItem item = null;
        JSONObject temp = null;
        try{
            json = new JSONObject(apiJsonStr);
            arr = json.getJSONArray("api");
            for(int i=0;i<arr.length();i++){
                item = new APIItem();
                temp = arr.getJSONObject(i);
                item.setLat(temp.getString("lat"));
                item.setLon(temp.getString("lon"));
                item.setDatentime(temp.getString("datentime"));
                item.setLocation(temp.getString("location"));
                String tempweather = temp.getString("weather");
                int tempweatherId;
                if(tempweather.equals("Haze")){

                }if(tempweather.equals("Clouds"))
                {

                }
                item.setWeather(temp.getString("weather"));//임의로 넣어놓은거
                item.setTemperature(temp.getString("temperature"));
                item.setCity(temp.getString("city"));
                item.setFullName(temp.getString("fullName"));
                item.setAirmise(temp.getInt("airmise"));
                item.setAirchomise(temp.getInt("airchomise"));
                item.setAirno(temp.getInt("airno2"));
                item.setAirso2(temp.getInt("airso2"));
                item.setAiro3(temp.getInt("airo3"));
                item.setAirco(temp.getInt("airco"));
                data.add(item);
            }
        }catch(Exception e){

        }
        return data;
    }
}
