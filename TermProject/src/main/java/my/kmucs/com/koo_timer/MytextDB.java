package my.kmucs.com.koo_timer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Koo on 2016-12-01.
 */

public class MytextDB extends SQLiteOpenHelper {
    public MytextDB(Context context){
        super(context, "MemoTable", null, 1); //1번 버전을 만든다.
    }

    @Override //최초실행시
    public void onCreate(SQLiteDatabase db) {
        //최초 DB 만들때만 실행
        String sql = "CREATE TABLE MemoTable(_id INTEGER PRIMARY KEY AUTOINCREMENT, year INTEGER, month INTEGER, day INTEGER, hour INTEGER, min INTEGER, sec INTEGER, title TEXT, body TEXT)"; //table 이름 : timeRecord

        //sql실행
        db.execSQL(sql);

    }

    @Override //업데이트할때
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //버젼에 업그레이드가 있을때
        String sql = "DROP TABLE IF EXISTS MemoTable"; //만약 timeRecord이라는 테이블이 존재한다면 TABLE을 날려버려라!
        db.execSQL(sql);

        //날려버리고 새로 실행하는 것이 필요하다
        onCreate(db);
    }
}
