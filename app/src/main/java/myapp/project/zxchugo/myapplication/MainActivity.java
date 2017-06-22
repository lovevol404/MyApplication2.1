package myapp.project.zxchugo.myapplication;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

    FragmentSettings settings;
    FragmentRoutine routine;
    FragmentNote note;
    NotesDB dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_tab_menu);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        dbHelper = new NotesDB(this,"NoteDb.db",null,1);
        db = dbHelper.getWritableDatabase();

        routine = new FragmentRoutine();
        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,routine).commit();
        // 底部菜单实现
        RadioGroup myTabRg = (RadioGroup)findViewById(R.id.bottom_rg);

        myTabRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rbroutine:
                        routine = new FragmentRoutine();
                        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,routine).commit();
                        break;
                    case R.id.rbnote:
                        note = new FragmentNote();
                        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,note).commit();
                        break;
                    case R.id.rbsettings:
                        settings = new FragmentSettings();
                        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,settings).commit();
                        break;
                    default:
                        break;
                }
            }
        });

    }
}
