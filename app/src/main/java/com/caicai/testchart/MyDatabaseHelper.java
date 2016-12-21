package com.caicai.testchart;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by cj on 2016/11/17.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper
{

    final String CREATE_TABLE_SQL =
            "create table dict(_id integer primary " +
                    "key autoincrement , word , detail)";

    public MyDatabaseHelper(Context context, String name, int version)
    {
        super(context, name, null, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // 第一次使用数据库时自动建表
        db.execSQL(CREATE_TABLE_SQL);
        Log.e("caicai", "创建一个数据库");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db
            , int oldVersion, int newVersion)
    {
        System.out.println("--------onUpdate Called--------"
                + oldVersion + "--->" + newVersion);
    }
}
