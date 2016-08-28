package com.example.awake;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

public class Splash1 extends AppCompatActivity {
public final static String TAG = "Splash1Activity";
//    Handler hd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash1);
        Log.v(TAG, "splash1에 들어옴. 3초간 보여질거임.");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();   //3 초 후 이미지를 닫아버림
                startActivity(new Intent(Splash1.this, Splash2.class));
            }
        }, 3000);

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
}
