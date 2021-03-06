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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.Line;

/**
 * Created by cai on 2017/2/15.
 * 文件管理任务相关
 */

public class FileFragment extends Fragment {
    public static final String PATHNAME = "/storage/sdcard1/XRF/mydata/";
    ListView listView;
    TextView textView;
    String path;
    // 记录当前的父文件夹
    File currentParent;
    int flag = 0;
    // 记录当前路径下的所有文件的文件数组
    File[] currentFiles;
    Button parent, fileDelete, addToChart, fileSave;
    String content;

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
        fileSave = (Button) view.findViewById(R.id.saveFile);
        File root = new File(PATHNAME);
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
                        flag = 1;//标记文件删除
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
                    flag = 0;
                    Toast.makeText(getActivity(), "文件删除成功", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addToChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (path != null) {
                    DataProvide.MyPath = path;
                    ChartFragment.flag = 0;//显示原始图
                    DataProvide.addFile = 1;//更新原始数据
                }
                getActivity().getFragmentManager().beginTransaction().
                        replace(R.id.container, new ChartFragment()).
                        replace(R.id.left, new ResultFragment()).commit();
                Toast.makeText(getActivity(), "文件加载成功", Toast.LENGTH_SHORT).show();
            }
        });


        fileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //我要获取当前的日期
                Date date = new Date();
                //设置要获取到什么样的时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                //获取String类型的时间
                String createdate = sdf.format(date);
                Log.e("caicai", createdate);
                String fileName = createdate + ".txt";
                File file = new File(PATHNAME, fileName);
                try {
                    if (!file.exists()) {
                        file.createNewFile();
                        Log.e("caicai", "成功创建文件");
                        writeFileContent(PATHNAME + fileName, setContent());
                        currentParent=file.getParentFile();
                        currentFiles=currentParent.listFiles();
                        inflateListView(currentFiles);
                        Toast.makeText(getActivity(), "文件保存成功", Toast.LENGTH_SHORT).show();
                    }
                    Log.e("caicai", file.getParent().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void writeFileContent(String path, String content) throws IOException {
        try {
               /* FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
                fileOutputStream.write(content.getBytes());
                第一种文件写入，最慢
                fileOutputStream.close();*/

               /*FileWriter fileWriter=new FileWriter(path);
                fileWriter.write(content);
                最快
                fileWriter.close();*/
            FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bufferedOutputStream.write(content.getBytes());
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            Log.e("caicai", "2");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String setContent() {
        StringBuffer buffer=new StringBuffer();
        buffer.append("<<XRF SPECTRUM>>");
        buffer.append("\r\nAll rights reserved by CaiJing 2017.2.23");
        buffer.append("\r\n<<Smooth>>");
        buffer.append("\r\n"+DataProvide.smoothStyle+","+DataProvide.smoothTimes);
        buffer.append("\r\n<<ReduceBackground>>");
        buffer.append("\r\n"+DataProvide.reduce);
        buffer.append("\r\n<<ROI>>");
        for (int i=0;i<100;i+=2) {
            if (DataProvide.reignOfInteresting[i] == 0) {
                break;
            } else {
                buffer.append("\r\n["+DataProvide.reignOfInteresting[i]+","+DataProvide.reignOfInteresting[i+1]+"]");
                Log.e("caicai","addbuffer");
            }
        }
        buffer.append("\r\n<<DATA>>");
        int k=DataProvide.NUMBERPOINT;
        for (int j=0;j<k;j++) {
            buffer.append("\r\n"+DataProvide.rawValue[j]);
        }
        buffer.append("\r\n<<END>>");
        content=buffer.toString();

        return content;
    }

}
