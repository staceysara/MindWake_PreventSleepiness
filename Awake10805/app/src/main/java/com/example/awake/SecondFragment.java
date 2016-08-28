package com.example.awake;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class SecondFragment extends Fragment implements View.OnClickListener {
    private static final String TAG ="SecondFragment";
Context context;
    Button bt_getAPI;
    ImageView iv_frequentWeather;
    TextView tv_frequentTemp;

//    TextView tv_apiTextView;
private GpsInfo gps;
    double latitude = 0;
    double longitude=0;
    ProgressDialog dialog;
    String id="";
    String firstOrNot="first";
    ArrayList<APIItem> arrayListData =  new ArrayList<APIItem>();
APIAdapter adapter = null;
    ListView lv_view;
    public SecondFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_second, container, false);
        Bundle extra = getArguments();
        id = extra.getString("id");
        Log.v(TAG,"id:"+id);
        bt_getAPI=(Button)view.findViewById(R.id.bt_getAPI);
        lv_view = (ListView)view.findViewById(R.id.listView);
        iv_frequentWeather=(ImageView)view.findViewById(R.id.iv_frequentWeather);
        tv_frequentTemp=(TextView)view.findViewById(R.id.tv_frequentTemp);

        adapter = new APIAdapter(getActivity().getApplicationContext(),arrayListData,R.layout.item);
        lv_view.setAdapter(adapter);
        bt_getAPI.setOnClickListener(this);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
       context = null;
    }


    @Override
    public void onClick(View v) {
    switch(v.getId()){

        case R.id.bt_getAPI:
            doAction1();

            break;
    }
    }
    public void doAction2(){
/*String temp = adapter.apidata.get(0).city;*/
        int temp = adapter.getCount();
        int temp1 = adapter.getMaxWeather();
        Log.v(TAG,Integer.toString(temp));
        Log.v(TAG,Integer.toString(temp1));
       String getMaxTemp =  adapter.getMaxTemp();

        tv_frequentTemp.setText(getMaxTemp);
        int getMaxWeatherIndex = adapter.getMaxWeather();
        switch(getMaxWeatherIndex){
            case 0:
                iv_frequentWeather.setImageResource(R.drawable.d01d);
                break;
            case 1:
                iv_frequentWeather.setImageResource(R.drawable.d02d);
                break;
            case 2:
                iv_frequentWeather.setImageResource(R.drawable.d03d);
                break;
            case 3:
                iv_frequentWeather.setImageResource(R.drawable.d04d);
                break;
            case 4:
                iv_frequentWeather.setImageResource(R.drawable.d09d);
                break;
            case 5:
                iv_frequentWeather.setImageResource(R.drawable.d10d);
                break;
            case 6:
                iv_frequentWeather.setImageResource(R.drawable.d11d);
                break;
            case 7:
                iv_frequentWeather.setImageResource(R.drawable.d13d);
                break;
            case 8:
                iv_frequentWeather.setImageResource(R.drawable.d50d);
                break;
        }
    }
public void doAction1(){
    gps = new GpsInfo(getActivity());
    //GPS사용유무 가져오기

    if(gps.isGetLocation()){
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();

        Toast.makeText(getActivity().getApplicationContext(), "당신의 위치=\n 위도:" + latitude + "\n경도:" + longitude, Toast.LENGTH_LONG).show();
    }else{
        //GOS를 사용할 수 없으므로
        gps.showSettingAlert();
    }
    if(latitude==0||longitude==0){
        Toast.makeText(getActivity().getApplicationContext(),"latitude가 0이거나 longitude가 0. 잘못된 위경도값",Toast.LENGTH_SHORT).show();
    }else {
        dialog = ProgressDialog.show(getActivity(), "", "위경도값 전송 중 입니다");
        new GpsThread().start();
    }
}
String data="";
    class GpsThread extends Thread{
        public void run(){
            String url = ServerUtil.SERVER_URL+"api";

            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("action","sleepy"));
            nameValue.add(new BasicNameValuePair("lat", String.valueOf(latitude)));
            nameValue.add(new BasicNameValuePair("lon", String.valueOf(longitude)));
            nameValue.add(new BasicNameValuePair("id", String.valueOf(id)));
            nameValue.add(new BasicNameValuePair("firstOrNot", String.valueOf(firstOrNot)));
           // firstOrNot="notFirst";

            HttpClient client = NetManager1.getHttpClient();
            HttpPost post = NetManager1.getPost(url);

            HttpResponse response = null;
            BufferedReader br = null;
            StringBuffer sb = null;

            String line = "";
            try{
                Log.v(TAG,"inGPSThread");
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValue);
                post.setEntity(entity);
                response=client.execute(post);
                int code = response.getStatusLine().getStatusCode();
                Log.v(TAG,"process code:"+code);
                switch(code){
                    case 200:
                        br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                        sb = new StringBuffer();
                        while((line=br.readLine())!=null){
                            sb.append(line);
                        }
                        Log.v(TAG, "register process msg : " + sb);
                        data = sb.toString();
                        gpsHandler.sendEmptyMessage(0);
                        break;
                    default:
                        gpsHandler.sendEmptyMessage(1);
                }


            }catch(Exception e){
                Log.v(TAG,"register paraser error :" +e);
                gpsHandler.sendEmptyMessage(2);
            }finally{
                try{
                    br.close();
                }catch(Exception e){}
            }

        }
        Handler gpsHandler = new Handler(){
            public void handleMessage(Message msg){
                dialog.dismiss();
                switch(msg.what){
                    case 0:
                        if(doAPICheck()){
                            showToast("GPS등록에 성공했고 api값 받아왔습니다!!");
                            doAction2();
                        }else{
                            showToast("GPS등록에 실패했거나 api값 받아오는데 실패했습니다(case 0). 다시 시도하세요");
                        }
                        break;
                    case 1:
                        showToast("GPS등록에 실패했거나 api값 받아오는데 실패했습니다(case 1). 다시 시도하세요");
                        break;
                    case 2:
                        showToast("GPS등록에 실패했거나 api값 받아오는데 실패했습니다(case 2). 다시 시도하세요");
                        break;
                }
            }
        };
        void showToast(String text){
            Toast.makeText(getActivity().getApplicationContext(),text,Toast.LENGTH_SHORT).show();

        }
        boolean doAPICheck(){

            if(!data.contains("fail")&&data.length()>0){
               // tv_apiTextView.append(data);
                String tempdata ="{\"api\":["+data+"]}";
                Log.v(TAG,"tempdata:"+tempdata);
                arrayListData =JSONAPIParser.parser(tempdata);
if(  firstOrNot=="first") {
    adapter.setData(arrayListData);
    firstOrNot="notFirst";
}else{//firstOrNot=="notFirst"일 때
adapter.setDatafromUp(arrayListData);
}
                adapter.notifyDataSetChanged();
                return true;
            }
            else if(data.indexOf("fail")>-1){
                return false;

            }else{
                return false;
            }
        }
        boolean doCheck(){
            if(data.indexOf("fail")>-1){
                return false;

            }else{
                return true;
            }
        }
    }
}
