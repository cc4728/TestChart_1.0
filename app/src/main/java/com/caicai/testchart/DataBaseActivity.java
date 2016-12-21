package com.caicai.testchart;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by cj on 2016/11/17.
 */
public class DataBaseActivity extends Activity {
    private Button search, insert, delete;
    private ListView listView;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private SimpleAdapter simpleAdapter = null;
    private EditText ka, kb, la, lb, key, element;
    MyDatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_base);
        // 创建MyDatabaseHelper对象，指定数据库版本为1，此处使用相对路径即可
        // 数据库文件自动会保存在程序的数据文件夹的databases目录下
        //getData();
        dbHelper = new MyDatabaseHelper(this, "elementDB", 1);
        insert = (Button) findViewById(R.id.insert);
        search = (Button) findViewById(R.id.search);
        delete = (Button) findViewById(R.id.delete);
        listView = (ListView) findViewById(R.id.show);
        element = (EditText) findViewById(R.id.element);
        ka = (EditText) findViewById(R.id.ka);
        kb = (EditText) findViewById(R.id.kb);
        la = (EditText) findViewById(R.id.la);
        lb = (EditText) findViewById(R.id.lb);
        key = (EditText) findViewById(R.id.key);


        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    /*    simpleAdapter = new SimpleAdapter(this, getData(), R.layout.line_data_result,
                new String[]{"_id", "element", "ka","kb","la","lb"}, new int[]{
                R.id._id, R.id.element_show, R.id.ka_show,R.id.kb_show,R.id.la_show,R.id.lb_show});
        listView.setAdapter(simpleAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 退出程序时关闭MyDatabaseHelper里的SQLiteDatabase
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
    private List<Map<String, Object>> getData () {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String key_element = key.getText().toString();

        Cursor cursor = db.rawQuery("select * from dict where element=key_element", null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            String element = cursor.getString(1);
            String ka = cursor.getString(2);
            String kb = cursor.getString(3);
            String la = cursor.getString(4);
            String lb = cursor.getString(5);
            //这里同样是查询，只不过把查询到的数据添加到list集合，可以参照ListView的用法
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("_id", id); //获取_id
            map.put("element", element);  //获取name
            map.put("ka", ka);
            map.put("kb", kb);
            map.put("la", la);
            map.put("lb", lb);
            //获取credit学分
            list.add(map);
            //System.out.println("query--->" + id + "," + name + "," + credit);
        }
        return list;
    }*/

    }
}

