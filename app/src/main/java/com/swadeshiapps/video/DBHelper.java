package com.swadeshiapps.video;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.swadeshiapps.video.POJO.Videos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "demo", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table videos(id int primary key,thumb text,path text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
    boolean addFav (Videos v) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("path",v.getPath());
        contentValues.put("thumb",v.getThumb());
        try {
            return db.insertOrThrow("videos", null, contentValues) > 0;
        }catch (Exception e){
            Log.e("myerr",e.getMessage());
        }
        return false;
    }
    public List<Videos> getAll() {
        List<Videos> array_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from videos order by id desc", null );
        res.moveToFirst();
        while(!res.isAfterLast()){
            Videos d=new Videos(res.getString(res.getColumnIndex("path")),res.getString(res.getColumnIndex("thumb")));
            File f=new File(d.getPath());
            if(f.exists() && f.length()>0)
                array_list.add(d);
            else
                removeFav(d.getPath());
            res.moveToNext();
        }
        res.close();
        return array_list;
    }
    boolean isFab(String path){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from videos where path=?", new String[]{path} );
        return res.getCount()>0;
    }
    boolean removeFav(String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        return  db.delete("videos","path=?",new String[]{path})>0;
    }
}
