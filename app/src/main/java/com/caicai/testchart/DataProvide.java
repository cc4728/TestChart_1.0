package com.caicai.testchart;

import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.jar.Manifest;

import lecho.lib.hellocharts.model.Line;

/**
 * Created by cai on 2017/2/14.
 * 给全局提供各种类型的数据
 */

public class DataProvide {
    public final static int NUMBERPOINT=2048;
    public static int[] rawValue = new int[NUMBERPOINT];
    public static ArrayList<Integer> peekTop = null;
    public static int[] afterSmooth = new int[NUMBERPOINT];
    public static int[] reignOfInteresting = new int[100];//最多支持50个感兴趣点设置
    public static String MyPath;
    public final static String DafPath = "/storage/sdcard1/XRF/2017.2.23.txt";
    public static int smoothTimes, moveValue, byHandBase, byHandRange, smoothStyle;
    public final static String[] SMOOTHSTYLE = {"", "五点平滑", "十五点平滑", "重力法平滑", "最小二乘平滑"};
    public static boolean reduce;
    public static int addFile = 0;//标记加载文件
    public final static String FILEKEYWORD="<<DATA>>";

    public DataProvide() {

    }

    public void setDefaultData() {
        Log.e("caicai", "setdefaulta1");
        if (addFile == 0) {
            rawValue = getData(DafPath);
            addFile = -1;
        }
        if (addFile == 1) {
            rawValue = getData(MyPath);
            addFile = -1;
        }
        Log.e("caicai", "setdefaulta2");
        peekTop = null;//初始化峰值数组
        smoothStyle = 0;
        smoothTimes = 0;
        moveValue = 0;
        reduce = false;
        Log.e("caicai", "setdefaulta3");

        Log.e("caicai", "setdefaulta4");
    }


    //文件读取相关操作
    //InputStreamReader read = new InputStreamReader(new FileInputStream(f),"GBK");
    public int[] getData(String string) {
        //测试inputStreamReader
        int[] arrayNum = new int[NUMBERPOINT];
        String[]totalString=new String[NUMBERPOINT+80];
        int lineNumber=1;
        String lineString=null;
        int fileKeyLine=-1;
        BufferedReader bufferedReader=null;
        try {
             bufferedReader=new BufferedReader(new FileReader(string));
            lineString=bufferedReader.readLine();
            while (lineString != null) {
                lineNumber++;
                lineString=bufferedReader.readLine();
                if (lineString.equals(FILEKEYWORD))
                {
                    fileKeyLine=lineNumber+1;
                }
                totalString[lineNumber]=lineString;
                if (lineString.equals("<<END>>")) {
                    break;
                }
            }
            Log.e("caicai", String.valueOf(fileKeyLine));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        int k=fileKeyLine+NUMBERPOINT;
        for (int i=fileKeyLine;i<k;i++) {
            arrayNum[i-fileKeyLine]=Integer.parseInt(totalString[i]);
        }


        return arrayNum;
    }

    //数据处理相关操作
    public int[] smoothSpectrum_average(int[] ints) {
        for (int i = 2; i < ints.length - 2; i++) {
            ints[i] = (-3 * ints[i - 2] + 12 * ints[i - 1] + 17 * ints[i] + 12 * ints[i + 1] - 3 * ints[i + 2]) / 35;
        }
        return ints;
    }//五点算数滑动平均

    public int[] smoothSpectrum_gravity(int[] ints) {
        for (int i = 2; i < ints.length - 2; i++) {
            ints[i] = (ints[i - 2] + 4 * ints[i - 1] + 6 * ints[i] + 4 * ints[i + 1] + ints[i + 2]) / 16;
        }
        return ints;
    }

    public int[] smoothSpectrum_least_square(int[] ints) {
        for (int i = 2; i < ints.length - 2; i++) {
            ints[i] = ((-3) * ints[i - 2] + 12 * ints[i - 1] + 17 * ints[i] + 12 * ints[i + 1] + (-3) * ints[i + 2]) / 35;
        }
        return ints;
    }

    public int[] smoothSpectrum_15point(int[] ints) {
        for (int i = 7; i < ints.length - 7; i++) {
            ints[i] = ((-78) * ints[i - 7] - 13 * ints[i - 6] + 42 * ints[i - 5] + 87 * ints[i - 4] + 122 * ints[i - 3] + 147 * ints[i - 2] + 162 * ints[i - 1] + 167 * ints[i] + 162 * ints[i + 1] + 147 * ints[i + 2] + 122 * ints[i + 3] + 87 * ints[i + 4] + 42 * ints[i + 5] - 13 * ints[i + 6] - 78 * ints[i + 7]) / 1105;
        }
        return ints;
    }//15点平滑

    public int[] reduceBackground(int[] ints) {
        int[] k = new int[ints.length];
        for (int i = 0; i < ints.length; i++) {
            k[i] = (int) Math.log(ints[i] + 1);
        }
        for (int j = 0; j < 10; j++) {
            for (int i = 1; i < ints.length - 1; i++) {
                if (k[i] > (k[i + 1] + k[i - 1]) / 2) {
                    k[i] = (k[i + 1] + k[i - 1]) / 2;
                }
            }
        }
        for (int m = 0; m < ints.length; m++) {
            ints[m] = ints[m] - k[m];
        }
        return ints;
    }//直线法扣除本底

    public int[] moveSpectrum(int move_int, int[] ints) {
        int i;
        int[] media = new int[ints.length];
        if (move_int < 0) {//左移动
            for (i = 0; i < ints.length; i++) {
                if (i < (ints.length + move_int)) {
                    media[i] = ints[i - move_int];
                    // Log.e("caicai", "i="+i+"    数组值："+String.valueOf(result_back[i]));
                }
                if (i >= (ints.length + move_int)) {
                    media[i] = 0;
                    // Log.e("caicai", "i="+i+"    数组值："+String.valueOf(result_back[i]));
                }
            }
        }
        if (move_int > 0) {//右移动
            for (i = 0; i < ints.length; i++) {
                if (i < move_int) {
                    media[i] = 0;
                    // Log.e("caicai", "i="+i+"    数组值："+String.valueOf(result_back[i]));

                }
                if (i >= move_int) {
                    media[i] = ints[i - move_int];
                    //  Log.e("caicai", "i="+i+"    数组值："+String.valueOf(result_back[i]));
                }
            }
        }
        return media;
    }

    private int[] derivative_15(int[] ints) {
        for (int i = 7; i < ints.length - 7; i++) {
            ints[i] = (12922 * (ints[i - 7] - ints[i + 7]) + 4121 * (ints[i + 6] - ints[i - 6]) + 14150 * (ints[i + 5] - ints[i - 5]) + 18334 * (ints[i + 4] - ints[i - 4]) + 17842 * (ints[i + 3] - ints[i - 3]) + 13843 * (ints[i + 2] - ints[i - 2]) + 7506 * (ints[i + 1] - ints[i - 1])) / 47736;
        }
        return ints;
    }//15点对应的导数公式

    private int[] derivative_5(int[] ints) {
        for (int j = 3; j < ints.length - 3; j++) {
            ints[j] = (22 * (ints[j - 3] - ints[j + 3]) - 67 * (ints[j - 2] - ints[j + 2]) - 58 * (ints[j - 1] - ints[j + 1])) / 252;
        }
        return ints;
    }//5点对应求导公式

    private void searchPeak(int[] ints) {
        for (int i = 7; i < ints.length - 2; i++) {
            if (ints[i] > 80 && ints[i] > ints[i + 1] && ints[i] > ints[i + 2] && ints[i] > ints[i - 1] && ints[i] > ints[i - 2]) {
                int j = 0;
                peekTop.add(ints[i]);
            }
        }
    }

}
