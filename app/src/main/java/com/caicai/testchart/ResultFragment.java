package com.caicai.testchart;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
    public ResultFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.result_layout, container, false);
        listView = (ListView) view.findViewById(R.id.result_show);
       if (DataProvide.reignOfInteresting[0] != 0)
           initListView(DataProvide.reignOfInteresting);
        return view;
    }


    private void initListView(int[] reignOfIntrest) {
        int count=0;
        for (int i=0;i<100;i++) {
            if (reignOfIntrest[i] == 0) {
                count=i;
                break;
            }
        }
        ArrayList<Map<String, String>> list = new ArrayList<>();

        for (int i=0;i<count;i+=2){
            String id, info;
            id = "ROI：[" + reignOfIntrest[i] + "," + reignOfIntrest[i + 1]+"]";
            info = "峰值：" + "\n净峰面积" + "\n其他详细信息";
            Map<String, String> map = new HashMap<>();
            map.put("id", id);
            map.put("info", info);
            list.add(map);
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
                Log.e("caicai", String.valueOf(i));//i=0选择数组01
                DataProvide.byHandBase=DataProvide.reignOfInteresting[2*i];
                DataProvide.byHandRange=DataProvide.reignOfInteresting[2*i+1];
                getFragmentManager().beginTransaction().replace(R.id.container,new ChartFragment()).commit();


            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                DataProvide.byHandBase=0;
                DataProvide.byHandRange=0;
                int temp;
                for (int k=2*i;k<96;k+=2){
                    //删除对应的数组的两个值，整体迁移
                    temp = DataProvide.reignOfInteresting[k];
                    DataProvide.reignOfInteresting[k] = DataProvide.reignOfInteresting[k + 2];
                    DataProvide.reignOfInteresting[k + 2] = temp;

                    temp = DataProvide.reignOfInteresting[k+1];
                    DataProvide.reignOfInteresting[k+1] = DataProvide.reignOfInteresting[k + 3];
                    DataProvide.reignOfInteresting[k + 3] = temp;
                }
                initListView(DataProvide.reignOfInteresting);

                getFragmentManager().beginTransaction().replace(R.id.container,new ChartFragment()).commit();

                Log.e("caicai", String.valueOf(i));//i=0选择数组01

                return true;
            }
        });
    }
}
