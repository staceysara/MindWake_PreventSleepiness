package com.example.awake;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.neurosky.thinkgear.TGDevice;
import com.neurosky.thinkgear.TGEegPower;
import com.neurosky.thinkgear.TGRawMulti;

import java.io.IOException;

public class HelloEEG extends Activity {
    int hAlphaHead=0,hAlphaTail=-1;
    int hAlphaOverCount=0;//16000,000이상인걸 카운트
    int hBetaOverCount=0;//16000,000이상인걸 카운트
    int hAlphaGijun=0;
    int hBetaGijun=0;
    int hBetaHead=0,hBetaTail=-1;
    int hAlphaQueue_Capacity=300;
    int hBetaQueue_Capacity=300;
    int hAlphaArr[] = new int[hAlphaQueue_Capacity];
    int hBetaArr[]= new int[hBetaQueue_Capacity];

    int AVGhead=0,AVGtail=-1;
    int Arrhead=0,Arrtail=-1;
    int ARRQueue_Capacity=500;
    int AVGQueue_Capacity=300;
    int AttentionArr[]=new int[ARRQueue_Capacity];//300개의 뇌파정보
    int AVGAttention[] = new int[AVGQueue_Capacity];
    int tempAVG=0;//AVGAttention에 들어감
    int sum=0;//300까지 다 더한거 저장
    int attentionArrCount=0;
    int avgAttTempCount=0;
    int decBindo=0;


    MediaPlayer player = new MediaPlayer();
    BluetoothAdapter bluetoothAdapter;

    TextView tv;
    Button b;

    TGDevice tgDevice;

    //순순
    /** TGEegPower. */
    private TGEegPower fbands;
    //순순

    final boolean rawEnabled = false;

    //순순
    /** TAG. */
    private final static String TAG = "BRAIN";
    //순순

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_eeg);
        tv = (TextView)findViewById(R.id.textView1);
        tv.setText("");
        tv.append("Android version: " + Integer.valueOf(android.os.Build.VERSION.SDK) + "\n" );
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
            // Alert user that Bluetooth is not available
            Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }else {
        	/* create the TGDevice */
            tgDevice = new TGDevice(bluetoothAdapter, handler);

            //순순
            doStuff();
            //순순
        }
    }

    @Override
    public void onDestroy() {
        tgDevice.close();
        super.onDestroy();
    }
    /**
     * Handles messages from TGDevice
     */
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TGDevice.MSG_STATE_CHANGE:

                    switch (msg.arg1) {
                        case TGDevice.STATE_IDLE:
                            break;
                        case TGDevice.STATE_CONNECTING:
                            tv.append("Connecting...\n");
                            break;
                        case TGDevice.STATE_CONNECTED:
                            tv.append("Connected.\n");
                            tgDevice.start();
                            break;
                        case TGDevice.STATE_NOT_FOUND:
                            tv.append("Can't find\n");
                            break;
                        case TGDevice.STATE_NOT_PAIRED:
                            tv.append("not paired\n");
                            break;
                        case TGDevice.STATE_DISCONNECTED:
                            tv.append("Disconnected mang\n");
                    }

                    break;

                //순순
                case TGDevice.MSG_EEG_POWER:
                    Log.d("TAG", "EEG");

                    fbands = (TGEegPower)msg.obj;


                    Log.d(TAG, "High Alpha:" + fbands.highAlpha);
                    tv.append("\n" + "High Alpha:" + "\t" + fbands.highAlpha + "\t");
                    if(attentionArrCount<hAlphaQueue_Capacity+1){
                        if(fbands.highAlpha>16000000){
                            hAlphaArr[hAlphaHead]=1;
                            hAlphaOverCount++;
                        }
/*
                        hAlphaArr[hAlphaHead] = fbands.highAlpha;
                        if(fbands.highAlpha>16000000){
                            hAlphaOverCount++;
                        }
*/
                        hAlphaHead = (hAlphaHead+1)%hAlphaQueue_Capacity;
                        hAlphaTail =(hAlphaTail+1)%hAlphaQueue_Capacity;

                    }else if(attentionArrCount==hAlphaQueue_Capacity){//같을 때 기준점을 잡아둠.
                        hAlphaGijun=hAlphaOverCount;
                        onBtnAlarm1();
                    }else{//300초보다 클 때
                        if(hAlphaArr[hAlphaHead]==1){
                            hAlphaOverCount-=1;
                            hAlphaArr[hAlphaHead]=0;
                        }
                        if(fbands.highAlpha>16000000){
                            hAlphaOverCount+=1;
                            hAlphaArr[hAlphaHead]=1;
                        }

                        hAlphaHead = (hAlphaHead+1)%hAlphaQueue_Capacity;
                        hAlphaTail =(hAlphaTail+1)%hAlphaQueue_Capacity;
                    /*    if(hAlphaOverCount<0.9*hAlphaGijun){*/
                        if(hAlphaOverCount<hAlphaGijun){
                            onBtnAlarm1();
                        }

                    }


                    Log.d(TAG, "Low Alpha:" + fbands.lowAlpha);
                    tv.append("Low Alpha:" + "\t" + fbands.lowAlpha + "\t");
                    Log.d(TAG, "High Beta:" + fbands.highBeta);
                    tv.append("High Beta:" + "\t" + fbands.highBeta + "\t");

                    if(attentionArrCount<hBetaQueue_Capacity+1){
                        if(fbands.highBeta>16000000){
                            hBetaArr[hBetaHead]=1;
                            hBetaOverCount++;
                        }
                        hBetaHead=(hBetaHead+1)%hBetaQueue_Capacity;
                        hBetaTail=(hBetaTail+1)%hBetaQueue_Capacity;
                    }else if(attentionArrCount==hBetaQueue_Capacity){
                        hBetaGijun=hBetaOverCount;
                    }else{//300초보다 클 때
                        if(hBetaArr[hBetaHead]==1){
                            hBetaOverCount-=1;
                            hBetaArr[hBetaHead]=0;
                        }
                        if(fbands.highBeta>16000000){
                            hBetaOverCount+=1;
                            hBetaArr[hBetaHead]=1;

                        }
                        hBetaHead = (hBetaHead+1)%hBetaQueue_Capacity;
                        hBetaTail=(hBetaTail+1)%hBetaQueue_Capacity;
                        if(hBetaOverCount<0.9*hBetaGijun){
                            onBtnAlarm1();
                        }

                    }



                    Log.d(TAG, "Low Beta:" + fbands.lowBeta);
                    tv.append("Low Beta:" + "\t" + fbands.lowBeta + "\t");
                    Log.d(TAG, "Theta:" + fbands.theta);
                    tv.append("Theta:" + "\t" + fbands.theta + "\t");
                    Log.d(TAG, "Delta:" + fbands.delta);
                    tv.append("Delta:" + "\t" + fbands.delta + "\t");
                    Log.d(TAG, "Mid Gamma:" + fbands.midGamma);
                    tv.append("Mid Gamma:" + "\t" + fbands.midGamma + "\t");
                    Log.d(TAG, "Low Gamma:" + fbands.lowGamma);
                    tv.append("Low Gamma:" + "\t" + fbands.lowGamma + "\t");


                /*    Log.d(TAG, "High Gamma:" +  fbands.midGamma);
                    tv.append("This is High Gamma"+fbands.midGamma+"\t");


                    Log.d(TAG, "Low Gamma:" +  fbands.lowGamma);
                    tv.append("This is Low Gamma"+fbands.lowGamma+"\t");*/
                    break;
                //순순


                case TGDevice.MSG_POOR_SIGNAL:
                    //signal = msg.arg1;
           //0526         tv.append("PoorSignal: " + msg.arg1 + "\n");
                    break;
                case TGDevice.MSG_RAW_DATA:
                    //raw1 = msg.arg1;
                    //tv.append("Got raw: " + msg.arg1 + "\n");
                    break;
                case TGDevice.MSG_HEART_RATE:
               //0526     tv.append("Heart rate: " + msg.arg1 + "\n");

//                    tv.append("Low Alpha : " + fbands.lowAlpha);

                    break;
                case TGDevice.MSG_ATTENTION:
                    //att = msg.arg1;

                    tv.append("Attention:" + "\t" + msg.arg1 + "\t");
                    attentionArrCount++;
                    if(attentionArrCount<ARRQueue_Capacity+1) {
                        AttentionArr[Arrhead] = msg.arg1;
                        sum += AttentionArr[Arrhead];
                        Arrhead = (Arrhead + 1) % ARRQueue_Capacity;
                        Arrtail = (Arrtail + 1) % ARRQueue_Capacity;
                    }
                    else if(attentionArrCount==ARRQueue_Capacity+1){//하나가 들어오면 attentionArrCount는 1이 됨. 300개가 들어왓을 때는 301이 됨.
                    AVGAttention[AVGhead] = sum / ARRQueue_Capacity;
                    AVGhead = (AVGhead + 1) % AVGQueue_Capacity;
                    AVGtail = (AVGtail + 1) % AVGQueue_Capacity;
                }else if(attentionArrCount>ARRQueue_Capacity+1){
                        //AVGAttention안에 두번째부터 값을 넣을 수 있게 됨.
                        sum-=AttentionArr[Arrhead];
                        AttentionArr[Arrhead]=msg.arg1;
                        sum+=AttentionArr[Arrhead];
                        Arrhead=(Arrhead+1)%ARRQueue_Capacity;
                        Arrtail = (Arrtail + 1) % ARRQueue_Capacity;

                        AVGAttention[AVGhead]=sum/ARRQueue_Capacity;
                        avgAttTempCount++;
                        AVGhead=(AVGhead+1)%AVGQueue_Capacity;
                        AVGtail=(AVGtail+1)%AVGQueue_Capacity;


                    }
                    if(avgAttTempCount>AVGQueue_Capacity){
                        decBindo=0;
                        //head부터 capacity까지 비교
                        if(AVGhead!=AVGQueue_Capacity-1){
                            for(int i=AVGhead;i<AVGQueue_Capacity-1;i++){
                                if(AVGAttention[i]>AVGAttention[i+1]){
                                    decBindo++;
                                }
                            }
                            if(AVGhead!=0){
                                for(int i=0;i<=AVGtail;i++){
                                    if(AVGAttention[i]>AVGAttention[i+1]){
                                        decBindo++;
                                    }
                                }
                            }
                        }
                        if(decBindo>130){
                            //alarm();
                            onBtnAlarm1();
                        }
                    }


                    //Log.v("HelloA", "Attention: " + att + "\n");
                    break;
                case TGDevice.MSG_MEDITATION:

                    tv.append("Meditation:" + "\t" + msg.arg1 + "\t");

                    break;
                case TGDevice.MSG_BLINK:

                    tv.append("Blink:" + "\t" + msg.arg1 + "\t");

                    break;
                case TGDevice.MSG_RAW_COUNT:
                    //tv.append("Raw Count: " + msg.arg1 + "\n");
                    break;
                case TGDevice.MSG_LOW_BATTERY:
                    Toast.makeText(getApplicationContext(), "Low battery!", Toast.LENGTH_SHORT).show();
                    break;
                case TGDevice.MSG_RAW_MULTI:
                    TGRawMulti rawM = (TGRawMulti)msg.obj;
        //0526            tv.append("Raw1: " + rawM.ch1 + "\nRaw2: " + rawM.ch2);
                default:
                    break;
            }
        }
    };
    // 1회 알람 시작 - 시간 간격 지정
    public void onBtnAlarm1() {
//        // 수행할 동작을 생성
//        Intent intent = new Intent(this, MainActivity.class);
//        PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
//        // 1회 알람 시작
//        mAlarmMgr.set(AlarmManager.RTC, System.currentTimeMillis() + 5000, pIntent);

        // 기본 알람음 재생
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(this, alert);
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (SecurityException e1) {
            e1.printStackTrace();
        } catch (IllegalStateException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            player.setAudioStreamType(AudioManager.STREAM_ALARM);
            player.setLooping(true);
            try {
                player.prepare();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            player.start();
        }
    }
//    public void doStuff(View view) {
public void doStuff() {
        if(tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED)
            tgDevice.connect(rawEnabled);
        //tgDevice.ena
    }
}