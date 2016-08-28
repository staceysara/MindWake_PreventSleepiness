package com.example.awake;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.neurosky.thinkgear.TGDevice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmMusic extends AppCompatActivity {
    private static final String TAG ="MainActivity";
    Button btnAlarm1;
    TextView tv;
    /* MediaPlayer player = new MediaPlayer();*/
    MediaPlayer player;
    TGDevice tgDevice;
    BluetoothAdapter bluetoothAdapter;
    TimerTask mTask;
    Timer mTimer;
    TimerTask mTempTask;
    Timer mTempTimer;
    PackageManager pm;
    Intent intent1;
    int length;
    int activityResult=0;
    final boolean rawEnabled = false;
    int speechFlag=0;//하이를 말안할경우 0, 말했을 경우 알람끄고1
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
        setContentView(R.layout.activity_alarm_music);
        btnAlarm1 = (Button) findViewById(R.id.btnAlarm1);
        tv = (TextView) findViewById(R.id.tView);
        player = new MediaPlayer();
        pm= getPackageManager();
         intent1 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //순순
        for (int i = 0; i < 5; i++) {
            blinkTime[i] = 0;
        }
        //순순


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

    public void onBtnAlarm1() {
//        // 기본 알람음 재생
//        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        MediaPlayer player = new MediaPlayer();
        try {
//            player.setDataSource(this, alert);
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
//        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
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
/*    public void onBtnAlarm1() {
//        // 기본 알람음 재생
//        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
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
//        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
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
    }*/

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAlarm1:
                // 1회 알람 시작 - 시간 간격 지정
                // onBtnAlarm1();
                onBtnAlarm2();
                break;
        }
    }

    /**
     * Handles messages from TGDevice
     */
    public void onBtnAlarm2() {
       player = new MediaPlayer();         // 객체생성

// TYPE_RINGTONE 을 하면 현재 설정되어 있는 밸소리를 가져온다.
// 만약 알람음을 가져오고 싶다면 TYPE_ALARM 을 이용하면 된다
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        try {
            // 이렇게 URI 객체를 그대로 삽입해줘야한다.
            //인터넷에서 url.toString() 으로 하는것이 보이는데 해보니까 안된다 -_-;
           player.setDataSource(this, alert);


            // 출력방식(재생시 사용할 방식)을 설정한다. STREAM_RING 은 외장 스피커로,
            // STREAM_VOICE_CALL 은 전화-수신 스피커를 사용한다.
            player.setAudioStreamType(AudioManager.STREAM_RING);

            player.setLooping(true);  // 반복여부 지정
            player.prepare();    // 실행전 준비
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.start();   // 실행 시작
mTask = new TimerTask() {
    @Override
    public void run() {
        Log.v(TAG,"run구문안으로들어옴 player.pause, length구하고 doAction3할거임");

      if(activityResult==0) {
          player.pause();
          length = player.getCurrentPosition();
          doAction3();
      }
            // mTimer.schedule(mTask,8000,6000);//8초 후 Task를 실행하고 6초마다 반복하라

    }
};//new timertask
        mTimer = new Timer();
 // mTimer.schedule(mTask,8000,6000);//8초 후 Task를 실행하고 6초마다 반복하라
        mTimer.schedule(mTask,8000);//8초 후 Task를 실행하라

/*
ScheduledJob job = new ScheduledJob();
        Timer jobScheduler = new Timer();
        jobScheduler.schedule(job,8000,6000);//알람끔
*/


    }

    public void doAction3(){

        //PackageManager:app이 설치된 폰의 static정보 추출. 설치된 앱 목록
        //ActivityManager: ui구성하는 아니라 app이 설치된 폰의 app관련 dynamic정보
       // PackageManager pm = getPackageManager();

        //안드로이드는 음성인식기를 음성인식액티비티를 통해 사용. 음성인식액티비티: 사용자음성을 인식해 파형분석. 음성인식결과인 문자열의 리스트를 얻을 수 있음
        //intent에 반응할 componenet정보
        //ResolveInfo타입의 리스트를 리턴값으로 얻음. 이 리스트를 순회하며 필요정보를 얻어낼 수 있음
        List<ResolveInfo> activities=pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),0);//0: flag

        if(activities.size()>0){//음성인식 상수값이 RecognizerIntent클래스에 정의되어있음.
          //  Intent intent1 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent1.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);//사람말을 곧이곧대로문자로
            intent1.putExtra(RecognizerIntent.EXTRA_PROMPT,"알람을 끄려면 '하이'를 말하세요");
            startActivityForResult(intent1,20);//20:request code
            //인텐트에 정보를 실어 다른 액티비티로 보내주고 되돌아오는것을 onActivityResult로 받음
        }else{
            Toast t = Toast.makeText(this,"사용자의 휴대전화에서 음성인식을 지원하지 않습니다.",Toast.LENGTH_SHORT);
            t.show();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        Log.v(TAG, results.get(0));
        activityResult=1;
        if(requestCode==20&&resultCode==RESULT_OK){


            // et_voice.setText(results.get(0));
           /* if(!results.get(0).equals("하이")){
                // doAction3();
                Log.v(TAG,"하이를 말안함. player.seekTo(length)하고 player.start()할거임");
                player.seekTo(length);
                player.start();
            }*/if(results.get(0).equals("하이")){//하이를말한경우
                speechFlag=1;
                player.stop();
                mTimer.cancel();

            }else{
                finishActivity(20);
                Log.v(TAG,"else1구문안으로들어옴 player.pause, length구하고 doAction3할거임");
       /*     player.pause();
            length = player.getCurrentPosition();
            doAction3();
            Log.v(TAG,"하이를 말안함. else if문에서 player.seekTo(length)하고 player.start()할거임");
       */     player.seekTo(length);
                player.start();
                mTimer.cancel();
                mTimer.schedule(mTask,5000);
            }
        }else{
            finishActivity(20);
            Log.v(TAG,"else2구문안으로들어옴 player.pause, length구하고 doAction3할거임");
       /*     player.pause();
            length = player.getCurrentPosition();
            doAction3();
            Log.v(TAG,"하이를 말안함. else if문에서 player.seekTo(length)하고 player.start()할거임");
       */     player.seekTo(length);
            player.start();
            mTimer.schedule(mTask,5000);
        }
      //  super.onActivityResult(requestCode,resultCode,data);
    }
}
class ScheduledJob extends TimerTask{
    private static final String TAG1 ="MainActivity";
    @Override
    public void run() {
     //  Log.v(TAG1,new Date().toString());

    }
}
