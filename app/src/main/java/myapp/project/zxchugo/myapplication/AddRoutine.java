package myapp.project.zxchugo.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class AddRoutine extends AppCompatActivity {
    EditText editText;
    String date;
    TimePicker timePicker;
    String time;
    TextView  textView;
    String content;
    Button button;
    private NotesDB dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_routine);
        Intent intent ;
        intent = getIntent();
        date=intent.getStringExtra("date");
        timePicker = (TimePicker)findViewById(R.id.timePicker);
        textView = (TextView)findViewById(R.id.textViewTime);
        editText = (EditText)findViewById(R.id.editTextRoutine);
        button = (Button)findViewById(R.id.button111);
        dbHelper = new NotesDB(this,"Mydb.db",null,1);
        timePicker.setIs24HourView(true);
        time = timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute();
        textView.setText(date+" "+time);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                time = timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute();
                textView.setText(date+" "+time);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Save();
            }
        });
    }
    public void Save(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        content = editText.getText().toString();
        ContentValues values = new ContentValues();
        values.put("Date", date);
        values.put("Time",time);
        values.put("Content", content);
        db.insert("Routine", null, values);
        values.clear();
        db.close();
        finish();
    }
}
