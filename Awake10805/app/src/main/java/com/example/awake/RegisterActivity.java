package com.example.awake;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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



public class RegisterActivity extends AppCompatActivity {
    private static final String TAG ="MainActivity";
    EditText et_regID;
    EditText et_regPW;
    EditText et_regPWAgain;
    Button bt_regOK;

    Button bt_msg;
    Button bt_toMusicAlarm;
    Button bt_toWatchAlarm;


    //    Button bt_regCancel;
//    Button bt_voice;
//    Button bt_msg;
    // EditText et_voice;
    ProgressDialog dialog;
    EditText et_phonenum;
    Context context;

    View.OnClickListener bhandler = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.bt_regOK:
                    doAction1();
                    break;
//                case R.id.bt_regCancel:
//                    doAction2();
//                    break;
//                case R.id.bt_voice:
//                    Log.v(TAG,"들어옴");
//                    doAction3();
//                    break;
                //지연
                case R.id.bt_msg:
                    doAction4();
                    break;
                case R.id.bt_toMusicAlarm:
                    toMusicAlarm();
                    break;
                case R.id.bt_toNotiAlarm:
                    toWatchAlarm();
                    break;
            }
        }
    };

    void toMusicAlarm(){
        Intent intent = new Intent(this, AlarmMusic.class);
        startActivity(intent);
    }
    void toWatchAlarm() {
        Intent tent = new Intent(this, AlarmWatch.class);
        startActivity(tent);
    }

    public void doAction1(){
        if(et_regID.getText().toString().equals("")||et_regPW.getText().toString().equals("")||et_regPWAgain.getText().toString().equals("")){
            Toast.makeText(this,"빈 입력칸이 존재하면 안됩니다",Toast.LENGTH_SHORT).show();
        }else {
            dialog = ProgressDialog.show(RegisterActivity.this, "", "회원가입 중 입니다");
            new RegisterThread().start();
        }
    }
    public void doAction2(){
        Intent intent = new Intent(this,HelloEEG.class);
        startActivity(intent);
    }
    public void doAction3(){
        //PackageManager:app이 설치된 폰의 static정보 추출. 설치된 앱 목록
        //ActivityManager: ui구성하는 아니라 app이 설치된 폰의 app관련 dynamic정보
        PackageManager pm = getPackageManager();

        //안드로이드는 음성인식기를 음성인식액티비티를 통해 사용. 음성인식액티비티: 사용자음성을 인식해 파형분석. 음성인식결과인 문자열의 리스트를 얻을 수 있음
        //intent에 반응할 componenet정보
        //ResolveInfo타입의 리스트를 리턴값으로 얻음. 이 리스트를 순회하며 필요정보를 얻어낼 수 있음
        List<ResolveInfo> activities=pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),0);//0: flag

        if(activities.size()>0){//음성인식 상수값이 RecognizerIntent클래스에 정의되어있음.
            Intent intent1 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent1.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);//사람말을 곧이곧대로문자로
            intent1.putExtra(RecognizerIntent.EXTRA_PROMPT,"알람을 끄려면 '하이'를 말하세요");
            startActivityForResult(intent1,20);//20:request code
        }else{
            Toast t = Toast.makeText(this,"사용자의 휴대전화에서 음성인식을 지원하지 않습니다.",Toast.LENGTH_SHORT);
            t.show();
        }

    }
    public void doAction4(){
//String phonenum = et_phonenum.getText().toString();
        //발신자 전화번호를 유저 폰번호 추출로
        TelephonyManager telephony=(TelephonyManager)getSystemService(TELEPHONY_SERVICE);

        Log.v(TAG,telephony.toString());
        //  String myNumber=telephony.getLine1Number();



        //발송후 사후처리 등록
        Intent sIntent = new Intent("ACTION_SMS");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,sIntent,0);

        //sms발송
        SmsManager sms = SmsManager.getDefault();
        //지연 sms.sendTextMessage(phonenum,myNumber,"운전자가 졸고 있습니다. 깨워주세요",pendingIntent,null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==20&&resultCode==RESULT_OK){
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Log.v(TAG,results.get(0));
            // et_voice.setText(results.get(0));
            if(!results.get(0).equals("하이")){
                doAction3();
            }

        }
    }

    String data ="";
    class RegisterThread extends Thread{
        public void run(){
            String url = ServerUtil.SERVER_URL+"member";

            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("action","insert"));
            nameValue.add(new BasicNameValuePair("id", et_regID.getText().toString()));
            nameValue.add(new BasicNameValuePair("pw1", et_regPW.getText().toString()));
            nameValue.add(new BasicNameValuePair("pw2",et_regPWAgain.getText().toString()));

            HttpClient client = NetManager1.getHttpClient();
            HttpPost post = NetManager1.getPost(url);

            HttpResponse response = null;
            BufferedReader br = null;
            StringBuffer sb = null;

            String line = "";
            try{
                Log.v(TAG,"registerprocess");
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
                        Log.v(TAG,"register process msg : "+sb);
                        data = sb.toString();
                        handler.sendEmptyMessage(0);
                        break;
                    default:
                        handler.sendEmptyMessage(1);
                }


            }catch(Exception e){
                Log.v(TAG,"register paraser error :" +e);
                handler.sendEmptyMessage(2);
            }finally{
                try{
                    br.close();
                }catch(Exception e){}
            }

        }
    }
    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            dialog.dismiss();
            switch(msg.what){
                case 0:
                    if(doCheck()){
                        Intent intent = new Intent(RegisterActivity.this,AlarmSelect.class);
                        intent.putExtra("name",data);
                        startActivity(intent);
                        finish();
                    }else{
                        showToast("회원가입등록에 실해했습니다. 다시 시도하세요");
                    }
                    break;
            }
        }
    };
    void showToast(String text){
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
        et_regID.setText("");
        et_regPW.setText("");
        et_regPWAgain.setText("");
    }
    boolean doCheck(){
        if(data.indexOf("fail")>-1){
            return false;

        }else{
            return true;
        }
    }

    //sms 전송후 전달되는 sent ack에 의해 실행될 receiver
    BroadcastReceiver sentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String msg="";
            switch(getResultCode()){
                case Activity.RESULT_OK:
                    msg="OK!";
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    msg="generic error!";
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF://failed because radio is off
                    msg="radio off!";
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    msg="pdu error!";
                    break;

            }
            Toast t = Toast.makeText(RegisterActivity.this,msg,Toast.LENGTH_SHORT);
            t.show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        et_regPW = (EditText)findViewById(R.id.et_regPW);
        et_regPWAgain = (EditText)findViewById(R.id.et_regPWAgain);
        et_regID = (EditText)findViewById(R.id.et_regID);
        //   et_voice=(EditText)findViewById(R.id.et_voice);
        //   et_phonenum=(EditText)findViewById(R.id.et_phonenum);
        bt_regOK = (Button)findViewById(R.id.bt_regOK);
//        bt_regCancel=(Button)findViewById(R.id.bt_regCancel);
//        bt_voice=(Button)findViewById(R.id.bt_voice);
           bt_msg = (Button)findViewById(R.id.bt_msg);
                bt_toMusicAlarm = (Button)findViewById(R.id.bt_toMusicAlarm);
        bt_toWatchAlarm = (Button)findViewById(R.id.bt_toNotiAlarm);
        bt_regOK.setOnClickListener(bhandler);
//        bt_regCancel.setOnClickListener(bhandler);
//        bt_voice.setOnClickListener(bhandler);
           bt_msg.setOnClickListener(bhandler);
                bt_toMusicAlarm.setOnClickListener(bhandler);
        bt_toWatchAlarm.setOnClickListener(bhandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(sentReceiver,new IntentFilter("ACTION_SMS"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(sentReceiver);
    }
}
