package com.caicai.testchart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import java.io.FileNotFoundException;

import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.LineChartView;

/*
    1.主界面尽量简洁，各种功能启动
    2.主要负责波形显示，传入一个数组，显示波形
    3.避免更多的其他功能，减少耦合
    4.所有信息交汇处理： string 文件路径
                         index  谱线处理各种信息
                         y_value 显示数组信息
 */

public class MainActivity extends AppCompatActivity {
    FrameLayout frameLayout_left;
    FrameLayout frameLayout_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (frameLayout_left == null) {
            getFragmentManager().beginTransaction().add(R.id.left, new ResultFragment()).commit();
        }
        if (frameLayout_container == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new ChartFragment()).commit();
        }
        setContentView(R.layout.activity_main);
    }


}
