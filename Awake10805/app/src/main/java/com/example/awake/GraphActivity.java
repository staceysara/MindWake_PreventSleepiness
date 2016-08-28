package com.example.awake;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class GraphActivity extends AppCompatActivity {
private static final String TAG = "GraphActivity";

    FirstFragment firstFragment;
    SecondFragment secondFragment;
   FragmentTransaction ft;
    Fragment fragment;
    FragmentManager manager;
String id = "";
    String isPhoneAlarm="";
    String isOtherPerson="";
    String isSmartWatch="";
    String phoneNum="";

    Button bt_firstFragment;
    Button bt_secondFragment;
    View.OnClickListener handler = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.bt_firstFragment:
                    bt_firstFragment.setBackgroundColor(Color.parseColor("#303F9F"));
                    bt_secondFragment.setBackgroundColor(Color.parseColor("#C5CAE9"));
                    doAction1();
                    break;
                case R.id.bt_secondFragment:
                    bt_firstFragment.setBackgroundColor(Color.parseColor("#C5CAE9"));
                    bt_secondFragment.setBackgroundColor(Color.parseColor("#303F9F"));
                    doAction2();
                    break;
            }
        }
    };
    void doAction1(){
        fragment = manager.findFragmentByTag("first");
        if(fragment ==null){
            Bundle bundle = new Bundle();
            firstFragment = new FirstFragment();
            firstFragment.setArguments(bundle);
            ft = manager.beginTransaction();
            ft.replace(R.id.main,firstFragment,"first");
            ft.commit();
        }
    }
    void doAction2(){
        fragment = manager.findFragmentByTag("second");
        if(fragment==null){
            Bundle bundle = new Bundle();
            secondFragment = new SecondFragment();
            bundle.putString("id",id);
            Log.v(TAG,"doAction2에서의 id:"+id);
            secondFragment.setArguments(bundle);
            ft = manager.beginTransaction();
            ft.replace(R.id.main,secondFragment,"second");
            ft.commit();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Intent intent1 = getIntent();
        id = intent1.getExtras().getString("id");
        Log.v(TAG,"onCreate에서의 id:"+id);
        isPhoneAlarm=intent1.getExtras().getString("isPhoneAlarm");//true나 false가 string으로 반환
        isOtherPerson=intent1.getExtras().getString("isOtherPerson");
         isSmartWatch=intent1.getExtras().getString("isSmartWatch");
        phoneNum = intent1.getExtras().getString("otherPhoneNo");
        Log.v(TAG,"동승자번호:"+ phoneNum);
       findViewById(R.id.bt_firstFragment).setOnClickListener(handler);
       findViewById(R.id.bt_secondFragment).setOnClickListener(handler);
        bt_firstFragment = (Button)findViewById(R.id.bt_firstFragment);
        bt_secondFragment = (Button)findViewById(R.id.bt_secondFragment);
        manager = getSupportFragmentManager();
        if(savedInstanceState==null){
            Bundle bundle = new Bundle();
            firstFragment = new FirstFragment();
            bundle.putString("isPhoneAlarm",isPhoneAlarm);
            bundle.putString("isOtherPerson",isOtherPerson);
            bundle.putString("isSmartWatch",isSmartWatch);
            bundle.putString("otherPhoneNo", phoneNum);
            firstFragment.setArguments(bundle);

            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.main,firstFragment);
            ft.commit();
        }
    }
}
