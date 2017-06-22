package myapp.project.zxchugo.myapplication;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ZxcHugo on 2017/2/19.
 */

public class TodayRoutine extends Fragment {
    private NotesDB dbHelper;
    private List<String> list;
    private ListView listView;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    ArrayAdapter<String> adapter;
    String date;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todayroutine, null);
        dbHelper = new NotesDB(getContext(),"Mydb.db",null,1);
        listView = (ListView)view.findViewById(R.id.listToday);
        date = sdf.format(new Date());
        show();
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int location = i;
                String s = list.get(i);
                final String Date = s.substring(0,s.indexOf(" "));
                final String Time = s.substring(s.indexOf(" ")+1,s.indexOf("\n"));
                new AlertDialog.Builder(getContext()).setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 删除
                        SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
                        dbWrite.execSQL("delete from Routine where Date=" + "'" + Date + "' and Time = "+"'"+Time+"'");
                        dbWrite.close();
                        list.remove(location);
                        adapter.notifyDataSetChanged();
                    }
                }).show();

                return false;
            }
        });

        return view;
    }
    public void onResume(){
        super.onResume();
        show();
    }
    private void show(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String getDate;
        String getTime;
        String content;
        list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from Routine where Date = ? ", new String[]{date});
        if(cursor.moveToFirst()){
            do{
                getDate = cursor.getString(cursor.getColumnIndex("Date"));
                getTime = cursor.getString(cursor.getColumnIndex("Time"));
                content = cursor.getString(cursor.getColumnIndex("Content"));
                list.add(getDate+" "+getTime+"\n"+content);
            }while (cursor.moveToNext());
        }
        adapter = new ArrayAdapter<> (getContext(),android.R.layout.simple_list_item_1,list);

        listView.setAdapter(adapter);
        db.close();
    }
}
