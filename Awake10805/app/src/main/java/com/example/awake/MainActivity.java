package com.example.awake;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    Button bt_newReg;
    Button bt_login;
    EditText et_loginID;
    EditText et_loginPW;
    ProgressDialog dialog;

    void doAction1() {
        dialog = ProgressDialog.show(MainActivity.this, "", "로그인중입니다");//context/title/message
        new LoginThread().start();

    }

    void doAction2() {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    View.OnClickListener bhandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_login:
                    doAction1();
                    break;
                case R.id.bt_newReg:
                    doAction2();
                    break;
            }
        }
    };
    String data = "";

    class LoginThread extends Thread {
        public void run() {
            String url = ServerUtil.SERVER_URL + "member";
            //private NameValuePair temp = new BasicNameValuePair("first",1);//fist=1이런 식. key=value
            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
            nameValue.add(new BasicNameValuePair("action", "login"));
            nameValue.add(new BasicNameValuePair("id", et_loginID.getText().toString()));
            nameValue.add(new BasicNameValuePair("pw", et_loginPW.getText().toString()));

            HttpClient client = NetManager1.getHttpClient();
            HttpPost post = NetManager1.getPost(url);

            HttpResponse response = null;
            BufferedReader br = null;
            StringBuffer sb = null;

            String line = "";
            try {
                Log.v(TAG, "process");
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValue);
                post.setEntity(entity);
                response = client.execute(post);
                int code = response.getStatusLine().getStatusCode();
                Log.v(TAG, "process code:" + code);
                switch (code) {
                    case 200:
                        br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                        sb = new StringBuffer();
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }
                        Log.v(TAG, "process msg:" + sb);
                        data = sb.toString();
                        Log.v(TAG,"MainActivity에서의 data:"+data);
                        handler.sendEmptyMessage(0);
                        break;
                    default:
                        handler.sendEmptyMessage(1);


                }
            } catch (Exception e) {
                Log.v(TAG, "login parser error:" + e);
                handler.sendEmptyMessage(2);
            } finally {
                try {
                    br.close();
                } catch (Exception e) {
                }


            }
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            switch (msg.what) {
                case 0:
                    if (doCheck()) {
                        Intent intent = new Intent(MainActivity.this, AlarmSelect.class);
                        intent.putExtra("id", data);
                        startActivity(intent);
                        finish();
                    } else {
                        showToast("아이디 암호가 다릅니다. 다시 시도하세요");
                    }
                    break;

            }
        }
    };

    void showToast(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
        et_loginID.setText("");
        et_loginPW.setText("");
    }

    boolean doCheck() {
        if (data.indexOf("fail") > -1) {
            return false;
        } else {
            return true;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //startActivity(new Intent(this, Splash3.class)); //9 초가 로딩 후 메인화면 뿌려짐
        startActivity(new Intent(this, Splash1.class));
        bt_newReg = (Button) findViewById(R.id.bt_newReg);
        bt_login = (Button) findViewById(R.id.bt_login);
        et_loginID = (EditText) findViewById(R.id.et_loginID);
        et_loginPW = (EditText) findViewById(R.id.et_loginPW);
        bt_login.setOnClickListener(bhandler);
        bt_newReg.setOnClickListener(bhandler);
    }
}
