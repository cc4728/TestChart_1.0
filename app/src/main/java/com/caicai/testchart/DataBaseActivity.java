package com.caicai.testchart;

import android.app.Activity;
import android.app.AlertDialog;
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


import java.io.File;
import java.io.IOException;
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
    private List<Map<String, Element>> list = new ArrayList<>();
    private SimpleAdapter simpleAdapter = null;
    private EditText ka, kb, la, lb, element;
    MyDatabaseHelper dbHelper;
    Cursor model = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_base);
        // 创建MyDatabaseHelper对象，指定数据库版本为1，此处使用相对路径即可
        // 数据库文件自动会保存在程序的数据文件夹的databases目录下
        initList();
        dbHelper = new MyDatabaseHelper(this);
        Show(dbHelper.SearchByName(""));
    }

    private void initList() {
        insert = (Button) findViewById(R.id.insert);
        search = (Button) findViewById(R.id.search);
        delete = (Button) findViewById(R.id.delete);
        listView = (ListView) findViewById(R.id.showDataBase);
        element = (EditText) findViewById(R.id.element);
        ka = (EditText) findViewById(R.id.ka);
        kb = (EditText) findViewById(R.id.kb);
        la = (EditText) findViewById(R.id.la);
        lb = (EditText) findViewById(R.id.lb);


        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = element.getText().toString();
                int ka1 = Integer.parseInt(ka.getText().toString());
                int kb2 = Integer.parseInt(kb.getText().toString());
                int la3 = Integer.parseInt(la.getText().toString());
                int lb4 = Integer.parseInt(lb.getText().toString());
                dbHelper.insert(name, ka1, kb2, la3, lb4);
                Show(dbHelper.SearchByName(""));
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("caicai", "删除");
                String name = element.getText().toString();
                if (name != null) {
                    dbHelper.delete(name);
                    Show(dbHelper.SearchByName(""));
                }
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = element.getText().toString();
                Show(dbHelper.SearchByName(name));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 退出程序时关闭MyDatabaseHelper里的SQLiteDatabase
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    public void Show(Cursor cursor) {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        while (cursor.moveToNext()) {
            String name;
            String value;
            int ka, kb, la, lb;
            name = cursor.getString(1);
            ka = cursor.getInt(2);
            kb = cursor.getInt(3);
            la = cursor.getInt(4);
            lb = cursor.getInt(5);
            value = "             " + ka + ";            " + kb + ";              " + la + ";              " + lb + ";";
            Map<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("value", value);
            list.add(map);
            Log.e("caicai", "list创建");
        }
        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.db_show_listview, new String[]{"name", "value"}, new int[]{R.id.db_show_name, R.id.db_show_value});
        listView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
        Log.e("caicai", "显示");
    }

}

