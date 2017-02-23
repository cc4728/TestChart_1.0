package com.caicai.testchart;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by cai on 2017/2/14.
 * 处理波形显示界面
 * 更新fragment重新提交一次就能实现更新view
 */

public class ChartFragment extends Fragment {
    private int[] value = new int[3051];
    private int vertexNum = value.length;
    private LineChartView chart;
    private LineChartData data;
    public static int flag;

    public ChartFragment() {
        Log.e("caicai", "chart构造方法");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);
        //从container中获取layou_width 和layout_height组成的LayoutParams，如果false，则会对加载的视图加载参数，
        //如果是true，我们将加载视图作为子视图增添到container中
        chart = (LineChartView) rootView.findViewById(R.id.chart);// 强制类型转换，View类转换为LineChartView类
        // 从外部导入数据
        // 在内部加工成显示数据
        Log.e("caicai", "创建视图");
        initData();
        Log.e("caicai", "完成数据初始化");
        generateData();
        Log.e("caicai", "完成图表数据转化");
        chart.setViewportCalculationEnabled(false);
        resetViewport();
        return rootView;
    }

    public void initData() {
        //打开文件的时候显示原始图，设置一个flag=0，标记，清空数组afterSmooth；
        //数据处理时，显示平滑后的图形，flag=1，afterSmooth不为空；
        Log.e("caicai", "chart.initData");
        DataProvide dp = new DataProvide();
        if (flag == 0 && DataProvide.rawValue != null) {
            Log.e("caicai", "原始图形");
            dp.setDefaultData();
            Log.e("caicai", "initData.setdefaulta");
            System.arraycopy(DataProvide.rawValue, 0, value, 0, 3051);
            Log.e("caicai", "复制完成数组");
        }
        if (flag == 1 && DataProvide.afterSmooth != null) {
            Log.e("caicai", "平滑后的图形");
            System.arraycopy(DataProvide.afterSmooth, 0, value, 0, 3051);

        }
    }

    private void generateData() {
        Log.e("caicai", "chart.generateData");
        int[] temp = value;

        List<Line> lines = new ArrayList<Line>();//初始化一个集合，存放不同线


        List<PointValue> values = new ArrayList<PointValue>();//存放主线
        for (int i = 0; i < vertexNum; i++) {
            values.add(new PointValue(i, temp[i]));
        }
        Line line = new Line(values);//将一维数组传入组成一条线，应用了线的构造方法
        Log.e("caicai","创建line");
        line.setColor(Color.parseColor("#ADADAD"));//设置line的颜色
        line.setShape(ValueShape.CIRCLE);//已经在前面赋值了
        line.setCubic(false);//设置line的一个画法
        line.setFilled(false);
        line.setHasLabels(false);
        line.setHasLabelsOnlyForSelected(false);
        line.setHasLines(true);
        line.setHasPoints(false);
        line.setPointColor(ChartUtils.COLORS[(2) % ChartUtils.COLORS.length]);
        lines.add(line);//将这条线添加到线集合


        for (int count = 0;DataProvide.reignOfInteresting[count] != 0 && count < 100;  count+=2) {
            Log.e("caicai","创建兴趣区图形");
            int left=DataProvide.reignOfInteresting[count];
            int right=DataProvide.reignOfInteresting[count+1];
            List<PointValue> valueOfROI = new ArrayList<>();
            for (int i=left;i<=right;i++) {
                valueOfROI.add(new PointValue(i,temp[i]));
            }
            Line lineOfROi=new Line(valueOfROI);
            lineOfROi.setFilled(true);
            lineOfROi.setHasLines(DataProvide.isHightLight);
            lineOfROi.setHasLabels(false);
            lineOfROi.setHasPoints(false);
            lineOfROi.setCubic(false);
            if (DataProvide.isHightLight) {
                Log.e("caicai","高亮显示");
            }
            lineOfROi.setColor(ChartUtils.COLOR_RED);
            lines.add(lineOfROi);
        }


        setAxis(lines);//设置坐标参数
    }

    private void setAxis(List<Line> lines) {
        data = new LineChartData(lines);//
        Axis axisX = new Axis().setHasLines(true);//新建一个x坐标
        Axis axisY = new Axis().setHasLines(true);//新建y轴，传入线
        axisX.setName("  道            指  ");//坐标值名称
        axisX.setTextColor(ChartUtils.COLOR_BLACK);
        axisY.setTextColor(ChartUtils.COLOR_BLACK);
        axisX.setLineColor(Color.parseColor("#D3D3D3"));
        axisY.setLineColor(Color.parseColor("#D3D3D3"));
        axisY.setName("  计   数   率 （cps） ");
        data.setAxisXBottom(axisX);//给数据这个对象设置下方，传入参数x轴
        data.setAxisYLeft(axisY);//传入参数y轴
        data.setBaseValue(Float.NEGATIVE_INFINITY);//数据的一个方法，传入的是一个固定值
        data.setValueLabelBackgroundEnabled(false);
        data.setValueLabelTypeface(Typeface.MONOSPACE);
        data.setValueLabelsTextColor(ChartUtils.COLOR_BLACK);
        chart.setLineChartData(data);//chart的一个方法，传入的是数据
    }


    private void resetViewport() {
        Log.e("caicai", "chart.resetView");
        final Viewport v = new Viewport(chart.getMaximumViewport());
        int minValue = value[0];
        int maxValue = value[0];
        for (int i = 0; i < vertexNum; i++) {
            if (minValue > value[i])
                minValue = value[i];
            if (maxValue < value[i])
                maxValue = value[i];
        }
        v.bottom = minValue - 10;
        v.top = maxValue + 10;//y轴最大坐标值
        v.left = 0;//只能显示七个点，12-5，
        v.right = vertexNum + 10;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }


    //高亮显示一个感兴趣区，短按
    private void HighLightShow(int start, int end) {
        int temp[] = new int[end - start + 1];


    }

    //低亮显示所有设置感兴趣区
    private void LowLightShow(int[] select) {

    }
}
