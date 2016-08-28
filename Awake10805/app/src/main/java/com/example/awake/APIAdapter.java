package com.example.awake;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 지연 on 2016-08-01.
 */
public class APIAdapter extends BaseAdapter {
    public static final String TAG = "APIAdapter";
    Context context;
    int layout;
    ArrayList<APIItem> apidata;
    int weatherArr[] = new int[9];//01,02,03,04,09,10,11,13,50
    int tempArr[] = new int[10];
    public APIAdapter(Context context, ArrayList<APIItem> apidata, int layout){
        this.context = context;
        this.apidata = apidata;
        this.layout = layout;
    }
    String getMaxTemp(){
        String tempTemp="";
        double tempTemp1=0;
        int maxTemp=0;
        int maxTempIndex=0;
        String returnStr="";
        for(int i=0;i<apidata.size();i++) {
            tempTemp = apidata.get(i).temperature;//temperature는 string형태임
            tempTemp1 = Double.parseDouble(tempTemp);
            if (tempTemp1 < 15) {
              tempArr[0]++;
            } else if (tempTemp1 <= 15 && tempTemp1 < 18) {
                tempArr[1]++;
            } else if (tempTemp1 <= 18 && tempTemp1 < 21) {
                tempArr[2]++;
            } else if (tempTemp1 <= 21 && tempTemp1 < 24) {
                tempArr[3]++;
            } else if (tempTemp1 <= 24 && tempTemp1 < 27) {
                tempArr[4]++;
            } else if (tempTemp1 <= 27 && tempTemp1 < 30) {
                tempArr[5]++;
            }else if(tempTemp1 <=30  && tempTemp1<33){
                tempArr[6]++;
            }else if(tempTemp1 <=33  && tempTemp1<36){
                tempArr[7]++;
            }else if(tempTemp1 <=36  && tempTemp1<39){
                tempArr[8]++;
            }else{
                tempArr[9]++;
            }
        }//for
        for(int i=0;i<weatherArr.length;i++){
            if(weatherArr[i]>maxTemp){
                maxTemp = weatherArr[i];
                maxTempIndex=i;
            }
        }
        if(maxTempIndex==0){
            returnStr="0℃~15℃ 사이";
        }else if(maxTempIndex==1){
            returnStr ="15℃이상 18℃ 미만";
        }else if(maxTempIndex==2){
            returnStr = "18℃이상 21℃ 미만";
        }else if(maxTempIndex==3){
            returnStr = "21℃이상 24℃ 미만";
        }else if(maxTempIndex==4){
            returnStr = "24℃이상 27℃ 미만";
        }else if(maxTempIndex==5){
            returnStr = "27℃이상 30℃ 미만";
        }else if(maxTempIndex==6){
            returnStr = "30℃이상 33℃ 미만";
        }else if(maxTempIndex==7){
            returnStr = "33℃ 이상 36℃ 미만";
        }else if(maxTempIndex==8){
            returnStr = "36℃이상 39℃ 미만";
        }else if(maxTempIndex==9){
            returnStr = "39℃ 이상";
        }
        Log.v(TAG, returnStr);
        return returnStr;
    }
    int getMaxWeather(){
        String tempWeath="";
        int max=0, maxIndex=0;
        for(int i=0;i<apidata.size();i++){
            tempWeath = apidata.get(i).weather;
            if(tempWeath.equals("01d")|| tempWeath.equals("01n")){
                weatherArr[0]++;
            }else if(tempWeath.equals("02d")|| tempWeath.equals("02n")){
                weatherArr[1]++;
            }else if(tempWeath.equals("03d")|| tempWeath.equals("03n")){
                weatherArr[2]++;
            }else if(tempWeath.equals("04d")|| tempWeath.equals("04n")){
                weatherArr[3]++;
            }else if(tempWeath.equals("09d")|| tempWeath.equals("09n")){
                weatherArr[4]++;
            }else if(tempWeath.equals("10d")|| tempWeath.equals("10n")){
                weatherArr[5]++;
            }else if(tempWeath.equals("11d")|| tempWeath.equals("11n")){
                weatherArr[6]++;
            }else if(tempWeath.equals("13d")|| tempWeath.equals("13n")){
                weatherArr[7]++;
            }else if(tempWeath.equals("50d")|| tempWeath.equals("50n")){
                weatherArr[8]++;
            }
        }//for

        for(int i=0;i<weatherArr.length;i++){
            if(weatherArr[i]>max){
                max = weatherArr[i];
                maxIndex=i;
            }
        }
        String maxIndexStr =Integer.toString(maxIndex);
        Log.v(TAG, maxIndexStr);
        return  maxIndex;
    }


    public void getFrequentTemp(){

    }
    public void addData(APIItem item){
        apidata.add(item);
    }
    @Override
    public int getCount() {
        return apidata.size();
    }

    @Override
    public Object getItem(int position) {
        return apidata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(ArrayList<APIItem> data){
        //this.apidata = data;
        for(int i=0;i<data.size();i++){
            APIItem tempapiitem = data.get(i);
            this.apidata.add(tempapiitem);

        }
    }   public void setDatafromUp(ArrayList<APIItem> data){
        //this.apidata = data;
        for(int i=0;i<data.size();i++){
            APIItem tempapiitem = data.get(i);
            this.apidata.add(0,tempapiitem);
        }
    }
    class ViewHolder{
        ImageView iv_weather;
        ImageView iv_arrow;
        TextView tv_datentime;
        TextView tv_fullName;
        TextView tv_temperature;
    }

    public View getView(int position,View convertView, ViewGroup parent){
        ViewHolder holder = null;
        if(convertView==null){
            convertView = View.inflate(context,layout,null);
            holder = new ViewHolder();
            holder.iv_weather = (ImageView)convertView.findViewById(R.id.iv_weather);
            holder.iv_arrow = (ImageView)convertView.findViewById(R.id.iv_arrow);
            holder.tv_temperature=(TextView)convertView.findViewById(R.id.tv_temperature);
            holder.tv_fullName = (TextView)convertView.findViewById(R.id.tv_fullName);
            holder.tv_datentime=(TextView)convertView.findViewById(R.id.tv_datentime);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        final APIItem apiitem = apidata.get(position);
        holder.iv_arrow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Toast.makeText(context, apiitem.getAirmise(), Toast.LENGTH_SHORT).show();//int니까 안될 것.
            Intent intent = new Intent(context,APIDetailActivity.class);
                intent.putExtra("lat",apiitem.getLat());
                intent.putExtra("lon",apiitem.getLon());
                intent.putExtra("datentime",apiitem.getDatentime());
                intent.putExtra("location",apiitem.getLocation());
                intent.putExtra("weather",apiitem.getWeather());
                intent.putExtra("temperature",apiitem.getTemperature());
                intent.putExtra("city",apiitem.getCity());
                intent.putExtra("fullName",apiitem.getFullName());
                int airmise = apiitem.getAirmise();
                int airchomise = apiitem.getAirchomise();
                int airno = apiitem.getAirno();
                int airso2 = apiitem.getAirso2();
                int airo3 = apiitem.getAiro3();
                int airco = apiitem.getAirco();
                intent.putExtra("airmise",airmise);
                intent.putExtra("airchomise",airchomise);
                intent.putExtra("airno",airno);
                intent.putExtra("airso2",airso2);
                intent.putExtra("airo3",airo3);
                intent.putExtra("airco",airco);
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        if(apiitem.weather.equals("01d")||apiitem.weather.equals("01n")){
            holder.iv_weather.setImageResource(R.drawable.d01d);
        }else if(apiitem.weather.equals("02d")||apiitem.weather.equals("02n")){
            holder.iv_weather.setImageResource(R.drawable.d02d);
        }
        else if(apiitem.weather.equals("03d")||apiitem.weather.equals("03n")){
            holder.iv_weather.setImageResource(R.drawable.d03d);
        }else if(apiitem.weather.equals("04d")||apiitem.weather.equals("04n")){
            holder.iv_weather.setImageResource(R.drawable.d04d);
        }else if(apiitem.weather.equals("09d")||apiitem.weather.equals("09n")){
            holder.iv_weather.setImageResource(R.drawable.d09d);
        }else if(apiitem.weather.equals("10d")||apiitem.weather.equals("10n")){
            holder.iv_weather.setImageResource(R.drawable.d10d);
        }else if(apiitem.weather.equals("11d")||apiitem.weather.equals("11n")){
            holder.iv_weather.setImageResource(R.drawable.d11d);
        }else if(apiitem.weather.equals("13d")||apiitem.weather.equals("13n")){
            holder.iv_weather.setImageResource(R.drawable.d13d);
        }else if(apiitem.weather.equals("50d")||apiitem.weather.equals("50n")){
            holder.iv_weather.setImageResource(R.drawable.d50d);
        }
    holder.tv_temperature.setTextColor(Color.BLACK);
        holder.tv_fullName.setTextColor(Color.BLACK);
        holder.tv_datentime.setTextColor(Color.BLACK);
     holder.tv_temperature.setText(apiitem.getTemperature());
       holder.tv_datentime.setText(apiitem.getDatentime());
       holder.tv_fullName.setText(apiitem.getFullName());
        return convertView;
    }
}
