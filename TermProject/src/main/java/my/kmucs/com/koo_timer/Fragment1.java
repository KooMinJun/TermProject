package my.kmucs.com.koo_timer;

/**
 * Created by Koo on 2016-12-01.
 */

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

/**
 * Created by Koo on 2016-11-21.
 */

public class Fragment1 extends Fragment {
    CalendarView calendar;
    MyDB myDB;
    SQLiteDatabase sqlite;
    Cursor cursor;
    String sql;
    int totalhour =0, totalmin =0 , totalsec = 0;
    TextView textCheckTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment1, container, false);
        calendar = (CalendarView)rootView.findViewById(R.id.calendar);
        myDB = new MyDB(getContext());
        textCheckTime = (TextView)rootView.findViewById(R.id.txt_checktime);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                sqlite = myDB.getReadableDatabase(); //읽기 전용
                sql = "SELECT * FROM timeRecord WHERE year="+year+" and month="+(month+1) + " and day=" + dayOfMonth;
                Log.e("SEEEEE", sql);
                cursor = sqlite.rawQuery(sql, null);

                while(cursor.moveToNext()){ //데이터베이스에서 일수마다의 총 시간을 저장
                    totalhour += Integer.parseInt(cursor.getString(4));
                    totalmin += Integer.parseInt(cursor.getString(5));
                    totalsec += Integer.parseInt(cursor.getString(6));

                }

                while(totalsec>=60){ //초가 60초넘어가면 60초뺴주고 1분 더해줌
                    totalsec -= 60;
                    totalmin += 1;
                }
                while (totalmin>=60){ //분이 60분 넘어가면 60분 뺴주고 1시간 더해줌
                    totalmin -= 60;
                    totalhour += 1;
                }

                textCheckTime.setText("총 기록된 시간은 " + totalhour + "시간 " + totalmin + "분 "+ totalsec+"초 "+ "입니다.");
                totalhour = 0;
                totalmin = 0;
                totalsec = 0;

            }
        });
        return rootView;
    }
}
