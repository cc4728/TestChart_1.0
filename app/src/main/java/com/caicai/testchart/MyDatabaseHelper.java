package com.caicai.testchart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by cj on 2016/11/17.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "elements.db";//数据库名称
    private static final int SCHEMA_VERSION = 1;

    public MyDatabaseHelper(Context context) {//构造函数,接收上下文作为参数,直接调用的父类的构造函数
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE element (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, ka INTEGER, kb INTEGER, la INTEGER, lb INTEGER);");
        Log.e("caicai","创建表element");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }


    public Cursor SearchByName(String name) {//根据点击事件获取id,查询数据库
        String[] args = {"%"+name+"%"};
        Log.e("caicai", "search");
        Log.e("caicai", name);
        return (getReadableDatabase().rawQuery("SELECT * FROM element WHERE name like ?", args));
    }



    public void insert(String name, int ka, int kb, int la, int lb) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("ka", ka);
        cv.put("kb", kb);
        cv.put("la", la);
        cv.put("lb", lb);
        getWritableDatabase().insert("element", "name", cv);
        Log.e("caicai", "charu");
    }


    public void delete(String name){
        //表名，满足whereClause 的句子将会删除，用于whereClause传入参数
        getWritableDatabase().delete("element", "name like ?", new String[]{name});
    }

}
