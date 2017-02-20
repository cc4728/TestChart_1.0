package com.caicai.testchart;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by cai on 2017/2/14.
 */

public class TitleFragment extends Fragment {

    TextView appName, file, process, set, dataBase;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.title_layout, container, false);
        appName = (TextView) view.findViewById(R.id.title_app);
        file = (TextView) view.findViewById(R.id.title_file);
        process = (TextView) view.findViewById(R.id.title_process);
        set = (TextView) view.findViewById(R.id.title_set);
        dataBase = (TextView) view.findViewById(R.id.title_db);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        appName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().beginTransaction().replace(R.id.left, new ResultFragment()).commit();
            }
        });
        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().beginTransaction().replace(R.id.left, new FileFragment()).commit();
                Log.e("caicai", "文件管理待开发");
            }
        });
        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().beginTransaction().replace(R.id.left, new DPFrament()).commit();
            }
        });
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().beginTransaction().replace(R.id.left, new SettingFragment()).commit();
            }
        });
        dataBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().beginTransaction().replace(R.id.left, new DBFragment()).commit();
            }
        });


    }

}







