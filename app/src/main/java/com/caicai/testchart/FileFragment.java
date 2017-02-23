package com.caicai.testchart;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cai on 2017/2/15.
 * 文件管理任务相关
 */

public class FileFragment extends Fragment {
    ListView listView;
    TextView textView;
    String path;
    // 记录当前的父文件夹
    File currentParent;
    int flag=0;
    // 记录当前路径下的所有文件的文件数组
    File[] currentFiles;
    Button parent, fileDelete, addToChart;

    public FileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.file_layout, container, false);
       listView = (ListView) view.findViewById(R.id.list);
        textView = (TextView) view.findViewById(R.id.path);
        parent = (Button) view.findViewById(R.id.parent);
        fileDelete = (Button) view.findViewById(R.id.deleteFile);
        addToChart = (Button) view.findViewById(R.id.addToChart);
        File root = new File("/storage/sdcard1/XRF/mydata");
        if (root.exists()) {
            currentParent = root;
            currentFiles = root.listFiles();
            // 使用当前目录下的全部文件、文件夹来填充ListView
            inflateListView(currentFiles);
        }
        return view;
    }

    private void inflateListView(File[] files) {

        // 创建一个List集合，List集合的元素是Map
        List<Map<String, Object>> listItems =
                new ArrayList<Map<String, Object>>();
        for (int i = 0; i < files.length; i++) {
            Map<String, Object> listItem =
                    new HashMap<String, Object>();
            // 如果当前File是文件夹，使用folder图标；否则使用file图标
            if (files[i].isDirectory()) {
                listItem.put("icon", R.drawable.folder);
            } else {
                listItem.put("icon", R.drawable.file);
            }
            listItem.put("fileName", files[i].getName());
            // 添加List项
            listItems.add(listItem);
        }
        // 创建一个SimpleAdapterfiles
        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity()
                , listItems, R.layout.line
                , new String[]{"icon", "fileName"}
                , new int[]{R.id.icon, R.id.file_name});
        // 为ListView设置Adapter
        listView.setAdapter(simpleAdapter);
        try {
            textView.setText("路径:"
                    + currentParent.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //逻辑实现
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.e("caicai", String.valueOf(parent));
                Log.e("caicai", String.valueOf(id));
                Log.e("caicai", String.valueOf(position));
                // 用户单击了文件，直接返回，不做任何处理
                if (currentFiles[position].isFile())
                    try {
                        path = currentParent.getCanonicalPath() + "/" + currentFiles[position].getName();
                        flag=1;
                        Toast.makeText(getActivity(), path, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                // 获取用户点击的文件夹下的所有文件

                File[] tmp = currentFiles[position].listFiles();

                if (tmp == null) return;//空文件夹不做处理
                else {
                    // 获取用户单击的列表项对应的文件夹，设为当前的父文件夹
                    currentParent = currentFiles[position]; // ②
                    // 保存当前的父文件夹内的全部文件和文件夹
                    currentFiles = tmp;
                    // 再次更新ListView
                    inflateListView(currentFiles);
                }
            }
        });

        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View source) {
                try {
                    if (!currentParent.getCanonicalPath()
                            .equals("/storage")) {
                        // 获取上一级目录
                        currentParent = currentParent.getParentFile();
                        // 列出当前目录下所有文件
                        currentFiles = currentParent.listFiles();
                        // 再次更新ListView
                        inflateListView(currentFiles);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        fileDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (flag == 1) {
                    File file = new File(path);
                    currentParent = file.getParentFile();
                    file.delete();
                    currentFiles = currentParent.listFiles();
                    inflateListView(currentFiles);
                    flag=0;
                    Toast.makeText(getActivity(), "文件删除成功", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addToChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (path != null){
                    DataProvide.MyPath = path;
                    ChartFragment.flag=0;//显示原始图
                }
                getActivity().getFragmentManager().beginTransaction().
                        replace(R.id.container,new ChartFragment()).
                        replace(R.id.left, new ResultFragment()).commit();
                Toast.makeText(getActivity(), "文件加载成功", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
