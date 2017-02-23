package com.caicai.testchart;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cai on 2017/2/14.
 *
 * 显示兴趣区分析结果
 * 维护兴趣区的删除操作
 */

public class ResultFragment extends Fragment {
    private ListView listView;
    private int[] reignOfIntrest = new int[100];

    public ResultFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.result_layout, container, false);
        listView = (ListView) view.findViewById(R.id.show_result);
        if (DataProvide.reignOfInteresting[0] != 0)
            initListView(DataProvide.reignOfInteresting);
        return view;
    }

    private void initListView(int[] reignOfIntrest) {
        ArrayList<Map<String, String>> list = new ArrayList<>();
        int i = 0;
        while (reignOfIntrest[i] != 0 && i < 100) {
            String id, info;
            id = "第" + (i / 2 + 1) + "个ROI：Start" + reignOfIntrest[i] + "End" + reignOfIntrest[i + 1];
            info = "峰值：" + "/n净峰面积" + "/n其他详细信息";
            Map<String, String> map = new HashMap<>();
            map.put("id", id);
            map.put("info", info);
            list.add(map);
            i += 2;
        }
        SimpleAdapter adapter = new SimpleAdapter
                (getActivity(), list, R.layout.result_show_lsv, new String[]
                        {"id", "info"}, new int[]{R.id.result_id, R.id.result_info});
        listView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//点击高亮显示如图所示区域
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {



            }
        });
        listView.setOnLongClickListener(new View.OnLongClickListener() {//长按则删除感兴趣区
            @Override
            public boolean onLongClick(View view) {



                return false;
            }
        });
    }
}
