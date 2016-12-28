package my.kmucs.com.koo_timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Koo on 2016-12-17.
 */

public class MemoListViewActivity extends Activity{

    ListView listView;
    MytextDB mytextDB;
    SQLiteDatabase sqlite;
    String sql;
    Cursor cursor;
    Intent i;
    MemoAdapter memoAdapter;

    //ListView 넣으려고
    ArrayList<String> arrayList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memolistview);

        listView = (ListView)findViewById(R.id.listmemo);
        i = new Intent(getApplicationContext(), MemoDetailListViewActivity.class);
        
        //데이터베이스 연결
        mytextDB = new MytextDB(this);
        
        //arrlist에 데이터베이스 파일들을 읽어서 대입, 데이터 셋팅
        getMemoForCursorAdapter();
        
        //listview 클릭시 이벤트 구현
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int _id = (int)view.getTag();


                //팝업창
                AlertDialog.Builder alertDlg = new AlertDialog.Builder(MemoListViewActivity.this); //여기서 뜨게하겠다
                alertDlg.setTitle(R.string.memo_alert_title_question);
                alertDlg.setMessage(R.string.memo_alert_msg);

                //positive버튼 삭제
                alertDlg.setPositiveButton(R.string.memo_alert_button_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteInfo(_id);
                        dialog.dismiss();
                        refresh();
                    }
                });

                //negative버튼 수정
                alertDlg.setNegativeButton(R.string.memo_alert_button_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        i.putExtra("_id", _id);

                        startActivityForResult(i,1);
                        dialog.dismiss();
                    }
                });

                alertDlg.show();
            }
        });
    }

    //편집화면에서 삭제버튼 누를때 사용할 함수
    public void deleteInfo(int _id){
        sqlite = mytextDB.getWritableDatabase();
        sql = "DELETE FROM MemoTable WHERE _id=" + _id;
        sqlite.execSQL(sql);
        sqlite.close();
        Toast.makeText(getApplicationContext(), "데이터가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
    }

    public void refresh(){
        getMemoForCursorAdapter();
    }

    private void getMemoForCursorAdapter() {
        //데이터베이스 열고
        sqlite = mytextDB.getReadableDatabase();
        sql = "SELECT * FROM MemoTable";

        cursor = sqlite.rawQuery(sql, null);

        if(cursor.getCount() > 0){
            startManagingCursor(cursor);
            memoAdapter = new MemoAdapter(this, cursor);
            listView.setAdapter(memoAdapter);
        }
    }

    //어플 종료시 생명주기 끝내려고고
    @Override
   protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        sqlite.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //startActivityForResult 사용 시 requestCode로 분기
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                if(data.getBooleanExtra("updateResult", false)){
                    //updateResult에 값이 안담겨올 경우 false이다.
                    Log.d("update >>>>> ", "성공");
                }
                else{
                    Log.d("update >>>>> ", "실패");
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
