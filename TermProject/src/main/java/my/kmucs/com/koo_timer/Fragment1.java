package my.kmucs.com.koo_timer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bcgdv.asia.lib.ticktock.TickTockView;

/**
 * Created by Koo on 2016-11-21.
 */

public class Fragment1 extends Fragment {
    Intent i;
    Button countUp, countRec;
    TickTockView mCountUp;

    final static int INIT = 0;
    final static int RUN = 1;
    final static int PAUSE = 2;

    int curStatus = INIT; //현재의 상태를 저장할 변수를 초기화함
    int myCount = 1;
    long myBaseTime;
    long myPauseTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment1, container, false);

        countUp = (Button)rootView.findViewById(R.id.countUp);
        countRec = (Button)rootView.findViewById(R.id.countRec);
        mCountUp = (TickTockView)rootView.findViewById(R.id.view_ticktock_countup);
        //i = new Intent(getActivity(), CountUpActivity.class);
        countUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return rootView;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
