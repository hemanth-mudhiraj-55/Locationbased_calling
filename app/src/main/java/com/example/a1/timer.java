package com.example.a1;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class timer extends AppCompatActivity {

    private TextView min,sec;
    private CountDownTimer countDownTimer;
    private final long startTime = 5 * 60 * 1000; // 2 minutes in milliseconds
    private final long interval = 1000; // 1 second interval
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        startTimer();

    }


    public void setMin(TextView min) {
        this.min = min;
    }

    public void setSec(TextView sec) {
        this.sec = sec;
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(startTime, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String time = String.format(Locale.getDefault(),"%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)%60);


                        final String[] timerr= time.split(":");
                        min.setText(timerr[0]);
                        sec.setText(timerr[1]);
                    }
                });
            }

            @Override
            public void onFinish() {

            }
        }.start(); // Start the timer
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel the timer to avoid memory leaks
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void resetTimer(TextView eMin, TextView eSec) {
    }
}
