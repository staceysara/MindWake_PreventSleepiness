package com.example.awake;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmSelect extends AppCompatActivity implements View.OnClickListener{
    BluetoothAdapter adapter = null;
    CheckBox chk_phone;
    CheckBox chk_otherPPL;
    CheckBox chk_watch;
    String phoneNum="";
    String isPhoneAlarm="false";//어떤 알람방식을 받을건지
    String isOtherPerson="false";
    String isSmartWatch="false";
    //    Button bt_toMusicAlarm;
//    Button bt_toWatchAlarm;
    Button bt_alarmConfirm;
//    Button bt_dialog1;

    String id;

    ImageView iv_alarm1;
    ImageView iv_alarm2;
    ImageView iv_alarm3;

    TextView textView9;
    EditText et_otherPhoneNo;

    private static final String TAG = "AlarmSelect";
    private static final int BT_ENABLED=1;
    private static final int BT_CONNECTED=2;



    View.OnClickListener handler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//                case R.id.bt_toMusicAlarm:
//                    toMusicAlarm();
//                    break;
//                case R.id.bt_toNotiAlarm:
//                    toWatchAlarm();
//                    break;
                case R.id.chk_otherPPL:
                    if(chk_otherPPL.isChecked()) {
                        textView9.setTextColor(Color.GRAY);
                        et_otherPhoneNo.setEnabled(true);
                        phoneNum = et_otherPhoneNo.getText().toString();
                        isOtherPerson="true";

                    } else {
                        textView9.setTextColor(Color.TRANSPARENT);
                        et_otherPhoneNo.setEnabled(false);
                    }

                    break;
                case R.id.chk_phone:
                        isPhoneAlarm="true";
                    break;
                case R.id.chk_watch:
                       isSmartWatch="true";
                    break;
                case R.id.bt_alarmConfirm:
                    doAction1();
                    break;
//                case R.id.bt_dialog1:
//
//
//                    Context mContext = getApplicationContext();
//                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
//
//                    View layout = inflater.inflate(R.layout.dialog1,(ViewGroup) findViewById(R.id.popup1));
//                    AlertDialog.Builder aDialog = new AlertDialog.Builder(v.getContext());
//
//                    aDialog.setTitle("알림받을 방법 선택");
//                    aDialog.setView(layout);
//
//                    aDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                        }
//                    });
//                    AlertDialog ad = aDialog.create();
//                    ad.show();
//                    break;
                case R.id.iv_alarm1:

                    Context mContext1 = getApplicationContext();
                    LayoutInflater inflater1 = (LayoutInflater) mContext1.getSystemService(LAYOUT_INFLATER_SERVICE);

                    View layout1 = inflater1.inflate(R.layout.dialog_alarm1,(ViewGroup) findViewById(R.id.da1));
                    AlertDialog.Builder aDialog1 = new AlertDialog.Builder(v.getContext());

                    aDialog1.setTitle("알림받을 방법 선택");
                    aDialog1.setView(layout1);

                    aDialog1.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog ad1 = aDialog1.create();
                    ad1.show();
                    break;
                case R.id.iv_alarm2:

                    Context mContext2 = getApplicationContext();
                    LayoutInflater inflater2 = (LayoutInflater) mContext2.getSystemService(LAYOUT_INFLATER_SERVICE);

                    View layout2 = inflater2.inflate(R.layout.dialog_alarm2,(ViewGroup) findViewById(R.id.da2));
                    AlertDialog.Builder aDialog2 = new AlertDialog.Builder(v.getContext());

                    aDialog2.setTitle("알림받을 방법 선택");
                    aDialog2.setView(layout2);

                    aDialog2.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog ad2 = aDialog2.create();
                    ad2.show();
                    break;
                case R.id.iv_alarm3:

                    Context mContext3 = getApplicationContext();
                    LayoutInflater inflater3 = (LayoutInflater) mContext3.getSystemService(LAYOUT_INFLATER_SERVICE);

                    View layout3 = inflater3.inflate(R.layout.dialog_alarm3,(ViewGroup) findViewById(R.id.da3));
                    AlertDialog.Builder aDialog3 = new AlertDialog.Builder(v.getContext());

                    aDialog3.setTitle("알림받을 방법 선택");
                    aDialog3.setView(layout3);

                    aDialog3.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog ad3 = aDialog3.create();
                    ad3.show();
                    break;
            }
        }
    };
    void doAction1(){
        if(isOtherPerson.equals("true")&&et_otherPhoneNo.getText().toString().equals("")){
            Toast.makeText(this,"동승자의 전화번호를 입력하세요!",Toast.LENGTH_SHORT).show();
           // Log.v(TAG,"동승자번호:"+phoneNum);
        }else if(isOtherPerson.equals("false")&& isPhoneAlarm.equals("false")&&isSmartWatch.equals("false")){
            Toast.makeText(this,"알림받을 방법을 선택하세요!",Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(this, GraphActivity.class);
            intent.putExtra("id", id);
            Log.v(TAG,"AlarmSelect에서의 id:"+id);
            intent.putExtra("isPhoneAlarm", isPhoneAlarm);
            intent.putExtra("isOtherPerson", isOtherPerson);
            intent.putExtra("isSmartWatch", isSmartWatch);
            intent.putExtra("otherPhoneNo", et_otherPhoneNo.getText().toString());
        Log.v(TAG,"동승자번호:"+ et_otherPhoneNo.getText().toString());
            startActivity(intent);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_alarm_select);
        Intent intent1 = getIntent();
        id = intent1.getExtras().getString("id");

        chk_phone = (CheckBox)findViewById(R.id.chk_phone);
        chk_otherPPL = (CheckBox)findViewById(R.id.chk_otherPPL);
        chk_watch = (CheckBox)findViewById(R.id.chk_watch);
//        bt_toMusicAlarm = (Button)findViewById(R.id.bt_toMusicAlarm);
//        bt_toWatchAlarm = (Button)findViewById(R.id.bt_toNotiAlarm);
        bt_alarmConfirm = (Button)findViewById(R.id.bt_alarmConfirm);
//        bt_dialog1 = (Button)findViewById(R.id.bt_dialog1);
        iv_alarm1 = (ImageView)findViewById(R.id.iv_alarm1);
        iv_alarm2 = (ImageView)findViewById(R.id.iv_alarm2);
        iv_alarm3 = (ImageView)findViewById(R.id.iv_alarm3);
        chk_phone.setOnClickListener(handler);
        chk_watch.setOnClickListener(handler);
        chk_otherPPL.setOnClickListener(handler);
        iv_alarm1.setOnClickListener(handler);
        iv_alarm2.setOnClickListener(handler);
        iv_alarm3.setOnClickListener(handler);
//        bt_toMusicAlarm.setOnClickListener(handler);
//        bt_toWatchAlarm.setOnClickListener(handler);
        bt_alarmConfirm.setOnClickListener(handler);
//        bt_dialog1.setOnClickListener(handler);
        textView9 = (TextView)findViewById(R.id.textView9);
        textView9.setTextColor(Color.TRANSPARENT);
        et_otherPhoneNo = (EditText)findViewById(R.id.et_otherPhoneNo);
        et_otherPhoneNo.setEnabled(false);
        adapter = BluetoothAdapter.getDefaultAdapter().getDefaultAdapter();
        if(adapter == null){
            Toast.makeText(this,"블루투스를 지원하지 않습니다",Toast.LENGTH_SHORT).show();
            Log.v(TAG,"블루투스 장비가 지원됨");
        }
        if(!adapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,BT_ENABLED);
        }
    }

    @Override
    public void onClick(View v) {

    }
}
