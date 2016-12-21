package com.caicai.testchart;

import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by cj on 2016/9/19.
 * 寻峰方法：在谱漂小于10道，允许±1道误差时，利用对称面积法寻峰，准确度高，更容易计算峰面积，需要提前知道波峰大概位置
 */
public class SearchActivity extends Activity {
    public String path1;
    protected String base_path = "/storage/sdcard1/Android/mydata/test.txt";
    int peakTop = 0;//波峰道指
    int peakLeft;
    int peakRight;
    int peakRange;
    int area, areaTotal, areaBackground;
    int[] y_value;//原始数据
    int[] y_value_compute;//计算后的数据
    int smooth;
    int base_num;
    static final String[] smooth_str = new String[]{"", "五点平均法", "重力平均法", "最小二乘法", "十五点平均法"};
    static final int[] range = {0, 20, 50, 100};
    int[] transfer_result = new int[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        showUserSettings();
        Intent intent = getIntent();
        y_value = intent.getIntArrayExtra("rawValue");
    }

    private void showUserSettings() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            peakRange = range[Integer.parseInt(sharedPrefs.getString("range_num", "NULL"))];
            base_num = Integer.parseInt(sharedPrefs.getString("base_num", "NULL"));
            smooth = Integer.parseInt(sharedPrefs.getString("smooth", "NULL"));
        } catch (Exception e) {

        }
        StringBuilder builder = new StringBuilder();
        builder.append("\n 谱线平滑方式：  "
                + smooth_str[smooth] + "\n");
        builder.append("\n 寻峰基值：  "
                + base_num + "\n");
        builder.append("\n 寻峰范围：  "
                + peakRange + "\n");
        TextView tv_setting = (TextView) findViewById(R.id.tv_setting);
        tv_setting.setText(builder.toString());
    }

    public void ok(View source) {
        chooseSpectrum();
        searchTop(base_num, y_value_compute, peakRange);
        searchArea(peakTop, peakRange, y_value_compute);
        showComputeResult();
    }

    public void back(View source) {
        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putIntArray("transfer", transfer_result);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void showComputeResult() {
        StringBuilder strBer = new StringBuilder();
        strBer.append("\n 波峰对应道指：  "
                + peakTop + "\n");
        strBer.append("\n 波形左边界对应道指：  "
                + peakLeft + "\n");
        strBer.append("\n 波形有边界对应道指：  "
                + peakRight + "\n");
        strBer.append("\n 特征峰面积：  "
                + area + "\n");
        strBer.append("\n 特征峰本底面积：  "
                + areaTotal + "\n");
        strBer.append("\n 特征峰底面积：  "
                + areaBackground + "\n");
        TextView tv_result = (TextView) findViewById(R.id.tv_result);
        tv_result.setText(strBer.toString());
    }

    public void searchTop(int base, int[] y_value, int range) {
        int[] pp = new int[2 * range];//y_value 一阶导数数组,没有初始化
        int[] p = new int[2 * range];//用来存放k值，零点的道指位置
        int jj = 0;//保存零点数组的序列号
        for (int j = 0; j < 2 * range; j++) {
            int media = j + base - range + 1;
            pp[j] = (22 * (y_value[media - 3] - y_value[media + 3]) - 67 * (y_value[media - 2] - y_value[media + 2]) - 58 * (y_value[media - 1] - y_value[media + 1])) / 252;
        }//区间内的一阶导数
        for (int k = 3; k < 2 * range - 3; k++) {
            if (pp[k - 3] > 0 && pp[k - 2] > 0 && pp[k - 1] > 0 && pp[k + 1] < 0 && pp[k + 2] < 0 && pp[k + 3] < 0) {//一阶导数由负到正
                //此处应该得到k的值 pp里面对应的k值 是零点，零点不一定一个，想个办法把所有的k都保存起来
                p[jj] = base - range + 1 + k;
                jj++;
            }//储存的都是零点所在道指
            for (int l = 0; l < 2 * range; l++) {
                if (y_value[peakTop] < y_value[p[l]]) {
                    peakTop = p[l];//道指
                }
            }
        }
    }

    public void searchArea(int peakTop, int peakRange, int[] y_value) {
        peakLeft = peakRight = peakTop;
        for (int i = peakTop - peakRange; i < peakTop; i++) {
            if (y_value[i] < y_value[peakLeft])
                peakLeft = i;
        }
        for (int i = peakTop; i < peakRange + peakTop; i++) {
            if (y_value[i] < y_value[peakRight])
                peakRight = i;
        }
        if (peakRight != peakTop) {
            calculate_Peak_Area(peakLeft, peakRight, y_value);
        }

    }

    public void calculate_Peak_Area(int L, int R, int[] y_value) {
        areaTotal = 0;
        areaBackground = 0;
        areaBackground = (y_value[L] + y_value[R]) * (R - L + 1) / 2;
        for (int i = L; i <= R; i++) {
            areaTotal = y_value[i] + areaTotal;
        }
        area = areaTotal - areaBackground;
    }

    private int[] smoothSpectrum_average(int[] ints) {
        for (int i = 2; i < ints.length - 2; i++) {
            ints[i] = (ints[i - 2] + ints[i - 1] + ints[i] + ints[i + 1] + ints[i + 2]) / 5;
        }
        return ints;
    }//1

    private int[] smoothSpectrum_gravity(int[] ints) {
        for (int i = 2; i < ints.length - 2; i++) {
            ints[i] = (ints[i - 2] + 4 * ints[i - 1] + 6 * ints[i] + 4 * ints[i + 1] + ints[i + 2]) / 16;
        }
        return ints;
    }//2

    private int[] smoothSpectrum_least_square(int[] ints) {
        for (int i = 2; i < ints.length - 2; i++) {
            ints[i] = ((-3) * ints[i - 2] + 12 * ints[i - 1] + 17 * ints[i] + 12 * ints[i + 1] + (-3) * ints[i + 2]) / 35;
        }
        return ints;
    }//3

    private int[] smoothSpectrum_15point(int[] ints) {
        for (int i = 7; i < ints.length - 7; i++) {
            ints[i] = ((-78) * ints[i - 7] - 13 * ints[i - 6] + 42 * ints[i - 5] + 87 * ints[i - 4] + 122 * ints[i - 3] + 147 * ints[i - 2] + 162 * ints[i - 1] + 167 * ints[i] + 162 * ints[i + 1] + 147 * ints[i + 2] + 122 * ints[i + 3] + 87 * ints[i + 4] + 42 * ints[i + 5] - 13 * ints[i + 6] - 78 * ints[i + 7]) / 1105;
        }
        return ints;
    }//4

    private void chooseSpectrum() {
        switch (smooth) {
            case 1:
                y_value_compute = smoothSpectrum_average(y_value);
                break;
            case 2:
                y_value_compute = smoothSpectrum_gravity(y_value);
                break;
            case 3:
                y_value_compute = smoothSpectrum_least_square(y_value);
                break;
            case 4:
                y_value_compute = smoothSpectrum_15point(y_value);
                break;
            default:
                break;
        }
    }

}
