package myapp.project.zxchugo.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by ZxcHugo on 2017/2/19.
 * 此文件为笔记界面，对应布局为note.xml
 * item为标签，对应布局为note_label_item.xml
 */

public class FragmentNote extends Fragment {

    private ArrayList<String> mLabels;              // 标签数组
    private RecyclerView labelRecycleview;
    private NoteLabelAdapter adapter;
    private FloatingActionButton fab;               // 悬浮按钮，添加新标签
    private NotesDB dbHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new NotesDB(getActivity(), "Notes.db", null, 1);
        mLabels = new ArrayList<String>();
        //mLabels.add("默认");
        //SQLiteDatabase dbRead = dbHelper.getWritableDatabase();
        //dbRead.execSQL("insert into Label (label) values('默认')");       // 添加默认标签
        initData();                 // 在onCreate过程中从数据库中读出已存在的标签
    }

    public void initData(){
        SQLiteDatabase dbRead = dbHelper.getReadableDatabase();
        Cursor cursor = dbRead.rawQuery("select label from Label", null);
        if(cursor.moveToFirst()){
            do {
                // 遍历，将数据库中的所有标签读出，添加进mLabel数组
                String labelName = cursor.getString(cursor.getColumnIndex("label"));
                mLabels.add(labelName);
            }while(cursor.moveToNext());
        }
        cursor.close();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.note, container, false);
        labelRecycleview = (RecyclerView)v.findViewById(R.id.notelabelrv);          // 绑定RecycleView并设置其Adapter
        labelRecycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        labelRecycleview.setAdapter(adapter = new NoteLabelAdapter());
        fab = (FloatingActionButton)v.findViewById(R.id.fabtn_add_label);

        // 绑定悬浮按钮点击实现，以对话框的形式体现
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText edt = new EditText(getContext());
                // 对话框由一个EditText，两个Button构成
                new AlertDialog.Builder(getContext()).setTitle("请输入新标签名").setView(edt).
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String labelName = edt.getText().toString();
                                if(labelName.equals("")){
                                    Toast.makeText(getContext(), "标签名不能为空!", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    for(int i = 0; i < mLabels.size(); i++){
                                        if(mLabels.get(i).equals(labelName)) {
                                            Toast.makeText(getContext(), "标签名已存在!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                    // 添加新标签，往mLabel和数据库中添加
                                    mLabels.add(labelName);
                                    SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
                                    dbWrite.execSQL("insert into Label (label) values('" + labelName + "')");
                                    adapter.notifyDataSetChanged();         // 刷新recycleview界面
                                }
                            }
                        }).setNegativeButton("取消", null).show();
            }
        });
        return v;
    }


    // 以下为实现RecycleView的固定方法
    class NoteLabelAdapter extends RecyclerView.Adapter<NoteLabelAdapter.MyLabelViewHolder>{

        @Override
        public NoteLabelAdapter.MyLabelViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

            final MyLabelViewHolder holder = new MyLabelViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.note_label_item,parent,false));
            holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    String label = mLabels.get(pos);
                    Intent intent = new Intent(parent.getContext(), NotePreviewActivity.class);
                    intent.putExtra("label",label);
                    startActivity(intent);
                }
            });
            // item长按功能，删除
            holder.tv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //final long pos = holder.getItemId();
                    final String labelName = holder.tv.getText().toString();
                    new AlertDialog.Builder(getContext()).setPositiveButton("删除标签", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 删除
                            //mLabels.remove(pos);
                            for(int i = 0; i < mLabels.size(); i++){
                                if(mLabels.get(i).equals(labelName)) {
                                    mLabels.remove(i);
                                    break;
                                }
                            }
                            SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
                            dbWrite.execSQL("delete from Label where label=" + "'" + labelName + "'");
                            dbWrite.execSQL("delete from Note where noteLabel=" + "'" + labelName + "'");
                            adapter.notifyDataSetChanged();         // 刷新recycleview界面
                        }
                    }).show();
                    return true;
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(NoteLabelAdapter.MyLabelViewHolder holder, int position) {
                holder.tv.setText(mLabels.get(position));
        }

        @Override
        public int getItemCount() {
            return mLabels.size();
        }

        class MyLabelViewHolder extends RecyclerView.ViewHolder{
            TextView tv;

            public MyLabelViewHolder(View view){
                super(view);
                tv = (TextView)view.findViewById(R.id.labeltv);
            }
        }
    }
}
