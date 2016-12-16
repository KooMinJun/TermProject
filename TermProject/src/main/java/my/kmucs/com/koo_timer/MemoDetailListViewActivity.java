package my.kmucs.com.koo_timer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Koo on 2016-12-17.
 */

public class MemoDetailListViewActivity extends Activity{

    MytextDB mytextDB;
    SQLiteDatabase sqlite;
    Cursor cursor;
    EditText titleEdit, bodyEdit;
    Button modifyBtn, stopBtn;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memodetail);

        mytextDB = new MytextDB(this);
        modifyBtn = (Button)findViewById(R.id.modifyBtn);
        stopBtn = (Button)findViewById(R.id.stopBtn);
        titleEdit = (EditText)findViewById(R.id.detailText01);
        bodyEdit = (EditText)findViewById(R.id.detailText02);

        i = getIntent();
        final int _id = i.getExtras().getInt("_id");

        // 단건조회
        getInfoForCursorAdapter(_id);

        //데이터 수정
        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = updateInfo(_id);

                //받아온 것들을 다시 돌려준다. 그리고 종료
                i.putExtra("updateResult", flag);
                setResult(RESULT_OK, i);
                finish();
            }
        });

        //인텐트 종료
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean updateInfo(int _id) {
        String titleStr = titleEdit.getText().toString();
        String bodyStr = bodyEdit.getText().toString();
        String sql1 = "UPDATE MemoTable SET title='" + titleStr + "' WHERE _id='" + _id +"'";
        String sql2 = "UPDATE MemoTable SET body='" + bodyStr + "' WHERE _id='" + _id +"'";

        try{
            sqlite = mytextDB.getWritableDatabase();
            sqlite.execSQL(sql1);
            sqlite.execSQL(sql2);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    //단건조회
    public void getInfoForCursorAdapter(int _id){
        sqlite = mytextDB.getReadableDatabase();
        String sql = "SELECT * FROM MemoTable WHERE _id=" + _id;

        cursor = sqlite.rawQuery(sql, null);

        String title = "";
        String body = "";

        if(cursor.getCount() > 0){
            cursor.moveToNext(); //커서의 처음위치는 -1이기 때문에 0으로 옮겨줌
            title = cursor.getString(7);
            body = cursor.getString(8);
        }

        titleEdit.setText(title);
        bodyEdit.setText(body);
        cursor.close();
        sqlite.close();
    }
}
