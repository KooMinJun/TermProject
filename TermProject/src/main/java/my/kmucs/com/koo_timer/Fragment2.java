package my.kmucs.com.koo_timer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bcgdv.asia.lib.ticktock.TickTockView;

import java.util.Calendar;

/**
 * Created by Koo on 2016-11-21.
 */

public class Fragment2 extends Fragment {
    Intent i;
    Button countDown;
    EditText edtSec, edtHour, edtMin;
    int sec, hour, min;
    Bundle bundle;
    TickTockView mCountDown;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment2, container, false);


        countDown = (Button)rootView.findViewById(R.id.countDown);
        edtHour = (EditText)rootView.findViewById(R.id.edtHour);
        edtMin = (EditText)rootView.findViewById(R.id.edtMin);
        edtSec = (EditText)rootView.findViewById(R.id.edtSec);

        bundle = new Bundle();
        mCountDown = (TickTockView)rootView.findViewById(R.id.view_ticktock_countdown);

        countDown.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if(edtHour.getText().toString().matches("")){
                    hour = 0;
                }
                else{
                    hour = Integer.parseInt(edtHour.getText().toString());
                }

                if(edtMin.getText().toString().matches("")){
                    min = 0;
                }
                else{
                    min = Integer.parseInt(edtMin.getText().toString());
                }
                if(edtSec.getText().toString().matches("")){
                    sec=0;
                }
                else{
                    sec = Integer.parseInt(edtSec.getText().toString());
                }
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

                starttime(hour, min, sec);


            }

        });

        return rootView;
    }

    public void starttime(int hour, int min, int sec){
        Calendar end = Calendar.getInstance();
        end.add(Calendar.HOUR,hour);
        end.add(Calendar.MINUTE, min);
        end.add(Calendar.SECOND, sec);

        Calendar start = Calendar.getInstance();
        start.add(Calendar.MINUTE, -1);
        if (mCountDown != null) {
            mCountDown.start(start, end);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        mCountDown.stop();
    }

}