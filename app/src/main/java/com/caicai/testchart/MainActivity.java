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
            getFragmentManager().beginTransaction().add(R.id.container,new ChartFragment()).commit();
        }
        setContentView(R.layout.activity_main);
    }



    /*




   <fragment
        android:id="@+id/container"
        android:layout_below="@+id/title"
        android:layout_alignParentRight="true"
        android:layout_toRightOf="@+id/left"
        android:name="com.caicai.testchart.ChartFragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

            *//*
            处理背景扣除
             *//*
                if (reduce_index == 1) {
                    result_back = reduceBackground(result_back);
                }
            *//*
            谱线漂移
             *//*

                if (move_int != 0) {
                    result_back = moveSpectrum(move_int, result_back);
                }
            *//*
            处理手动寻峰
             *//*
            }else {
                //默认4次15点平滑，自动全谱寻峰，求导
                    result_back = smoothSpectrum_15point(y_value);
              for (int i=0;i<3;i++){
                    result_back=smoothSpectrum_15point(result_back);
                }
                   result_back=reduceBackground(result_back);
              //  peak_list = searchPeak(result_back);
                searchPeak(result_back);
                Log.e("caicai", String.valueOf(peak_list));
            }

            *//*
            处理自动寻峰
             *//*



            for (int j = 0; j < numberOfPoints; j++) {
                randomNumbersTab[j] = result_back[j];//赋值给Tab作图 默认调用这个方法
            }
            //找出value最值
            maxValue = minValue = result_back[0];
            maxValue_x = 0;//必须要先赋值，才能算
            for (int i = 0; i < result_back.length; i++) {
                if (result_back[i] > maxValue)
                    maxValue = result_back[i];//y坐标
                if (maxValue == result_back[i])
                    maxValue_x = i;//如果这样算maxValue 会一直被i循环取代，
                if (result_back[i] < minValue)
                    minValue = result_back[i];
            }
        }//获得y_value[],max_value

        public void reset() {
            numberOfLines = 2;
            //f=0;
            hasAxes = true;
            hasAxesNames = true;
            hasLines = true;
            hasPoints = false;
            shape = ValueShape.CIRCLE;
            isFilled = false;
            hasLabels = false;
            isCubic = false;
            hasLabelForSelected = false;
            pointsHaveDifferentColor = false;
            generateValues();
            generateData();
            chart.setValueSelectionEnabled(hasLabelForSelected);
            resetViewport();
        }

        private void resetViewport() { //坐标值点已经确定了，但是不一定能看到，只有通过这个窗口才能看见
            // Reset viewport height range to (0,100)
            final Viewport v = new Viewport(chart.getMaximumViewport());
            v.bottom = minValue;
            v.top = 1520;//y轴最大坐标值
            v.left = 0;//只能显示七个点，12-5，
            v.right = (float) (numberOfPoints);
            chart.setMaximumViewport(v);
            chart.setCurrentViewport(v);
        }

        private void generateData() {

            String[] label = new String[3251];
            label[1086] = "FeKα";
            label[796] = "FeKb";
            label[744]="MnKa";
            label[1582]="MnKb";

            List<Line> lines = new ArrayList<Line>();//线条堆积，当前就一个线条
            List<PointValue> values = new ArrayList<PointValue>();//点堆积
            for (int j = 0; j < numberOfPoints; ++j) {
                values.add(new PointValue(j, randomNumbersTab[j]));//将y值的一维数组添加到values例
            }
            Line line = new Line(values);//将一维数组传入组成一条线，应用了线的构造方法
            line.setColor(ChartUtils.COLOR_RED);//设置line的颜色
            line.setShape(shape);//已经在前面赋值了
            line.setCubic(isCubic);//设置line的一个画法
            line.setFilled(false);
            line.setHasLabels(false);
            line.setHasLabelsOnlyForSelected(false);
            line.setHasLines(true);
            line.setHasPoints(false);
            if (pointsHaveDifferentColor) {
                line.setPointColor(ChartUtils.COLORS[(2) % ChartUtils.COLORS.length]);
            }//执行设置颜色
            lines.add(line);//将这条线添加到线集合

            //新建一个线条，显示寻峰结果，设置阈值，自动寻峰
            List<PointValue> pointPeak = new ArrayList<PointValue>();//点堆积
            for (int i=0;i<peak_list.size();i++){
                int k= peak_list.get(i);
                pointPeak.add(new PointValue(k,randomNumbersTab[k]).setLabel(""+label[k]));
            }
            Line peakLine = new Line(pointPeak);
            peakLine.setColor(ChartUtils.COLOR_RED);
            peakLine.setShape(shape);
            peakLine.setCubic(isCubic);
            peakLine.setFilled(false);
            peakLine.setHasLines(false);
            peakLine.setHasPoints(true);
            peakLine.setHasLabels(true);
            peakLine.setPointColor(ChartUtils.COLOR_BLUE);
            lines.add(peakLine);





        }



            }
        }

        */

    /**
     * To animate values you have to change targets values and then call {@link Chart#startDataAnimation()}
     * method(don't confuse with View.animate()). If you operate on data that was set before you don't have to call
     * {@link LineChartView#setLineChartData(LineChartData)} again.
     *//*

        private class ValueTouchListener implements LineChartOnValueSelectListener {

            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                makeText(getActivity(), "Selected: " + value, LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {
                // TODO Auto-generated method stub

            }

        }
    }*/


/*    private int[] smoothSpectrum_average(int[] ints) {
        for (int i = 2; i < ints.length - 2; i++) {
            ints[i] = (-3 * ints[i - 2] + 12 * ints[i - 1] + 17 * ints[i] + 12 * ints[i + 1] - 3 * ints[i + 2]) / 35;
        }
        return ints;
    }//五点算数滑动平均

    private int[] smoothSpectrum_gravity(int[] ints) {
        for (int i = 2; i < ints.length - 2; i++) {
            ints[i] = (ints[i - 2] + 4 * ints[i - 1] + 6 * ints[i] + 4 * ints[i + 1] + ints[i + 2]) / 16;
        }
        return ints;
    }

    private int[] smoothSpectrum_least_square(int[] ints) {
        for (int i = 2; i < ints.length - 2; i++) {
            ints[i] = ((-3) * ints[i - 2] + 12 * ints[i - 1] + 17 * ints[i] + 12 * ints[i + 1] + (-3) * ints[i + 2]) / 35;
        }
        return ints;
    }

    private int[] smoothSpectrum_15point(int[] ints) {
        for (int i = 7; i < ints.length - 7; i++) {
            ints[i] = ((-78) * ints[i - 7] - 13 * ints[i - 6] + 42 * ints[i - 5] + 87 * ints[i - 4] + 122 * ints[i - 3] + 147 * ints[i - 2] + 162 * ints[i - 1] + 167 * ints[i] + 162 * ints[i + 1] + 147 * ints[i + 2] + 122 * ints[i + 3] + 87 * ints[i + 4] + 42 * ints[i + 5] - 13 * ints[i + 6] - 78 * ints[i + 7]) / 1105;
        }
        return ints;
    }//15点平滑

    private int[] reduceBackground(int[] ints) {
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

    private int[] moveSpectrum(int move_int, int[] ints) {
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
            ints[j] = (22 * (y_value[j - 3] - y_value[j + 3]) - 67 * (y_value[j - 2] - y_value[j + 2]) - 58 * (y_value[j - 1] - y_value[j + 1])) / 252;
        }
        return ints;
    }//5点对应求导公式

    private void searchPeak(int[] ints) {
        for (int i = 7; i < ints.length - 2; i++) {
            if (ints[i] > 80 && ints[i] > ints[i + 1] && ints[i] > ints[i + 2] && ints[i] > ints[i - 1] && ints[i] > ints[i - 2]) {
                peak_list.add(i);
            }
        }
    }*/

    /*
    private int searchTop(int base,int rang,int[] ints) {
        int i = input;//i为 y_value数组序列号
        int[] pp = new int[y_value.length];//y_value 一阶导数数组,没有初始化
        int[] p = new int[100];//用来存放k值，零点的道指位置
        int jj = 0;//保存零点数组的序列号
        for (int j = i - 50; j < i + 50; j++) {
            pp[j] = (22 * (y_value[j - 3] - y_value[j + 3]) - 67 * (y_value[j - 2] - y_value[j + 2]) - 58 * (y_value[j - 1] - y_value[j + 1])) / 252;
        }//一阶导数
        for (int k = i - 50; k < i + 50; k++) {
            if (pp[k - 3] > 0 && pp[k - 2] > 0 && pp[k - 1] > 0 && pp[k + 1] < 0 && pp[k + 2] < 0 && pp[k + 3] < 0) {//一阶导数由负到正
                //此处应该得到k的值 pp里面对应的k值 是零点，零点不一定一个，想个办法把所有的k都保存起来
                p[jj] = k;
                jj++;
            }
            for (int l = 0; l < jj; l++) {
                if (y_value[peak] < y_value[p[l]]) {
                    peak = p[l];//道指
                }
            }
        }
        return peak;
    }//由一个输入量f，左右50道指区间内对应的波峰
    //
    */
/*
    public int[] getY_value() {
        return y_value;
    }

    public void setY_value(int[] y_value) {
        this.y_value = y_value;
    }*/
/*

    }*/
}
