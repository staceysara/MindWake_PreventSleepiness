package com.example.awake;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Splash2 extends AppCompatActivity {
    Button bt_splashNext2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);
      //  startActivity(new Intent(this, Splash1.class)); // 초가 로딩 후 메인화면 뿌려짐


   /*     new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();   //6 초 후 이미지를 닫아버림
            }
        }, 6000);*/
        bt_splashNext2 = (Button)findViewById(R.id.bt_splashNext2);
        bt_splashNext2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Splash2.this, Splash3.class);
                startActivity(intent);
                finish();
            }
        });

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
