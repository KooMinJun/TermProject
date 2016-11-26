package my.kmucs.com.koo_timer;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.bcgdv.asia.lib.ticktock.TickTockView;

import java.util.Calendar;

/**
 * Created by Koo on 2016-11-23.
 */

public class CountDownActivity extends Activity {
    Intent i, getIntent;
    int sec;
    int secInt;
    TickTockView mCountDown;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);
        mCountDown = (TickTockView)findViewById(R.id.view_ticktock_countdown);



        getIntent = getIntent();
        sec = getIntent.getIntExtra("sec",11);

        if(mCountDown != null){
            mCountDown.setOnTickListener(new TickTockView.OnTickListener() {
                @Override
                public String getText(long timeRemainingInMillis) {
                    int seconds = (int) (timeRemainingInMillis / 1000) % 60;
                    int minutes = (int) ((timeRemainingInMillis / (1000 * 60)) % 60);
                    int hours = (int) ((timeRemainingInMillis / (1000 * 60 * 60)) % 24);
                    int days = (int) (timeRemainingInMillis / (1000 * 60 * 60 * 24));
                    boolean hasDays = days > 0;
                    return String.format("%1$02d%4$s %2$02d%5$s %3$02d%6$s",
                            hasDays ? days : hours,
                            hasDays ? hours : minutes,
                            hasDays ? minutes : seconds,
                            hasDays ? "d" : "h",
                            hasDays ? "h" : "m",
                            hasDays ? "m" : "s");
                }
            });
        }




    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart() {
        super.onStart();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.MINUTE, 11);
        end.add(Calendar.SECOND, sec);

        Calendar start = Calendar.getInstance();
        start.add(Calendar.MINUTE, -1);
        if (mCountDown != null) {
            mCountDown.start(start, end);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mCountDown.stop();
    }
}
