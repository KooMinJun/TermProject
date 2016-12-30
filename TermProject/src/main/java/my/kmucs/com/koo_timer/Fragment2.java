package my.kmucs.com.koo_timer;

/**
 * Created by Koo on 2016-12-01.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Koo on 2016-11-21.
 */

public class Fragment2 extends Fragment {

    MyDB mydb;
    SQLiteDatabase sqlite;

    Button btn_statistic, btn_all_reset;
    Intent i;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment2, container, false);

        mydb= new MyDB(getContext());

        btn_all_reset = (Button)rootView.findViewById(R.id.all_data_reset_btn);
        btn_statistic = (Button)rootView.findViewById(R.id.statistic);
        i = new Intent(getActivity(), StatisticActivity02.class);

        btn_statistic.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(i);
            }
        });
        btn_all_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDlg = new AlertDialog.Builder(getActivity());
                alertDlg.setTitle(R.string.fragment2_save_alert_title_question);
                alertDlg.setMessage(R.string.fragment2_save_alert_msg_delete);

                //positive버튼
                alertDlg.setPositiveButton(R.string.fragment2_save_alert_button_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sqlite = mydb.getWritableDatabase(); //읽기 쓰기가 가능한 속성

                        mydb.onUpgrade(sqlite,1,2);
                        sqlite.close();
                        dialog.dismiss();
                        Toast.makeText(getContext(),"삭제되었습니다.",Toast.LENGTH_SHORT).show();
                    }
                });

                alertDlg.setNegativeButton(R.string.fragment2_save_alert_button_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDlg.show();

            }
        });

        return rootView;
    }
}