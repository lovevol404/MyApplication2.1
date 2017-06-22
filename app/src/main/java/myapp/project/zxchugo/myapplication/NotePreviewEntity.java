package myapp.project.zxchugo.myapplication;

import android.graphics.Bitmap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ZxcHugo on 2017/2/25.
 * 此文件为日记缩略预览实体类
 */

 class NotePreviewEntity {

    private int id;
    private String title;           // 标题
    private Date date;              // 日期
    private String content;         // 内容
    private String bitmapUri;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public NotePreviewEntity(int id, String title, Date date, String content, String bitmap){
        this.id = id;
        this.title = title;
        this.date = date;
        this.content = content;
        this.bitmapUri = bitmap;
    }

    public NotePreviewEntity(String title, String content){
        this.title = title;
        this.content = content;
        //this.date = new Date();     // 默认为设备上的日期
    }

    public String getTitle(){
        return title;
    }

    public int getId(){
        return id;
    }

    /*
    * 返回日期的字符串形式如 "2017-01-01"
     */
    public String getDate(){
        String strDate = sdf.format(date);
        return strDate;
    }

    public String getContent(){
        return content;
    }

    public void setTitle(String title){
        this.title = title;
    }
    public String getBitmapUri(){
        return this.bitmapUri;
    }

    /*
    * 设置日期
    * @Param date为"yyyy-MM-dd"形式
     */
    public void setDate(String date){
        try {
            this.date = sdf.parse(date);
        }catch(ParseException e){
            e.printStackTrace();
        }
    }

    public void setContent(String content){
        this.content = content;
    }

}
