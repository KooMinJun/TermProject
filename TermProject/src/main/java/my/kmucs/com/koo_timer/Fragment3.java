package my.kmucs.com.koo_timer;

/**
 * Created by Koo on 2016-12-01.
 */

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.icu.util.TimeZone;
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
import android.widget.Toast;

/**
 * Created by Koo on 2016-11-21.
 */

public class Fragment3 extends Fragment {
    MytextDB myTextDB;
    SQLiteDatabase sqlite;
    String sql;

    Calendar cal;
    TimeZone timeZone;
    int year, month, day, hour, min, sec;

    EditText edtTitle, edtBody;
    Button btnSave, btnView;
    String titleStr, bodyStr;

    Intent i;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment3, container, false);

        edtTitle = (EditText) rootView.findViewById(R.id.editTitle);
        edtBody = (EditText)rootView.findViewById(R.id.editBody);
        btnSave = (Button)rootView.findViewById(R.id.memoSaveBtn);
        btnView = (Button)rootView.findViewById(R.id.memoListBtn);

        i = new Intent(getActivity(), MemoListViewActivity.class);

        myTextDB = new MytextDB(getContext());


        cal = new GregorianCalendar();
        timeZone = TimeZone.getTimeZone("Asia/Seoul");
        cal.setTimeZone(timeZone);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                titleStr = edtTitle.getText().toString();
                bodyStr = edtBody.getText().toString();


                if(titleStr.equalsIgnoreCase("")){
                    Toast.makeText(getContext(), "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                }

                else if(bodyStr.equalsIgnoreCase("")){
                    Toast.makeText(getContext(), "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                }

                else {

                    sqlite = myTextDB.getWritableDatabase();
                    year = cal.get(Calendar.YEAR);
                    month = cal.get(Calendar.MONTH) + 1;
                    day = cal.get(Calendar.DAY_OF_MONTH);
                    hour = cal.get(Calendar.HOUR);
                    min = cal.get(Calendar.MINUTE);
                    sec = cal.get(Calendar.SECOND);

                    sql = "INSERT INTO MemoTable(year, month, day, hour, min, sec, title, body) VALUES('" + year + "', '" + month + "', '" + day + "', '" + hour + "', '" + min + "','" + sec + "', '" + titleStr + "', '" + bodyStr + "')";
                    sqlite.execSQL(sql);

                    sqlite.close();

                    Toast.makeText(getContext(), "데이터가 저장되었습니다.", Toast.LENGTH_SHORT).show();

                    edtTitle.setText("");
                    edtBody.setText("");
                }



            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(i);
            }
        });




        return rootView;

    }

    @Override
    public void onPause() {
        super.onPause();
        edtTitle.setText("");
        edtBody.setText("");
        //다른탭이동하고 오면 사라지게 만듦


    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}





