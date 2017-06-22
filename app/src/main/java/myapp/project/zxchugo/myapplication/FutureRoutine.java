package myapp.project.zxchugo.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ZxcHugo on 2017/2/19.
 */

public class FutureRoutine extends Fragment {
    int Year,Month,Day;
    CalendarView calendarView;
    private NotesDB dbHelper;
    private List<String> list;
    private ListView listView;
    ArrayAdapter<String> adapter;
    String date;
    private FloatingActionButton add;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    boolean flag = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.futureroutine, null);
        flag = false;
        dbHelper = new NotesDB(getContext(),"Mydb.db",null,1);
        listView = (ListView)view.findViewById(R.id.listViewFuture);
        calendarView = (CalendarView)view.findViewById(R.id.calendarView);
        date = sdf.format(new Date());
        add = (FloatingActionButton)view.findViewById(R.id.addRoutine);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Year = year;
                Month = month+1;
                Day = dayOfMonth;
                flag = true;
                show();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),AddRoutine.class);
                if(flag)
                    intent.putExtra("date",Year+"-"+Month+"-"+Day);
                else
                    intent.putExtra("date",date);
                startActivity(intent);
            }
        });
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
        show();
        return view;
    }
    private void show(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String getDate;
        String getTime;
        String content;
        if(flag)
            date = Year+"-"+Month+"-"+Day;
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
    public void onResume(){
        super.onResume();
        show();
    }
}
