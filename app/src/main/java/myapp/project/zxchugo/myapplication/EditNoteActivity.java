package myapp.project.zxchugo.myapplication;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * 此文件为笔记编辑界面的activity，对应布局activity_editnote.xml
 */
public class EditNoteActivity extends AppCompatActivity {
    private static final int PHOTO_CHOOSE=1;
    private NotesDB dbHelper;
    private SQLiteDatabase db;
    private boolean isEdit;
    private EditText textTitle,textContent;
    private TextView textView;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private FloatingActionButton addImage;
    private String imagePath = null;
    private ImageView displayPic;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editnote);
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        isEdit = bundle.getBoolean("IsEdit");

        dbHelper = new NotesDB(this,"Notes.db",null,1);
        db = dbHelper.getWritableDatabase();

        Toolbar edtToolbar = (Toolbar)findViewById(R.id.edt_toolbar);
        setSupportActionBar(edtToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        displayPic = (ImageView)findViewById(R.id.display_pic);
        textView = (TextView)findViewById(R.id.textView3);
        textTitle = (EditText)findViewById(R.id.edt_title);
        textContent = (EditText)findViewById(R.id.edt_content);
        textView.setText(bundle.getString("Label"));                // 设置标签文本框
        // 如果处于编辑状态，则需要自动填充内容
        if(isEdit) {
            textTitle.setText(bundle.getString("Title"));
            textContent.setText(bundle.getString("Content"));
            imagePath= bundle.getString("Picture");
            if(imagePath != null)
                displayPic.setImageBitmap(compressImage(BitmapFactory.decodeFile(imagePath)));
        }

        // 悬浮按钮，添加照片
        addImage = (FloatingActionButton)findViewById(R.id.fabtn_add_pic);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(v.getContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(EditNoteActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);

                }
                else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, PHOTO_CHOOSE);        // 打开本机相册
                }
            }
        });

        // 返回按钮
        edtToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 确认按钮
        edtToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = bundle.getInt("Id");
                String title, content, label;
                label = textView.getText().toString();
                title = textTitle.getText().toString();
                content = textContent.getText().toString();
                switch (item.getItemId()){
                    case R.id.action_ok:
                        // 保存等操作
                        if(isEdit == false) {      // 新建
                            ContentValues values = new ContentValues();
                            values.put("noteTitle", title);
                            values.put("noteDate", sdf.format(new Date()));
                            values.put("noteContent", content);
                            values.put("noteLabel", label);
                            values.put("noteImage", imagePath);
                            db.insert("Note", null, values);
                            values.clear();
                            finish();               // finish之后回到前一个Activity的resume函数
                        }
                        else{                       // 编辑
                            db = dbHelper.getWritableDatabase();
                            db.execSQL("update Note set noteTitle='" + title
                                        + "',noteContent='" + content
                                        + "',noteImage='" + imagePath + "' where noteId="
                                        + id);
                            finish();
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        // 点击放大图片操作
        displayPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    // 从相册返回后在此处执行函数
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 设置ImageView
        switch(requestCode){
            case PHOTO_CHOOSE:
                if (Build.VERSION.SDK_INT >= 19) {
                    // 4.4及以上系统使用这个方法处理图片
                    handleImageOnKitKat(data);
                } else {
                    // 4.4以下系统使用这个方法处理图片
                    handleImageBeforeKitKat(data);
                }
                break;

            }
    }



    private void handleImageOnKitKat(Intent data) {
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];        // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayPic.setImageBitmap(compressImage(BitmapFactory.decodeFile(imagePath)));         // 在编辑界面显示图片
    }



    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        imagePath = getImagePath(uri, null);
        displayPic.setImageBitmap(compressImage(BitmapFactory.decodeFile(imagePath)));         // 在编辑界面显示图片
    }



    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);  // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>50) {          // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();       // 重置baos即清空baos
            options -= 10;      // 每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);  // 这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());   // 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);   //把ByteArrayInputStream数据生成图片
        return bitmap;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    //将drawable转换成可以用来存储的byte[]类型
    private byte[] BmpToByte(Bitmap bitmap) {
//        if(drawable == null) {
//            return null;
//        }
        if(bitmap == null) {
            return null;
        }
//        BitmapDrawable bd = (BitmapDrawable) drawable;传入int资源时用
//        Bitmap bitmap = bd.getBitmap();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        return os.toByteArray();
    }



    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor1 = getContentResolver().query(uri, null, selection, null, null);
        if (cursor1 != null) {
            if (cursor1.moveToFirst()) {
                path = cursor1.getString(cursor1.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor1.close();
        }
        return path;
    }
}
