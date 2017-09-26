package com.eeng.sms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.eeng.sms.R;

/**
 * Created by dsi01 on 05/03/2017.
 */

public class WelcomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);

        initComponent();
    }

    private void initComponent() {
        new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Intent intent = new Intent(WelcomeScreenActivity.this, SendSMSActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }
}
