package com.caicai.testchart;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cai on 2017/2/14.
 * 给全局提供各种类型的数据
 */

public class DataProvide {
    public static int [] rawValue=new int[3051];
    public static ArrayList<Integer> peekTop=null;
    public static int [] afterSmooth=new int[3051];
    public static String MyPath;
    public final static String DafPath ="/storage/sdcard1/Android/mydata/test.txt";
    public static int smoothTimes,moveValue,byHandBase,byHandRange,smoothStyle;
    public final static String[]SMOOTHSTYLE={"","五点平滑","十五点平滑","重力法平滑","最小二乘平滑"};
    public static boolean reduce,byHand;
    public DataProvide() {

    }
    public  void setDefaultData() {
        getPath(MyPath);//初始化原始数据
        peekTop=null;//初始化峰值数组
        smoothStyle=0;
        smoothTimes=0;
        moveValue=0;
        reduce=false;
        byHand=false;

    }


      //文件读取相关操作
    public int[] getData(String string) {
        File file = new File(string);
        StringBuffer contents = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String text = null;
            while ((text = reader.readLine()) != null) {
                contents.append(text);//逐行全部读取，添加到contents
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String data_string = contents.toString();//混合字符串
        String[] dataArray = data_string.split("\\s+");//以空格键为分隔符，放入字符串数组
        List<String> listx = new ArrayList<String>();
        List<String> listy = new ArrayList<String>();
        for (int i = 0; i < dataArray.length; i++) {
            if (i % 2 == 0) {
                listy.add(dataArray[i]);
            } else {
                listx.add(dataArray[i]);
            }
        }//将x，y坐标存放在ArrayList里面
        //将list转化为字符串数组，得到最终结果
        String[] x = new String[listx.size()];
        x = listx.toArray(x);//x坐标字符串数组
        String[] y = new String[listy.size()];
        y = listy.toArray(y);//y坐标字符串数组
        //去掉空格
        List<String> tmp = new ArrayList<String>();
        for (String str : y) {
            if (str != null && str.length() != 0) {
                tmp.add(str);
            }
        }
        y = tmp.toArray(new String[0]);

        rawValue = StringToInt(y);//转为整数数组，终极结果，哈哈，成功
        return rawValue;
    }

    public int[] StringToInt(String[] arrs) {
        int[] ints = new int[arrs.length];
        for (int i = 0; i < arrs.length; i++) {
            ints[i] = Integer.parseInt(arrs[i]);
        }
        return ints;
    }

    public  void getPath(String path) {

        if (path == null) {
            rawValue = getData(DafPath);
        } else {
            rawValue = getData(MyPath);
        }
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
                int j=0;
                peekTop.add(ints[i]);
            }
        }
    }

}
