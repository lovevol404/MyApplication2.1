package myapp.project.zxchugo.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ZxcHugo on 2017/2/25.
 * 此文件为笔记的DB设计
 */

public class NotesDB extends SQLiteOpenHelper {

    private Context mContext;

    public NotesDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_table_1 = "create table Label ("
                + "LabelId integer primary key autoincrement,"
                + "label char(20))";
        db.execSQL(create_table_1);                                     // 创建表一

        String create_table_2 = "create table Note ("
                + "noteId integer primary key autoincrement,"
                + "noteTitle char(30),"
                + "noteDate text,"
                + "noteContent text,"
                + "noteLabel char(20),"
                + "noteImage text,"
                + "foreign key(noteLabel) references Label(LabelId))";
        db.execSQL(create_table_2);                                     // 创建表二
        String create_table_3 = "create table Routine ("
                +"routineId integer primary key autoincrement,"
                + "Date text,"
                + "Time text,"
                + "Content text)";
        db.execSQL(create_table_3);                                     // 创建表一
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
