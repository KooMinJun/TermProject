package my.kmucs.com.koo_timer;

import android.content.Context;
import android.graphics.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Koo on 2016-11-21.
 */

public class Fragment1 extends Fragment implements SensorEventListener {

    //센서관련객체
    SensorManager sensorManager;
    Sensor sensor;

    Camera mCamera = null;


    TextView mEllapse;
    TextView mSplit;
    Button mBtnStart, mBtnSplit;

    //스톱워치의 상태를 위한 상수
    final static int IDLE = 0;
    final static int RUNNING = 1;
    final static int PAUSE = 2;
    int mStatus = IDLE;
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
//
        mEllapse = (TextView)rootView.findViewById(R.id.ellapse);
        mSplit = (TextView) rootView.findViewById(R.id.split);
        mBtnStart = (Button)rootView.findViewById(R.id.countUp);
        mBtnSplit = (Button)rootView.findViewById(R.id.countSplit);


        //시스템서비스로부터 SensorManager 객체를 얻는다.
        sensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        //Sensormanager를 이용해서 근접 센서 객체를 얻는다.
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
//
//
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStatus = PAUSE;
            }
        });



        return rootView;

    }


    //해당 액티비티가 시작되면 근접 데이터를 얻을 수 있도록 리스너를 등록한다.
    @Override
    public void onStart() {
        super.onStart();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }


    //해당 액티비티가 멈추면 근접 데이터를 얻어도 소용이 없으므로 리스너를 헤제한다.
    @Override
    public void onStop() {
        super.onStop();


        //센서 값이 필요하지 않는 시점에 리스너를 해제해준다.
        sensorManager.unregisterListener(this);
        mStatus = IDLE;
    }

    //    @Override
//    public void onDestroy() {
//        mTimer.removeMessages(0);
//        super.onDestroy();
//    }


    //측정한 값을 전달해주는 메소드
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE){
            //몇몇 기기의 경우 accuracy가 SENSOR_STATUS_UNRELIABLE 값을
            //가져서 측정값을 사용하지 못하는 경우가 있기 때문에 임의로 return;을 막는다
            //return ;
        }

        //센서값을 측정한 센서의 종류가 근접 센서인 경우
        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            switch (mStatus){
                case PAUSE:
                    mBaseTime = SystemClock.elapsedRealtime();
                    mTimer.sendEmptyMessage(0);
                    mStatus = RUNNING;
                    break;
                case RUNNING:
                    mTimer.removeMessages(0);
                    mPauseTime = SystemClock.elapsedRealtime();
                    mStatus = 3;
                    break;
                case 3:
                    //현재값 가져옴
                    long now = SystemClock.elapsedRealtime();
                    //베이스타임 = 베이스타임 + (now - mPauseTime)
                    //잠깐 스톱워치를 멈췄다가 다시 시작하면 기준점이 변하게 되므로
                    mBaseTime += (now - mPauseTime);

                    mTimer.sendEmptyMessage(0);
                    mStatus = RUNNING;
                    break;


            }
        }



//                Toast.makeText(getActivity(),"가깝다",Toast.LENGTH_SHORT).show();
//                switch(mStatus) {
////                    //IDLE상태이면
//                    case IDLE:
//                        //현재 값을 세팅해주고
//                        mBaseTime = SystemClock.elapsedRealtime();
//                        //핸들러로 메시지를 보낸다.
//                        mTimer.sendEmptyMessage(0);
//                        //시작을 중지로 바꾸고
//                        mBtnStart.setText("PAUSE");
//                        //옆버튼의 Enable을 푼다음
//                        mBtnSplit.setEnabled(true);
//                        //상태를 RUNNING으로 바꾼다.
//                        mStatus = RUNNING;
//                        break;
//                    case RUNNING:
//                        //핸들러 메시지를 없애고
//                        mTimer.removeMessages(0);
//                        //멈춘시간을 파악
//                        mPauseTime = SystemClock.elapsedRealtime();
//
//                        //버튼 텍스트를 바꿔줌
//                        mBtnStart.setText("START");
//                        mBtnSplit.setText("RESET");
//                        mStatus = PAUSE;//상태를 멈춤으로 표시
//                        break;
//                    //멈춤이면
//                    case PAUSE:
//                        //현재값 가져옴
//                        long now = SystemClock.elapsedRealtime();
//                        //베이스타임 = 베이스타임 + (now - mPauseTime)
//                        //잠깐 스톱워치를 멈췄다가 다시 시작하면 기준점이 변하게 되므로
//                        mBaseTime += (now - mPauseTime);
//
//                        mTimer.sendEmptyMessage(0);
//                        //텍스트 수정
//                        mBtnStart.setText("PAUSE");
//                        mBtnSplit.setText("REC");
//                        mStatus = RUNNING;
//                        break;
//                }
//
    }

    //정확도 변경시 호출되는 메소드. 센서의 경우 거의 호출되지 않는다.
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    String getEllapse(){
        long now = SystemClock.elapsedRealtime();
        long ell = now - mBaseTime; //현재시간과 지난 시간을 뺴서 ell값을 구하고
        //아래에서 포맷을 예쁘게 바꾼다음 리턴해준다.
        String sEll = String.format("%02d:%02d:%02d", ell/1000/60, (ell/1000)%60, (ell % 1000)/10);
        return sEll;
    }


    /////////////////////////////////////////////


}
