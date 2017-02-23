package com.caicai.testchart;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cai on 2017/2/14.
 */

public class DBFragment extends Fragment {
    private Button search, insert, delete;
    private ListView listView;
    private List<Map<String, Element>> list = new ArrayList<>();
    private SimpleAdapter simpleAdapter = null;
    private EditText ka, kb, la, lb, element;
    MyDatabaseHelper dbHelper;
    Cursor model = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.db_frg_layout, container, false);
        insert = (Button) view.findViewById(R.id.db_add);
        search = (Button) view.findViewById(R.id.db_search);
        delete = (Button) view.findViewById(R.id.db_delete);
        listView = (ListView) view.findViewById(R.id.showDataBase);
        element = (EditText) view.findViewById(R.id.input_name);
        ka = (EditText) view.findViewById(R.id.input_ka);
        kb = (EditText) view.findViewById(R.id.input_kb);
        la = (EditText) view.findViewById(R.id.input_la);
        lb = (EditText) view.findViewById(R.id.input_lb);
        dbHelper = new MyDatabaseHelper(getActivity());
        Show(dbHelper.SearchByName(""));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        //设置监听事件
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
            value = "   " + ka + "; " + kb + "; " + la + "; " + lb + ";";
            Map<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("value", value);
            list.add(map);
            Log.e("caicai", "list创建");
        }
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), list, R.layout.db_show_listview, new String[]{"name", "value"},
                new int[]{R.id.db_show_name, R.id.db_show_value});
        listView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
        Log.e("caicai", "显示");
    }


}
