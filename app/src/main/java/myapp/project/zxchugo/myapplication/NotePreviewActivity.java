package myapp.project.zxchugo.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ZxcHugo on 2017/2/24.
 * 此文件为实现笔记的缩略图预览对应布局note_list_item.xml和activity_note_preview.xml
 */

public class NotePreviewActivity extends AppCompatActivity {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private List<NotePreviewEntity> dataList;
    private NotePreviewAdapter adapter;
    NotesDB dbHelper;
    SQLiteDatabase db;
    String label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_preview);
        Intent intent = getIntent();
        label = intent.getStringExtra("label");
        setTitle(label);

        dataList = new ArrayList<>();
        dbHelper = new NotesDB(this,"Notes.db",null,1);
        db = dbHelper.getWritableDatabase();
        initData();
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.note_preview_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter = new NotePreviewAdapter(dataList));


        // 绑定悬浮按钮，创建新日记
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fabtn_add_note);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotePreviewActivity.this, EditNoteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("IsEdit", false);
                bundle.putString("Label", label);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    protected void onResume() {     // 回到此活动则刷新数据
        super.onResume();
        initData();
        adapter.notifyDataSetChanged();
    }
    private void initData(){
        dataList.clear();
       /*  for(int i = 0; i < 15; i++){
              NotePreviewEntity e = new NotePreviewEntity("日记", "今天天气不错！");
              dataList.add(e);
       }*/
        Cursor cursor;
        cursor=db.rawQuery("select * from Note where noteLabel = ? ", new String[]{label}); // 查询相关标签的全部笔记
        if(cursor.moveToFirst()){
            do{
                // 逐项读取id，title，content，date，image
                int id = cursor.getInt(cursor.getColumnIndex("noteId"));
                String title = cursor.getString(cursor.getColumnIndex("noteTitle"));
                String content = cursor.getString(cursor.getColumnIndex("noteContent"));
                String date = cursor.getString(cursor.getColumnIndex("noteDate"));
                String imagePath = cursor.getString(cursor.getColumnIndex("noteImage"));
                //将获取的数据转换成drawable
                Date date1 = null;
                try {
                    date1= sdf.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // 创建日记预览实体
                NotePreviewEntity e = new NotePreviewEntity(id, title, date1, content, imagePath);
                dataList.add(e);
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

    // 笔记预览Adapter
    class NotePreviewAdapter extends RecyclerView.Adapter<NotePreviewAdapter.MyNotePreviewHolder>{

        private List<NotePreviewEntity> dataList;
        private Context mContext;

        @Override
        public NotePreviewAdapter.MyNotePreviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {  // 创建具体的ViewHolder
            if(mContext == null) {
                mContext = parent.getContext();
            }
            final View view = LayoutInflater.from(mContext).inflate(R.layout.note_list_item, parent, false);
            final MyNotePreviewHolder holder = new MyNotePreviewHolder(view);
            return new MyNotePreviewHolder(view);
        }

        public NotePreviewAdapter(List<NotePreviewEntity> dataList){
            this.dataList = dataList;
        }

        @Override
        public void onBindViewHolder(NotePreviewAdapter.MyNotePreviewHolder holder, final int position) {      // 绑定ViewHolder数据
            NotePreviewEntity entity = dataList.get(position);              // 得到一项预览item
            holder.previewContent.setText(entity.getContent());             // 设置内容
            holder.previewTitle.setText(entity.getDate() + " " + entity.getTitle());// 设置标题 "yyyy-MM-hh title_name"
            if(entity.getBitmapUri() != null){
                holder.previewBitmap.setImageBitmap(BitmapFactory.decodeFile(entity.getBitmapUri()));
            }
            else{
                holder.previewBitmap.setImageBitmap(null);
            }

            final int pos = position;           // 当前位置
            // 绑定长按点击操作
            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // 弹出式菜单
                    Log.e("tag1", "popmenu");
                    PopupMenu popupMenu = new PopupMenu(NotePreviewActivity.this,v);
                    // Toast.makeText(mContext,""+pos,Toast.LENGTH_SHORT).show();
                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            // 弹出菜单点击事件
                            Toast.makeText(mContext, ""+position, Toast.LENGTH_SHORT).show();
                            switch (item.getItemId()){
                                case R.id.edit:
                                    // 编辑
                                    Intent intent = new Intent(NotePreviewActivity.this, EditNoteActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean("IsEdit", true);
                                    bundle.putString("Label", label);
                                    bundle.putInt("Id", dataList.get(pos).getId());             // id
                                    bundle.putString("Title", dataList.get(pos).getTitle());    // 传入标题
                                    bundle.putString("Content", dataList.get(pos).getContent());// 内容
                                    bundle.putString("Picture", dataList.get(pos).getBitmapUri());  // 图片路径
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    break;
                                case R.id.delete:
                                    // 删除
                                    db = dbHelper.getWritableDatabase();
                                    db.execSQL("delete from Note where noteId=" + dataList.get(pos).getId());
                                    dataList.remove(pos);
                                    adapter.notifyDataSetChanged();         // 更新界面
                                    break;
                                default:
                                    break;
                            }
                            return false;
                        }
                    });
                    return true;
                }
            });         // 绑定完成
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        class MyNotePreviewHolder extends RecyclerView.ViewHolder{
            TextView previewTitle;              // 预览标题
            TextView previewContent;            // 预览内容
            CardView cardView;
            ImageView previewBitmap;

            public MyNotePreviewHolder(View view) {
                super(view);
                cardView = (CardView)view.findViewById(R.id.cardview);
                previewTitle = (TextView)view.findViewById(R.id.previewtitle);
                previewContent = (TextView)view.findViewById(R.id.previewcontent);
                previewBitmap = (ImageView) view.findViewById(R.id.imageView_);
            }
        }
    }
}
