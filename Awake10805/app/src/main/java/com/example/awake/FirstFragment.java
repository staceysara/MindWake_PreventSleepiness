package com.example.awake;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.neurosky.thinkgear.TGDevice;
import com.neurosky.thinkgear.TGEegPower;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;


public class FirstFragment extends Fragment implements View.OnClickListener, LocationListener {

    private static final String TAG = "FirstFragment";

    Context context;
    Context mContext;
    ImageView bt_dialog2;
    String isPhoneAlarm = "";
    String isOtherPerson = "";
    String isSmartWatch = "";
    String phoneNum = "";
    private ViewGroup layoutGraphView;
    private BluetoothAdapter bluetoothAdapter;

    // Signal processing
    private Analyzer mAnalyzer;
    private SignalHolder mSignalHolder;

    //tgDevice: 헤드셋 디바이스 클래스. 이걸로 소스 검색하면 뇌파 데이터 처리를 위한 부분이 다 나옴.
    private TGDevice tgDevice;
    final boolean rawEnabled = false;

    // Serial control
    private SerialListener mListener = null;
    private SerialConnector mSerialConn = null;

    private boolean mSendMindSignal = false;
    private int[] mSignalBuffer = new int[3];

    // Layout
    public static int mScreenW = 0;
    public static int mScreenH = 0;

    public LinearLayout mModeMonitoring;
    public LinearLayout mModeController;

    //----- Monitoring layout
    private TextView mTextConn;
    private TextView mTextRenderRaw;
    private TextView mTextSystem;
    private LinearLayout mLayoutRaw;
    private LinearLayout mLayoutValue;
    public RenderingView mRenderRaw;
    public RenderingView mRenderValue;
    private TextView mTextRawDesc;
    private TextView mTextValueDesc;

    //----- Controller layout
    private TextView mTextSerial;


    // Global
    private int[] mEEGRawBuffer = null;
    private int mEEGRawBufferIndex = 0;

    private int mFreqDrawingMode = Constants.FREQ_DRAW_MODE_ALPHA_TO_GAMMA;

    private int mCurrentViewMode = Constants.VIEW_MODE_MONITORING;

    private int mSerialLogCount = 0;
    private static final int SERIAL_LOG_COUNT_MAX = 300;
    private static final int SIGNAL_RATIO = 2;

    double lat;
    double lon;

    //순순
    private static final int BASIC_NOTIFICATION_ID = 0;
    int value;

    int timeLeft = 0; //시간이 얼마나 흘렀는지 알려줌
    int[] ha300 = new int[300]; //헤드셋 착용 직후 30 초 이후, 300 초 동안 측정되는 high alpha 수치가 들어가는 배열
    int[] la300 = new int[300]; //헤드셋 착용 직후 30 초 이후, 300 초 동안 측정되는 low alpha 수치가 들어가는 배열
    int[] hb300 = new int[300]; //헤드셋 착용 직후 30 초 이후, 300 초 동안 측정되는 high beta 수치가 들어가는 배열
    int[] lb300 = new int[300]; //헤드셋 착용 직후 30 초 이후, 300 초 동안 측정되는 low beta 수치가 들어가는 배열

    int halahblbHeadIndex300 = 0;//ha300, la300, hb300,lb300의 인덱스. 현재 들어가야 할 곳
    Boolean wait30 = false;//false일 경우 졸음체크해도됨, true일 경우 알람이 울린 후 30초가 지나지 않았으니 졸음체크 하면 안됨
    int wait30Now = 0;//wait30이 true일 때 몇초 지나갔는지 (30초가 지났을 경우 졸음여부측정)

    int standard_ha = 0;//330초 때 기준점
    int standard_la = 0;
    int standard_hb = 0;
    int standard_lb = 0;

    int tempBindoHa = 0;//빈도 구할 때 temp
    int tempBindoLa = 0;
    int tempBindoHb = 0;
    int tempBindoLb = 0;


    TextView text_system;

    MediaPlayer player = new MediaPlayer();
    // 기본 알람음 재생
    Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    //음악 알람을 눈 깜박임으로 끄기 위해, 5 칸짜리 배열 blinkTime 을 두고 0 으로 초기화할 것이다.
    //만약 [0] 과 [1], [1] 과 [2], [2] 와 [3], [3] 과 [4] 의 차이가,
    //currentTimeMillis() 로 <= 1000, 세 번 모두 AND 조건으로 묶이면 { 알람 꺼짐. }
    long[] blinkTime = new long[5];
    int blinkTimeIndex = 0;
    //순순

    /*****************************************************
     * Initialization methods
     ******************************************************/
    public FirstFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //순순
      /*  for (int i=0; i<300; i++)
            ha300[i] = 0;

        for (int i=0; i<300; i++)
            la300[i] = 0;

        for (int i=0; i<300; i++)
            hb300[i] = 0;

        for (int i=0; i<300; i++)
            lb300[i] = 0;

        for (int i=0; i<20; i++)
            t_ha[i] = 0;

        for (int i=0; i<20; i++)
            t_la[i] = 0;

        for (int i=0; i<20; i++)
            t_hb[i] = 0;

        for (int i=0; i<20; i++)
            t_lb[i] = 0;
        //순순*/

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        Bundle extra = getArguments();
        isPhoneAlarm = extra.getString("isPhoneAlarm");
        isOtherPerson = extra.getString("isOtherPerson");
        isSmartWatch = extra.getString("isSmartWatch");
        phoneNum = extra.getString("otherPhoneNo");

        bt_dialog2 = (ImageView) view.findViewById(R.id.bt_dialog2);
        bt_dialog2.setOnClickListener(this);
        layoutGraphView = (ViewGroup) view.findViewById(R.id.layoutGraphView);
        //  mContext = getApplicationContext();
        mContext = getActivity().getApplicationContext();
        //----- Layout
        //   requestWindowFeature(Window.FEATURE_NO_TITLE);
        //      setContentView(R.layout.activity_core_signal_main);

        mModeMonitoring = (LinearLayout) view.findViewById(R.id.layout_monitoring);
        mModeController = (LinearLayout) view.findViewById(R.id.layout_controller);
        mModeController.setVisibility(View.GONE);
        mCurrentViewMode = Constants.VIEW_MODE_MONITORING;


        mTextConn = (TextView) view.findViewById(R.id.text_conn);
        //  mTextSystem = (TextView) view.findViewById(R.id.text_system);
        mTextRenderRaw = (TextView) view.findViewById(R.id.text_title_render1);
        mTextRenderRaw.setOnClickListener(this);

        mTextRawDesc = (TextView) view.findViewById(R.id.text_freq_desc);
        SpannableString sText1 = new SpannableString("Delta, Theta, Alpha L, Alpha H, Beta L, Beta H, Gamma M, Gamma H");
        sText1.setSpan(new ForegroundColorSpan(0xFF00FF00), 0, 5, 0);
        sText1.setSpan(new ForegroundColorSpan(0xFF00AA00), 7, 12, 0);
        sText1.setSpan(new ForegroundColorSpan(0xFF0000FF), 14, 21, 0);
        sText1.setSpan(new ForegroundColorSpan(0xFF0000AA), 23, 30, 0);
        sText1.setSpan(new ForegroundColorSpan(0xFFFF0000), 32, 38, 0);
        sText1.setSpan(new ForegroundColorSpan(0xFFAA0000), 40, 46, 0);
        sText1.setSpan(new ForegroundColorSpan(0xFFAAAAAA), 48, 55, 0);
        sText1.setSpan(new ForegroundColorSpan(0xFF777777), 57, 64, 0);
        mTextRawDesc.append(sText1);

        mTextValueDesc = (TextView) view.findViewById(R.id.text_value_desc);
        SpannableString sText = new SpannableString("Attention, Meditation, Blink, Heart Rate, Poor Signal");
        sText.setSpan(new ForegroundColorSpan(Color.RED), 0, 9, 0);
        sText.setSpan(new ForegroundColorSpan(Color.BLUE), 11, 21, 0);
        sText.setSpan(new ForegroundColorSpan(Color.GREEN), 23, 28, 0);
        sText.setSpan(new ForegroundColorSpan(0xFF444444), 30, 40, 0);
        sText.setSpan(new ForegroundColorSpan(0xFFAAAAAA), 42, 53, 0);
        mTextValueDesc.append(sText);

        mTextSerial = (TextView) view.findViewById(R.id.text_logs);
        //-------------지연 위치구하기

        //-------------

        //----- Initialize processing core
        initialize();
        return view;
    }

    //initialize(): 액티비티 생성될 때 이 메서드를 호출함. 이 안에서 setupBT(), doStuff()가 호출되고, 디바이스와 BT연결이 정상적으로 되면 mHandler를 통해 데이터를 받을 수 있음.
    private void initialize() {
        mAnalyzer = new Analyzer();
        mSignalHolder = new SignalHolder();

        mEEGRawBuffer = new int[Constants.EEG_RAW_DATA_LEN];
        Arrays.fill(mEEGRawBuffer, 0);
        Arrays.fill(mSignalBuffer, 0);

        setupBT();        // get BT adapter and create TGDevice instance
        doStuff();        // connect TGDevice. If succeeded, Handler will receive CONNECTED message and call tgDevice.start()

    }

    private void initializeGraphics() {
        mRenderRaw = (RenderingView) getActivity().findViewById(R.id.layout_raw);
        mRenderValue = (RenderingView) getActivity().findViewById(R.id.layout_value);

        mRenderRaw.initializeGraphics();
        mRenderValue.initializeGraphics();
    }

    private void setupBT() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Alert user that Bluetooth is not available
            Toast.makeText(getActivity(), "Bluetooth not available", Toast.LENGTH_LONG).show();
            getActivity().finish();
            return;
        } else {
            /* create the TGDevice */
            tgDevice = new TGDevice(bluetoothAdapter, mHandler);
        }
    }

    private void initializeSerial() {
        if (mListener == null)
            mListener = new SerialListener();

        // Initialize Serial connector and starts Serial monitoring thread.
        if (mSerialConn == null) {
            mSerialConn = new SerialConnector(mContext, mListener, mHandler);
            mSerialConn.initialize();
        }
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
    public void onDestroy() {
        tgDevice.close();
        tgDevice = null;
        if (mSerialConn != null)
            mSerialConn.finalize();
        mSerialConn = null;

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_dialog2:
                //   onBtnAlarmJ1();
                Context mContext = getActivity().getApplicationContext();
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //R.layout.dialog??xml?뚯씪紐낆씠怨?R.id .popup? 蹂댁뿬以??덉씠?꾩썐?꾩씠??
                View layout = inflater.inflate(R.layout.dialog2, (ViewGroup) v.findViewById(R.id.popup2));
                AlertDialog.Builder aDialog = new AlertDialog.Builder(v.getContext());

                aDialog.setTitle("페이지 정보");//타이틀바 제목
                aDialog.setView(layout);//dialog.xml파일을 뷰로 셋팅

                //닫기버튼을 위한 부분
                aDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                //팝업창 생성
                AlertDialog ad = aDialog.create();
                ad.show();//보여줌
                break;
            case R.id.text_title_render1:
                mFreqDrawingMode++;
                if (mFreqDrawingMode > Constants.FREQ_DRAW_MODE_MAX)
                    mFreqDrawingMode = Constants.FREQ_DRAW_MODE_ALPHA_TO_GAMMA;
                break;
        }
    }

    private void doStuff() {
        if (tgDevice.getState() != TGDevice.STATE_CONNECTING
                && tgDevice.getState() != TGDevice.STATE_CONNECTED)
            tgDevice.connect(rawEnabled);
    }

    private void changeViewMode(int mode) {
        if (mode == Constants.VIEW_MODE_CONTROLLER) {
            if (mCurrentViewMode != mode) {
                mModeMonitoring.setVisibility(View.GONE);
                mModeController.setVisibility(View.VISIBLE);
                mCurrentViewMode = Constants.VIEW_MODE_CONTROLLER;


                initializeSerial();
            }
        } else if (mode == Constants.VIEW_MODE_MONITORING) {
            if (mCurrentViewMode != mode) {
                mModeMonitoring.setVisibility(View.VISIBLE);
                mModeController.setVisibility(View.GONE);

                mCurrentViewMode = Constants.VIEW_MODE_MONITORING;
            }
        }
    }

    private void setBackground(View v, boolean isEnabled) {
        if (isEnabled)
            v.setBackgroundColor(0xff0077aa);
        else
            v.setBackgroundColor(0xff999999);
    }


    private void sendMindSignal(int attention, int meditation, int blink) {
        Arrays.fill(mSignalBuffer, 0);

        // Do not send value 0. Aduino cannot receive 0x00.
        // 0 < value < 128
        if (attention > 0) {
            mSignalBuffer[0] = attention;
        } else {
            mSignalBuffer[0] = mSignalHolder.getLatestAttention();
        }

        if (meditation > 0) {
            mSignalBuffer[1] = meditation;
        } else {
            mSignalBuffer[1] = mSignalHolder.getLatestMeditation();
        }

        if (blink > 0) {
            mSignalBuffer[2] = blink;
        } else {
            mSignalBuffer[2] = mSignalHolder.getLatestBlink();
        }

        SerialCommand cmd = new SerialCommand();
        cmd.setCommand(Constants.SERIAL_CMD_MIND_SIGNAL, mSignalBuffer);
        mSerialConn.sendCommand(cmd);
    }

    private void sendMoveCommand(int command) {
        SerialCommand cmd = new SerialCommand();
        cmd.setCommand(Constants.SERIAL_CMD_MOVE, 1, command);        // Msg ID, index, command
        mSerialConn.sendCommand(cmd);
    }

    public void onBtnAlarmJ1() {
        player = new MediaPlayer();         // 객체생성

// TYPE_RINGTONE 을 하면 현재 설정되어 있는 밸소리를 가져온다.
// 만약 알람음을 가져오고 싶다면 TYPE_ALARM 을 이용하면 된다
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        try {
            // 이렇게 URI 객체를 그대로 삽입해줘야한다.
            //인터넷에서 url.toString() 으로 하는것이 보이는데 해보니까 안된다 -_-;
            player.setDataSource(context, alert);


            // 출력방식(재생시 사용할 방식)을 설정한다. STREAM_RING 은 외장 스피커로,
            // STREAM_VOICE_CALL 은 전화-수신 스피커를 사용한다.
            player.setAudioStreamType(AudioManager.STREAM_RING);

            player.setLooping(true);  // 반복여부 지정
            player.prepare();    // 실행전 준비
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.start();   // 실행 시작


/*
ScheduledJob job = new ScheduledJob();
        Timer jobScheduler = new Timer();
        jobScheduler.schedule(job,8000,6000);//알람끔
*/


    }

    public void onBtnAlarm1() {
//        // 기본 알람음 재생
//        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        MediaPlayer player = new MediaPlayer();
        try {
//            player.setDataSource(this, alert);
            player.setDataSource(context, alert);
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
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
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

//        //순순
//        new CountDownTimer(120,2) { //이 인자는 아직도 찾아봐도 뭔지 모르겠다.
//
//            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//                value++;
//                if (value % 5 == 0) {
//                    //noti 를 재생
//                    Notification noti = new Notification.Builder(context)
//                            .setContentTitle("일어나세요")
//                            .setContentText("여기서 자면 안 돼!")
//                            .setSmallIcon(R.drawable.logo)
//                            .setVibrate(new long[]{0, 5000})
//                            .build();
//                    NotificationManagerCompat nManager = NotificationManagerCompat.from(context);
//                    nManager.notify(BASIC_NOTIFICATION_ID, noti);
//                    if (value != -10) {
//                        nManager.cancel(0);
//                    }
//
//                }
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        }.start();
//        //순순
    }

    //순순
    public void onBtnAlarm2() {

        new CountDownTimer(120, 2) { //이 인자는 아직도 찾아봐도 뭔지 모르겠다.

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onTick(long millisUntilFinished) {

                value++;
                if (value % 5 == 0) {
                    //noti 를 재생
                    Notification noti = new Notification.Builder(context)
                            .setContentTitle("일어나세요")
                            .setContentText("여기서 자면 안 돼!")
                            .setSmallIcon(R.drawable.logo)
                            .setVibrate(new long[]{0, 5000})
                            .build();
                    NotificationManagerCompat nManager = NotificationManagerCompat.from(context);
                    nManager.notify(BASIC_NOTIFICATION_ID, noti);
                    if (value != -10) {
                        nManager.cancel(0);
                    }

                }
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    //순순
    public void onBtnAlarm3() {
        //발신자 전화번호를 유저 폰번호 추출로
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);

        Log.v(TAG, telephony.toString());
        String myNumber = telephony.getLine1Number();

        Log.v(TAG, myNumber);

        //발송후 사후처리 등록
        Intent sIntent = new Intent("ACTION_SMS");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, sIntent, 0);

        //sms발송
      /*  try{
            SmsManager sms = SmsManager.getDefault();
            String mymsg = "운전자가 졸고 있습니다. 깨워주세요";
            sms.sendTextMessage(phonenum,null,mymsg ,pendingIntent,null);
            Toast.makeText(getApplicationContext(),"메시지전송!",Toast.LENGTH_LONG).show();
        }catch(Exception e){
            Toast.makeText(getApplicationContext(),"메시지전송실패",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }*/
        String mymsg = "운전자가 졸고 있습니다. 깨워주세요";
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNum, myNumber, mymsg, pendingIntent, null);
        Toast.makeText(mContext, "메시지전송!", Toast.LENGTH_LONG).show();
    }


    //mHandler: 헤드셋에서 전달되는 커맨드를 처리. 모든 뇌파 데이터 처리가 여기서 시작.
    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case TGDevice.MSG_STATE_CHANGE:

                    // BT connection state
                    switch (msg.arg1) {
                        case TGDevice.STATE_IDLE:        // Initial state of the TGDevice. Not connected to a headset
                            mTextConn.setText(" _STATE_IDLE");
                            break;
                        case TGDevice.STATE_CONNECTING:    // Attempting a connection to the headset
                            mTextConn.setText(" 연결 중...");
                            break;
                        case TGDevice.STATE_CONNECTED:    // A valid device hand been found and data is being received
                            mTextConn.setText(" 연결 완료");

                            if (mRenderRaw == null || mRenderValue == null)
                                initializeGraphics();

                            tgDevice.start();
                            break;
                        case TGDevice.STATE_NOT_FOUND:    // Could not connect to headset
                            mTextConn.setText(" 검색 실패");
                            break;
                        case TGDevice.STATE_NOT_PAIRED:    // A valid headset could not be found
                            mTextConn.setText(" _STATE_NOT_PAIRED");

                            break;
                        case TGDevice.STATE_DISCONNECTED:    // connection to the device is lost
                            mTextConn.setText(" 연결 실패");
                            break;
                    }
                    break;

                // Raw data
                case TGDevice.MSG_RAW_COUNT:
                    /**
                     * Disabled

                     if(mEEGRawBufferIndex == Constants.EEG_RAW_DATA_LEN) {
                     Log.d(tag, "# Buffer full!!  Buffer size = "+(mEEGRawBufferIndex));
                     // Data full. Save array.
                     double[] copied = mSignalHolder.setEEGRawData(mEEGRawBuffer);
                     double[] ac = new double[copied.length];
                     Arrays.fill(ac, 0f);
                     // Analyze data
                     //mAnalyzer.realForwardFFT(copied);
                     // frequency = ((double)sampleRate / (double)FRAME_ELEMENT_COUNT) * freqIndex;

                     mAnalyzer.fftAutoCorrelation(copied, ac, true);		// raw data, auto correlation result, normalize or not

                     // Draw graph
                     mRenderRaw.drawRawGraph(ac);
                     mRenderRaw.invalidate();
                     } else {
                     // Some of raw data is lost.
                     Log.d(tag, "# We got invalid raw data count!!  Array size = "+(mEEGRawBufferIndex));
                     }

                     Arrays.fill(mEEGRawBuffer, 0);
                     mEEGRawBufferIndex = 0;
                     */
                    break;

                // TGDevice.MSG_RAW_DATA is Deprecated....
                case TGDevice.MSG_RAW_DATA:
                    // Log.d(tag, "Got raw: " + msg.arg1 + "\n");
//	            	if(mEEGRawBufferIndex < Constants.EEG_RAW_DATA_LEN) {
//	            		mEEGRawBuffer[mEEGRawBufferIndex] = msg.arg1;
//	            		mEEGRawBufferIndex++;
//	            	}
                    break;

                case TGDevice.MSG_EEG_POWER:
                    TGEegPower ep = (TGEegPower) msg.obj;
                    TGEegPower ep_normalized = new TGEegPower();
                    if (ep != null) {
                        mSignalHolder.setEEGBandData(ep);        // Remember

                        // Update view when it's monitoring mode
                        if (mCurrentViewMode == Constants.VIEW_MODE_MONITORING) {
                            // normalize data
                            double[] arrayTemp = new double[8];
                            arrayTemp[0] = ep.delta;
                            arrayTemp[1] = ep.theta;
                            arrayTemp[2] = ep.lowAlpha;
                            arrayTemp[3] = ep.highAlpha;
                            arrayTemp[4] = ep.lowBeta;
                            arrayTemp[5] = ep.highBeta;
                            arrayTemp[6] = ep.lowGamma;
                            arrayTemp[7] = ep.midGamma;

                            //mAnalyzer.normalization(arrayTemp);
                            ep_normalized.delta = (int) arrayTemp[0];
                            ep_normalized.theta = (int) arrayTemp[1];
                            ep_normalized.lowAlpha = (int) arrayTemp[2];
                            ep_normalized.highAlpha = (int) arrayTemp[3];
                            ep_normalized.lowBeta = (int) arrayTemp[4];
                            ep_normalized.highBeta = (int) arrayTemp[5];
                            ep_normalized.lowGamma = (int) arrayTemp[6];
                            ep_normalized.midGamma = (int) arrayTemp[7];

                            if (mFreqDrawingMode == Constants.FREQ_DRAW_MODE_ALPHA_TO_GAMMA)
                                mRenderRaw.drawRelativePowerGraph(ep_normalized);
                            else if (mFreqDrawingMode == Constants.FREQ_DRAW_MODE_ALL_BAND)
                                mRenderRaw.drawFreqBandGraph(ep_normalized);

                            mRenderRaw.invalidate();
                        }

                        ///알고리즘시작
                        if ((ep.highAlpha != 0) && (ep.lowAlpha != 0) && (ep.highBeta != 0) && (ep.lowBeta != 0)) {  //하이알파, 로우알파, 하이베타, 로우베타 수치가 0 이 아닐때
                            timeLeft++;
                        }
                        if (timeLeft == 1) {
                            Log.v(TAG, "수정된거임");

                        }
                        if (timeLeft >= 30 && timeLeft < 330) {//30초 이상 330초 이하일 때
                            ha300[halahblbHeadIndex300] = ep_normalized.highAlpha;
                            la300[halahblbHeadIndex300] = ep_normalized.lowAlpha;
                            hb300[halahblbHeadIndex300] = ep_normalized.highBeta;
                            lb300[halahblbHeadIndex300] = ep_normalized.lowBeta;
                            halahblbHeadIndex300++;
                            Log.v(TAG, "30초이상, 330초 미만이라 데이터를 쌓기만 하는중..");
                        } else if (timeLeft == 330) {//330초 때기준점을 잡는다(16000000이하의 수치가 몇회인지 기준점)
                            int toStdTempBindoHa = 0;
                            int toStdTempBindoLa = 0;
                            int toStdTempBindoHb = 0;
                            int toStdTempBindoLb = 0;

                            for (int i = 0; i < 300; i++) {
                                if (ha300[i] > 16000000) {
                                    toStdTempBindoHa++;
                                }
                                if (la300[i] > 16000000) {
                                    toStdTempBindoLa++;
                                }
                                if (hb300[i] > 16000000) {
                                    toStdTempBindoHb++;
                                }
                                if (lb300[i] > 16000000) {
                                    toStdTempBindoLb++;
                                }
                            }//for
                            standard_ha = toStdTempBindoHa;
                            Log.v(TAG, "standard_ha:" + standard_ha);
                            standard_la = toStdTempBindoLa;
                            Log.v(TAG, "standard_la:" + standard_la);
                            standard_hb = toStdTempBindoHb;
                            Log.v(TAG, "standard_hb:" + standard_hb);
                            standard_lb = toStdTempBindoLb;
                            Log.v(TAG, "standard_lb:" + standard_lb);
                            Log.v(TAG, "330초가 되어 standard값을 구하였습니다!");
                        } else if (timeLeft > 330) {  //330초 이후 졸음여부 측정
                            //30초이후에는 항상 이곳에 들어옴
                            if (wait30 == true) {//인덱스30이전으로수정, 데이터 쌓기만 하고 졸음검사는 안함
                                wait30Now++;//처음여기들어오면 1초부터 시작
                                Log.v(TAG, "wait30==true들어옴, wait30Now:" + wait30Now);
                                if (wait30Now == 1 && halahblbHeadIndex300 - 30 < 0) {//졸음이왔을때(wait30Now==1) 인덱스-30이음수이면 인덱스를30이전으로 하되 음수가 안되게 수정
                                    //halahblbHeadIndex300=halahblbHeadIndex300+299-29;
                                    halahblbHeadIndex300 = halahblbHeadIndex300 + 270;
                                    Log.v(TAG, "알람이울렸을 때의 인덱스-30이 음수여서 인덱스값조정. 인덱스:" + halahblbHeadIndex300);
                                } else if (wait30Now == 1 && halahblbHeadIndex300 - 30 >= 0) {//졸음이 왔을 때(wait30Now==1)인덱스를 30이전으로 해주기
                                    halahblbHeadIndex300 -= 30;
                                    Log.v(TAG, "알람이울렸을 때의 인덱스-30이 양수임. 인덱스값조정. 인덱스:" + halahblbHeadIndex300);
                                }
                                if (wait30Now == 31) {
                                    wait30 = false;
                                    Log.v(TAG, "알람울리고30초 기다리는거 끝남");
                                }
                            }//if(wait30==true)
                            if (halahblbHeadIndex300 == 300) {//다시 배열의 처음부터 채움
                                halahblbHeadIndex300 = 0;
                                Log.v(TAG, "300칸이 다 찼기에 다시 0번째 칸부터 시작!");
                            }
                            ha300[halahblbHeadIndex300] = ep_normalized.highAlpha;
                            la300[halahblbHeadIndex300] = ep_normalized.lowAlpha;
                            hb300[halahblbHeadIndex300] = ep_normalized.highBeta;
                            lb300[halahblbHeadIndex300] = ep_normalized.lowBeta;
                            Log.v(TAG, "[현재 넣은 halahblbHEadIndex300]:" + halahblbHeadIndex300);
                            halahblbHeadIndex300++;
                            //------------------------
                            /*if(timeLeft==380){
                                wait30=true;
                            }*/

                            //------------------------
                            if (wait30 == false) {//졸음여부측정가능

                                tempBindoHa = 0;
                                tempBindoLa = 0;
                                tempBindoHb = 0;
                                tempBindoLb = 0;

                                for (int i = 0; i < 300; i++) {
                                    if (ha300[i] > 16000000) {
                                        tempBindoHa++;
                                    }
                                    if (la300[i] > 16000000) {
                                        tempBindoLa++;
                                    }
                                    if (hb300[i] > 16000000) {
                                        tempBindoHb++;
                                    }
                                    if (lb300[i] > 16000000) {
                                        tempBindoLb++;
                                    }
                                }//for
                                Log.v(TAG, "졸음여부 측정하고 있습니다(wait30==false) standatd_ha*0.5738:" + standard_ha * 0.5738 + "tempbindoha:" + tempBindoHa);
                                Log.v(TAG, "std_la*0.55:" + standard_la * 0.55 + ",tempBindoLa" + tempBindoLa + "|std_hb*0.2959:" + standard_hb * 0.2959 + ",tempBindoHb: " + tempBindoHb);
                                Log.v(TAG, "|std_lb*0.3905:" + standard_lb * 0.3905 + "tempBindoLb:" + tempBindoLb);
                                //     Log.v(TAG,"player playing?"+String.valueOf(player.isPlaying()));
                                if ((player.isPlaying() == false) && ((standard_ha * 0.5738 > tempBindoHa) || (standard_la * 0.55 > tempBindoLa) || (standard_hb * 0.2959 > tempBindoHb) || (standard_lb * 0.3905 > tempBindoLb))) {
                                    //알람이 울리지 않고 있고, 그 외 4 가지 조건이 OR 조건으로 더 일치하면 조건문 안으로 들어와, 알람을 울린다.
                                    if (isPhoneAlarm.equals("true")) {
                                        onBtnAlarmJ1();

                                    }
                                    if (isSmartWatch.equals("true")) {
                                        onBtnAlarm2();
                                    }
                                    if (isOtherPerson.equals("true")) {
                                        onBtnAlarm3();
                                    }

                                    String tempstr = "!!!!!알람울림.현재시간의 Minute:" + new Date().getMinutes();//getMinites:현재시간에서 분만 가져옴
                                    Log.v(TAG, tempstr);

                                    wait30 = true;
                                    wait30Now = 0;//30초 기다리기 위해 현재는 0초임.

                                }//알람울리는if문
                            }//if(wait30==false)
                        }//else if(timeLeft>330)
                    }//if(ep!=null)

                    if (mSendMindSignal) {
                        int command = mSignalHolder.makeMoveCommand();
                        if (command > Constants.SERIAL_SUB_CMD_MOVE_NONE) {
                            sendMoveCommand(command);
                        }
                    }

                    //Log.d(tag, "MSG_EEG_POWER: " + ep.delta + ", " + ep.theta  + ", " + ep.lowAlpha  + ", " + ep.highAlpha  + ", " + ep.lowBeta  + ", " + ep.highBeta + ", " + ep.lowGamma + ", " + ep.midGamma);
                    break;

                // Additional data (Poor signal, Heart rate)
                case TGDevice.MSG_POOR_SIGNAL:
                    mSignalHolder.setPoorSignal(msg.arg1);

                    if (mCurrentViewMode == Constants.VIEW_MODE_MONITORING) {
                        mRenderValue.drawValueGraph(0, 0, 0, msg.arg1, 0);
                        mRenderValue.invalidate();
                    }
                    // Log.d(tag, "PoorSignal: " + msg.arg1 + "\n");
                    break;

                case TGDevice.MSG_HEART_RATE:
                    mSignalHolder.setHeartRate(msg.arg1);

                    if (mCurrentViewMode == Constants.VIEW_MODE_MONITORING) {
                        mRenderValue.drawValueGraph(0, 0, 0, 0, msg.arg1);
                        mRenderValue.invalidate();
                    }
                    // Log.d(tag, "Heart rate: " + msg.arg1 + "\n");
                    break;

                // Pre-processed data
                case TGDevice.MSG_ATTENTION:
                    mSignalHolder.setAttention(msg.arg1);

                    if (mCurrentViewMode == Constants.VIEW_MODE_MONITORING) {
                        mRenderValue.drawValueGraph(msg.arg1, 0, 0, 0, 0);
                        mRenderValue.invalidate();
                    }
//	            	if(mSendMindSignal) {
//	            		sendMindSignal(msg.arg1, 0, 0);
//	            	}
                    //Log.d(tag, "Attention: " + msg.arg1 + "\n");
                    break;

                case TGDevice.MSG_MEDITATION:
                    mSignalHolder.setMeditation(msg.arg1);

                    if (mCurrentViewMode == Constants.VIEW_MODE_MONITORING) {
                        mRenderValue.drawValueGraph(0, msg.arg1, 0, 0, 0);
                        mRenderValue.invalidate();
                    }
//	            	if(mSendMindSignal) {
//	            		sendMindSignal(0, msg.arg1, 0);
//	            	}
                    //Log.d(tag, "Meditation: " + msg.arg1 + "\n");
                    break;

                case TGDevice.MSG_BLINK:
                    mSignalHolder.setBlink(msg.arg1);
                    if (mCurrentViewMode == Constants.VIEW_MODE_MONITORING) {
                        mRenderValue.drawValueGraph(0, 0, msg.arg1, 0, 0);
                        mRenderValue.invalidate();

                        //순순
                        if (blinkTimeIndex == 5) {
                            for (int i = 0; i < 5; i++)
                                blinkTime[i] = 0;
                            blinkTimeIndex = 0;
                        }
                        blinkTime[blinkTimeIndex] = System.currentTimeMillis();
                        blinkTimeIndex++;
                        if ((blinkTime[1] - blinkTime[0] > 0) && (blinkTime[1] - blinkTime[0] < 1000) && (blinkTime[2] - blinkTime[1] > 0) && (blinkTime[2] - blinkTime[1] < 1000) && (blinkTime[3] - blinkTime[2] > 0) && (blinkTime[3] - blinkTime[2] < 1000) && (blinkTime[4] - blinkTime[3] > 0) && (blinkTime[4] - blinkTime[3] < 1000)) {

                            //워치 알람 끄기
                            value = -10;

                            // 소리 끄기
                            if (player.isPlaying()) {
                                player.stop();
                            }
                        }
                        //순순
                    }
//	            	if(mSendMindSignal) {
//	            		sendMindSignal(0, 0, msg.arg1);
//	            	}
                    //Log.d(tag, "Blink: " + msg.arg1 + "\n");
                    break;

                // System notice
                case TGDevice.MSG_LOW_BATTERY:
                    if (mCurrentViewMode == Constants.VIEW_MODE_MONITORING) {
                        //mTextSystem.setText("LOW_BATTERY");
                    }
                    // Toast.makeText(getApplicationContext(), "Low battery!", Toast.LENGTH_SHORT).show();
                    break;


                // Arduino serial message
                case Constants.MSG_DEVICD_INFO:
                    if (mCurrentViewMode == Constants.VIEW_MODE_CONTROLLER) {
                        mTextSerial.append((String) msg.obj);
                    }
                    break;
                case Constants.MSG_DEVICE_COUNT:
                    if (mCurrentViewMode == Constants.VIEW_MODE_CONTROLLER) {
                        mTextSerial.append(Integer.toString(msg.arg1) + " device(s) found \n");
                    }
                    break;
                case Constants.MSG_READ_DATA_COUNT:
                    if (mCurrentViewMode == Constants.VIEW_MODE_CONTROLLER) {
                        mTextSerial.append(Integer.toString(msg.arg1) + " buffer received \n");
                    }
                    break;
                case Constants.MSG_READ_DATA:
                    if (mCurrentViewMode == Constants.VIEW_MODE_CONTROLLER) {
                        SerialCommand cmd = null;
                        if (msg.obj != null) {
                            if (mSerialLogCount > SERIAL_LOG_COUNT_MAX) {
                                mTextSerial.setText("");
                                mSerialLogCount = 0;
                            }

                            cmd = (SerialCommand) msg.obj;
                            mTextSerial.append(cmd.toString());
                            mSerialLogCount++;
                        }
                    }
                    break;
                case Constants.MSG_SERIAL_ERROR:
                    mTextSerial.append((String) msg.obj);
                    break;


                default:
                    break;
            } // End of switch()
        } // End of handleMessage()
    };
    //sms 전송후 전달되는 sent ack에 의해 실행될 receiver
    BroadcastReceiver sentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String msg = "";
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    msg = "OK!";
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    msg = "generic error!";
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF://failed because radio is off
                    msg = "radio off!";
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    msg = "pdu error!";
                    break;

            }
            Toast t = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
            t.show();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        context.registerReceiver(sentReceiver, new IntentFilter("ACTION_SMS"));
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(sentReceiver);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public class SerialListener {
        public void onReceive(int msg, int arg0, int arg1, String arg2, Object arg3) {
            switch (msg) {
                case Constants.MSG_DEVICD_INFO:
                    if (mCurrentViewMode == Constants.VIEW_MODE_CONTROLLER) {
                        mTextSerial.append(arg2);
                    }
                    break;
                case Constants.MSG_DEVICE_COUNT:
                    if (mCurrentViewMode == Constants.VIEW_MODE_CONTROLLER) {
                        mTextSerial.append(Integer.toString(arg0) + " device(s) found \n");
                    }
                    break;
                case Constants.MSG_READ_DATA_COUNT:
                    if (mCurrentViewMode == Constants.VIEW_MODE_CONTROLLER) {
                        mTextSerial.append(Integer.toString(arg0) + " buffer received \n");
                    }
                    break;
                case Constants.MSG_READ_DATA:
                    if (mCurrentViewMode == Constants.VIEW_MODE_CONTROLLER) {
                        SerialCommand cmd = null;
                        if (arg3 != null) {
                            if (mSerialLogCount > SERIAL_LOG_COUNT_MAX) {
                                mTextSerial.setText("");
                                mSerialLogCount = 0;
                            }

                            cmd = (SerialCommand) arg3;
                            // mTextSerial.append(cmd.toString());

                            mSerialLogCount++;
                        }
                    }
                    break;
                case Constants.MSG_SERIAL_ERROR:
                    if (mCurrentViewMode == Constants.VIEW_MODE_CONTROLLER) {
                        mTextSerial.append(arg2);
                    }
                    break;
                case Constants.MSG_FATAL_ERROR_FINISH_APP:
                    getActivity().finish();
                    break;
            }
        }
    }
}
