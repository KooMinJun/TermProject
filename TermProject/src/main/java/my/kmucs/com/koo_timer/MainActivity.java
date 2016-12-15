package my.kmucs.com.koo_timer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.icu.util.TimeZone;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    Double latitude;
    Double longitude;
    //위치 정보를 받을 리스너 생성
    GPSListener gpsListener = new GPSListener();
    long minTime = 5000; //1000 = 1초
    float minDistance = 5; //1미터
    int etcStr; //분류를 위한 변수
    TextView txtLocation;
    Intent mapIntent;



    Calendar cal;
    TimeZone timeZone;
    int year, month, day;
    String hour, minute, second;

    MyDB mydb;
    SQLiteDatabase sqlite;
    String sql;

    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;
    int field = 0x00000020;

    Button countUp;

    Fragment1 fragment1;
    Fragment2 fragment2;
    Fragment3 fragment3;
    TabLayout tabs;

    private BackPressCloseHandler backPressCloseHandler;

    //센서관련객체
    SensorManager sensorManager;
    Sensor sensor;

    TextView mEllapse;
    TextView mSplit;

    //스톱워치의 상태를 위한 상수
    int mStatus = 0;
    long mBaseTime, mPauseTime;
    long now;

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


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkDangerousPermissions();
        mapIntent = new Intent(this, MapsActivity.class);


        //데이터베이스 연결
        mydb = new MyDB(this);

        //화면관리하기위한 코드선언부
        try{
            field = PowerManager.class.getClass().getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
        } catch (Throwable ignored){

        }
        powerManager = (PowerManager)getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(field, getLocalClassName());


        //시스템서비스로부터 SensorManager 객체를 얻는다.
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        //Sensormanager를 이용해서 근접 센서 객체를 얻는다.
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        //뒤로가기 두번누르게 하는 객체
        backPressCloseHandler = new BackPressCloseHandler(this);

        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();

        tabs = (TabLayout)findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("달력"));
        tabs.addTab(tabs.newTab().setText("통계"));
        tabs.addTab(tabs.newTab().setText("메모"));

        mEllapse = (TextView)findViewById(R.id.ellapse);
        mSplit = (TextView)findViewById(R.id.split);
        countUp = (Button)findViewById(R.id.countUp);

        txtLocation = (TextView)findViewById(R.id.txt_location);

        printLocation();

        countUp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                switch (countUp.getText().toString()){
                    case "START":
                        now = mPauseTime= mBaseTime = SystemClock.elapsedRealtime();
                        buttonClickTimeStart();
                        countUp.setText("SAVE & RESET");
                        Toast.makeText(getApplicationContext(),"휴대폰 화면을 엎어놓으면 스톱워치가 시작합니다.\n휴대폰 화면을 다시 돌리면 스톱워치가 일시정지 합니다.",Toast.LENGTH_LONG).show();


                        break;

                    case "SAVE & RESET":
                        buttonClickTimeStop();

                        cal = new GregorianCalendar();
                        timeZone = TimeZone.getTimeZone("Asia/Seoul");
                        cal.setTimeZone(timeZone);

                        year = cal.get(Calendar.YEAR);
                        month = cal.get(Calendar.MONTH) + 1;
                        day = cal.get(Calendar.DAY_OF_MONTH);
                        hour = mEllapse.getText().toString().substring(0,2);
                        minute = mEllapse.getText().toString().substring(3,5);
                        second = mEllapse.getText().toString().substring(6,8);

                        sqlite = mydb.getWritableDatabase(); //읽기쓰기가능한속성
                        sql = "INSERT INTO timeRecord(year, month, day, hour, min, sec) VALUES('" +year+"', '"+month+"', '" +day+ "', '" +hour+"', '" +minute+ "','"+ second+"')";


                        Log.d("SQL : ", sql);
                        sqlite.execSQL(sql);

                        sqlite.close();

                        // 임의의 데이터값을 넣어서 체크해보려고
//                        sqlite = mydb.getWritableDatabase();
//                        for(int i = 1 ; i<=15 ; i++) {
//                            sql = "INSERT INTO timeRecord(year, month, day, hour, min, sec) VALUES('" + 2016 + "', '" + 12 + "', '" + i + "', '" + 0 + "', '" + (int)(Math.random()*10) + "','" + (int)(Math.random()*60)+ "')";
//                            Log.d("SQL : ", sql);
//                            sqlite.execSQL(sql);
//
//
//                        }
//                        sqlite.close();

                        Toast.makeText(getApplicationContext(), "데이터가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        mEllapse.setText("00:00:00");
                        countUp.setText("START");
                        break;
                }
            }
        });

        txtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapIntent.putExtra("lat", latitude);
                mapIntent.putExtra("lng", longitude);
                startActivity(mapIntent);

            }
        });




        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.d("MainActivity", "선택된 탭 : " + position);

                Fragment selected = null;
                if(position == 0){
                    selected = fragment1;
                }
                else if(position == 1){
                    selected = fragment2;
                }
                else if(position == 2){
                    selected = fragment3;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.d("MainActivity", "선택된 탭 : " + position);

                Fragment selected = null;
                if(position == 0){
                    selected = fragment1;
                }
                else if(position == 1){
                    selected = fragment2;
                }
                else if(position == 2){
                    selected = fragment3;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
            }
        });
    }

    private void printLocation() {
        LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try{
            //gps를 이용한 위치 요청(주기적으로)
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance,gpsListener);
            //네트워크를 이용한 위치 요청(주기적으로)
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);
            //위치 확인이 안되는 경우에도 최근에 확인된 위치 정보 먼저 확인
            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastLocation != null){
                latitude = lastLocation.getLatitude();
                longitude = lastLocation.getLongitude();

                txtLocation.setText("\n현재위치 : " + getAddress(getApplicationContext(),latitude,longitude));

            }
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    //위치권한확인
    private void checkDangerousPermissions(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for(int i=0 ; i< permissions.length; i++){
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if(permissionCheck == PackageManager.PERMISSION_DENIED){
                break;
            }
        }
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,permissions[0])){
                Toast.makeText(this,"권한 설명 필요함.", Toast.LENGTH_LONG).show();
            }else{
                ActivityCompat.requestPermissions(this,permissions,1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == 1){
            for(int i=0 ; i<permissions.length ; i++){
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, permissions[i] + "권한이 승인됨.", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(this,permissions[i] + "권한이 승인되지 않음.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //위도 경도로 주소구하기
    public String getAddress(Context mContext, double lat, double lng){
        String nowAddress = "현재 위치를 확인할 수 없습니다.";
        Geocoder geocoder = new Geocoder(mContext, Locale.KOREA);
        List<Address> address;
        try{
            if(geocoder != null){
                //세번쨰 파라미터는 좌표에 대해 주소를 리턴받는 개수
                //한 좌표에 대해 두개이상의 이름의 존재할 수 있기에 주소배열을 리턴받기위해 최대갯수 설정
                address = geocoder.getFromLocation(lat, lng, 1);

                if(address != null && address.size() > 0){
                    //주소 받아오기
                    String currentLocationAddress = address.get(0).getAddressLine(0).toString();
                    nowAddress = currentLocationAddress;
                }
            }
        }catch (IOException e){
            Toast.makeText(getBaseContext(), "주소를 가져올 수 없습니다.",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return nowAddress;
    }




    public void buttonClickTimeStart(){
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void buttonClickTimeStop(){
        //센서 값이 필요하지 않는 시점에 리스너를 해제해준다.
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    //액티비티 나갈때
    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            //몇몇 기기의 경우 accuracy가 SENSOR_STATUS_UNRELIABLE 값을
            //가져서 측정값을 사용하지 못하는 경우가 있기 때문에 임의로 return;을 막는다
            //return ;
        }

        //센서값을 측정한 센서의 종류가 근접 센서인 경우
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] >= -0.01 && event.values[0] <= 0.01) {
                //near

                Toast.makeText(getApplicationContext(), "near", Toast.LENGTH_SHORT).show();
                //현재값 가져옴
                now = SystemClock.elapsedRealtime();
                //베이스타임 = 베이스타임 + (now - mPauseTime)
                //잠깐 스톱워치를 멈췄다가 다시 시작하면 기준점이 변하게 되므로
                mBaseTime += (now - mPauseTime);
                mTimer.sendEmptyMessage(0);
                if(!wakeLock.isHeld()){
                    wakeLock.acquire();
                }

            } else {

                mTimer.removeMessages(0);
                mPauseTime = SystemClock.elapsedRealtime();
                mStatus = 3;
                //far
                Toast.makeText(getApplicationContext(), "far", Toast.LENGTH_SHORT).show();
                if(wakeLock.isHeld()){
                    wakeLock.release();
                }
            }
        }
    }
    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    String getEllapse(){
        long now = SystemClock.elapsedRealtime();
        long ell = now - mBaseTime; //현재시간과 지난 시간을 뺴서 ell값을 구하고
        //아래에서 포맷을 예쁘게 바꾼다음 리턴해준다.
        // millsec : (ell % 1000) / 10
        // sec : (ell/1000)%60
        // min :

        String sEll = String.format("%02d:%02d:%02d", (ell/(1000*60*60)) % 24, (ell/(1000*60))%60, (ell/1000)%60);
        return sEll;
    }

    //정확도 변경시 호출되는 메소드. 센서의 경우 거의호출되지 않는다.
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class GPSListener implements LocationListener {
        //위치 정보가 확인될때 자동 호출되는 메소드
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            String msg = "\n위도 : " + latitude + "\n경도 : " + longitude;
            Log.i("GPSListener", msg);


            Toast.makeText(getApplicationContext(), "위치정보가 업데이트되었습니다. " , Toast.LENGTH_SHORT).show();

            txtLocation.setText("\n현재위치 : " + getAddress(getApplicationContext(),latitude,longitude));


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}