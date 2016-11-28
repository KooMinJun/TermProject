package my.kmucs.com.koo_timer;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Koo on 2016-11-21.
 */

public class Fragment1 extends Fragment {


    TextView mEllapse;
    TextView mSplit;
    Button mBtnStart, mBtnSplit;

    //스톱워치의 상태를 위한 상수
    final static int IDLE = 0;
    final static int RUNNING = 1;
    final static int PAUSE = 2;
    int mStatus = IDLE; //처음상태는 IDLE
    long mBaseTime, mPauseTime, mSplitCount;

    //스톱워치를 위해 핸들러를 만든다.
    Handler mTimer = new Handler(){

        //핸들러는 기본적으로 handleMessage에서 처리한다.
        public void handleMessage(android.os.Message msg){
            //텍스트뷰를 수정해준다.
            mEllapse.setText(getEllapse());
            //메시지를 다시보낸다
            mTimer.sendEmptyMessage(0);//0은 메시지를 구분하기 위한것
        };
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment1, container, false);

        mEllapse = (TextView)rootView.findViewById(R.id.ellapse);
        mSplit = (TextView) rootView.findViewById(R.id.split);
        mBtnStart = (Button)rootView.findViewById(R.id.countUp);
        mBtnSplit = (Button)rootView.findViewById(R.id.countSplit);


        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(mStatus) {
                    //IDLE상태이면
                    case IDLE:
                        //현재 값을 세팅해주고
                        mBaseTime = SystemClock.elapsedRealtime();
                        //핸들러로 메시지를 보낸다.
                        mTimer.sendEmptyMessage(0);
                        //시작을 중지로 바꾸고
                        mBtnStart.setText("PAUSE");
                        //옆버튼의 Enable을 푼다음
                        mBtnSplit.setEnabled(true);
                        //상태를 RUNNING으로 바꾼다.
                        mStatus = RUNNING;
                        break;
                    case RUNNING:
                        //핸들러 메시지를 없애고
                        mTimer.removeMessages(0);
                        //멈춘시간을 파악
                        mPauseTime = SystemClock.elapsedRealtime();

                        //버튼 텍스트를 바꿔줌
                        mBtnStart.setText("START");
                        mBtnSplit.setText("RESET");
                        mStatus = PAUSE;//상태를 멈춤으로 표시
                        break;
                    //멈춤이면
                    case PAUSE:
                        //현재값 가져옴
                        long now = SystemClock.elapsedRealtime();
                        //베이스타임 = 베이스타임 + (now - mPauseTime)
                        //잠깐 스톱워치를 멈췄다가 다시 시작하면 기준점이 변하게 되므로
                        mBaseTime += (now - mPauseTime);

                        mTimer.sendEmptyMessage(0);
                        //텍스트 수정
                        mBtnStart.setText("PAUSE");
                        mBtnSplit.setText("REC");
                        mStatus = RUNNING;
                        break;
                }
            }
        });

        mBtnSplit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mStatus){
                    //RUNNING상태일떄
                    case RUNNING:
                        //기존의 값을 가져온 뒤 이어붙이기 위해서
                        String sSplit = mSplit.getText().toString();

                        //+연산자로 이어붙임
                        sSplit += String.format("%d => %s\n", mSplitCount, getEllapse());

                        //텍스트 뷰의 값을 바꿔줌
                        mSplit.setText(sSplit);
                        mSplitCount++;
                        break;
                    case PAUSE: //여기서는 초기화 버튼이 됨
                        //핸들러를 없애고
                        mTimer.removeMessages(0);

                        //처음상태로 원상복귀시킴
                        mBtnStart.setText("START");
                        mBtnSplit.setText("REC");
                        mEllapse.setText("00:00:00");
                        mStatus = IDLE;
                        mSplit.setText("");
                        mBtnSplit.setEnabled(false);
                        mSplitCount=0;
                        break;
                }
            }
        });





        return rootView;
    }




    @Override
    public void onDestroy() {
        mTimer.removeMessages(0);
        super.onDestroy();
    }

    String getEllapse(){
        long now = SystemClock.elapsedRealtime();
        long ell = now - mBaseTime; //현재시간과 지난 시간을 뺴서 ell값을 구하고
        //아래에서 포맷을 예쁘게 바꾼다음 리턴해준다.
        String sEll = String.format("%02d:%02d:%02d", ell/1000/60, (ell/1000)%60, (ell % 1000)/10);
        return sEll;
    }
}
