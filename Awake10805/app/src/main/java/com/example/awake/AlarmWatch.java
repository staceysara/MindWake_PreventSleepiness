package com.example.awake;

import android.annotation.TargetApi;
import android.app.Notification;
import android.bluetooth.BluetoothAdapter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.neurosky.thinkgear.TGDevice;

public class AlarmWatch extends AppCompatActivity {

    //    static final int NAPNOTI = 100;
    private static final int BASIC_NOTIFICATION_ID = 0;
    //    NotificationManager mNoti;
    Button btnAlarm2;
    Button btnAlarm3;
    Button NotiStop;
    int value;
    TextView tvnoti;

    //    MediaPlayer player = new MediaPlayer();
    TGDevice tgDevice;
    BluetoothAdapter bluetoothAdapter;

    final boolean rawEnabled = false;

    //순순
    // 기본 알람음 재생
    Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    //음악 알람을 눈 깜박임으로 끄기 위해, 5 칸짜리 배열 blinkTime 을 두고 0 으로 초기화할 것이다.
    //만약 [0] 과 [1], [1] 과 [2], [2] 와 [3], [3] 과 [4] 의 차이가,
    //currentTimeMillis() 로 <= 1000, 세 번 모두 AND 조건으로 묶이면 { 알람 꺼짐. }
    long[] blinkTime = new long[5];
    int blinkTimeIndex = 0;
    //순순

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_watch);

//        mNoti = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);


        btnAlarm2 = (Button)findViewById(R.id.btnAlarm2);
        btnAlarm3 = (Button)findViewById(R.id.btnAlarm3);
        NotiStop = (Button)findViewById(R.id.NotiStop);
        value=0;

        tvnoti = (TextView)findViewById(R.id.tvnoti);

        //순순
        for(int i=0; i<5; i++) {
            blinkTime[i] = 0;
        }
        //순순

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if(bluetoothAdapter == null) {
//            // Alert user that Bluetooth is not available
//            Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_LONG).show();
//            finish();
//            return;
//        }else {
//        	/* create the TGDevice */
//            tgDevice = new TGDevice(bluetoothAdapter, handler);
//
//            //순순
//            doStuff();
//            //순순
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_music, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onBtnAlarm2() {

        new CountDownTimer(120,2) { //이 인자는 아직도 찾아봐도 뭔지 모르겠다.

            @Override
            public void onTick(long millisUntilFinished) {

                value++;
                if (value % 5 == 0) {
                    //noti 를 재생
                    Notification noti = new Notification.Builder(AlarmWatch.this)
                            .setContentTitle("일어나세요")
                            .setContentText("여기서 자면 안 돼!")
                            .setSmallIcon(R.drawable.logo)
                            .setVibrate(new long[]{0, 5000})
                            .build();
//        noti.defaults |= Notification.DEFAULT_VIBRATE;
//                    noti.flags = Notification.FLAG_INSISTENT;
                    NotificationManagerCompat nManager = NotificationManagerCompat.from(AlarmWatch.this);
                    nManager.notify(BASIC_NOTIFICATION_ID, noti);
                    if (value != -10) {
                        nManager.cancel(0);
                    }
                    tvnoti.append(Integer.toString(value));
                    tvnoti.append("\n");

                }
            }

            @Override
            public void onFinish() {

            }
        }.start();

//        //noti 를 재생
//        Notification noti = new Notification.Builder(AlarmWatch.this)
//                .setContentTitle("일어나세요")
//                .setContentText("여기서 자면 안 돼!")
//                .setSmallIcon(R.drawable.logo)
//                .setVibrate(new long[] {0, 30000})
//                .build();
////        noti.defaults |= Notification.DEFAULT_VIBRATE;
//        noti.flags = Notification.FLAG_INSISTENT;
//        NotificationManagerCompat nManager = NotificationManagerCompat.from(this);
//        nManager.notify(BASIC_NOTIFICATION_ID, noti);

//                .setDefaults(Notification.DEFAULT_VIBRATE)
//                .setVibrate(new long[] {1000,1000,500,500,200,200,200,200,200,200})
//                .build();
//
//        noti.flags = Notification.FLAG_INSISTENT;
//
//        mNoti.notify(NAPNOTI, noti);
    }


    public void onNotiStop() {
        value = -10;
    }

    public void onClick(View v) {


        switch( v.getId() ) {
            case R.id.btnAlarm2 :
                // 1회 알람 시작 - 시간 간격 지정
                onBtnAlarm2();
                break;
            case R.id.NotiStop:
                onNotiStop();
                break;
        }
    }

    /**
     * Handles messages from TGDevice
     */
//    private final Handler handler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case TGDevice.MSG_STATE_CHANGE:
//
//                    switch (msg.arg1) {
//                        case TGDevice.STATE_IDLE:
//                            break;
//                        case TGDevice.STATE_CONNECTING:
//                            tv.append("Connecting...\n");
//                            break;
//                        case TGDevice.STATE_CONNECTED:
//                            tv.append("Connected.\n");
//                            tgDevice.start();
//                            break;
//                        case TGDevice.STATE_NOT_FOUND:
//                            tv.append("Can't find\n");
//                            break;
//                        case TGDevice.STATE_NOT_PAIRED:
//                            tv.append("not paired\n");
//                            break;
//                        case TGDevice.STATE_DISCONNECTED:
//                            tv.append("Disconnected mang\n");
//                    }
//
//                    break;
//
//                case TGDevice.MSG_BLINK:
//                    tv.append("Blink: " + msg.arg1 + "\n");
////                    //순순
////                    if (blinkTimeIndex == 5) {
////                        for (int i=0; i<5; i++)
////                            blinkTime[i] = 0;
////                        blinkTimeIndex = 0;
////                    }
////                    blinkTime[blinkTimeIndex] = System.currentTimeMillis();
////                    blinkTimeIndex++;
////                    if ((blinkTime[1] - blinkTime[0] > 0) && (blinkTime[1] - blinkTime[0] < 1000) && (blinkTime[2] - blinkTime[1] > 0) && (blinkTime[2] - blinkTime[1] < 1000) && (blinkTime[3] - blinkTime[2] > 0) && (blinkTime[3] - blinkTime[2] < 1000) && (blinkTime[4] - blinkTime[3] > 0) && (blinkTime[4] - blinkTime[3] < 1000)) {
////                        // 소리 끄기
////                        if(player.isPlaying()){
////                            player.stop();
////                        }
////                    }
////                    //순순
//                    break;
//
//
//            }
//        }
//    };

    //    public void doStuff(View view) {
    public void doStuff() {
        if(tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED)
            tgDevice.connect(rawEnabled);
        //tgDevice.ena
    }
}
