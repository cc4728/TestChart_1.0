package com.caicai.testchart;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.LineChartView;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;


public class MainActivity extends AppCompatActivity {
    static final String AFTER_SMOOTH = "y_value";//标签
    public int[] y_value;//y值，因变量
    private String path1;
    protected String base_path = "/storage/sdcard1/Android/mydata/test.txt";
    int peak = 0;//寻峰道指
    float f = 0;
    int peakTop, peakLeft, peakRight, area, areaTotal, areaBackground;
    int[] result_back = new int[3051];
    List<Integer> peak_list = new ArrayList<Integer>();
    private PopupWindow popupWindow;
    int smooth_index, reduce_index, smooth_times_int = 1, base_num_int=0, range_int, move_int;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        path1 = path;
        getPath(path1);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();//为他设置一个容器
        }
    }

    //A fragment containing a line chart.
    public class PlaceholderFragment extends Fragment implements View.OnClickListener {
        private LineChartView chart;
        private LineChartData data;
        private int numberOfLines = 2;
        private int maxNumberOfLines = 2;
        private int numberOfPoints = 3251;
        private int maxValue;
        private int maxValue_x;
        private int minValue;
        private float maxLable;
        private float minLable;
        float[] randomNumbersTab = new float[numberOfPoints];


        private boolean hasAxes = true;
        private boolean hasAxesNames = true;
        private boolean hasLines = true;
        private boolean hasPoints = false;
        private ValueShape shape = ValueShape.CIRCLE;
        private boolean isFilled = false;
        private boolean hasLabels = false;
        private boolean isCubic = false;
        private boolean hasLabelForSelected = false;
        private boolean pointsHaveDifferentColor;


        public PlaceholderFragment() {
        }//无参构造方法

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {//LayoutInflater inflater寻找layout下的xml布局文件
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);//从container中获取layou_width 和layout_height组成的LayoutParams，如果false，则会对加载的视图加载参数，
            //如果是true，我们将加载视图作为子视图增添到container中
            chart = (LineChartView) rootView.findViewById(R.id.chart);//强制类型转换，View类转换为LineChartView类
            chart.setOnValueTouchListener(new ValueTouchListener());
            // 产生数据
            generateValues();
            generateData();
            // Disable viewport recalculations, see toggleCubic() method for more info.
            chart.setViewportCalculationEnabled(false);
            resetViewport();
            return rootView;
        }
        // MENU
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.line_chart, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_reset) {//重置
                reset();
                generateValues();
                generateData();
                return true;
            }



            if (id == R.id.Spectrum_data_process) {//谱数据处理
                showSetParmWindow();
                //  Toast.makeText(MainActivity.this,"提取输入的数是"+y_value.toString(),Toast.LENGTH_LONG).show();
                // Intent intent1 = new Intent(MainActivity.this, SearchActivity.class);
                //   Bundle bundle = new Bundle();
                //  bundle.putIntArray("rawValue", y_value);
                //  intent1.putExtras(bundle);
                //  startActivity(intent1);
                // reset();
                // inputTitleDialog();
                // if (f!=0){
                //     searchTop(f);
                //     generateData();
                //  }else {
                //      Toast.makeText(getActivity(),"寻峰结果有问题",Toast.LENGTH_SHORT).show();

                // }
                //  return true;
            }

            if (id == R.id.open_file) {//打开文件
                Intent intent = new Intent(MainActivity.this, OpenActivity.class);
                startActivity(intent);
                generateData();
                return true;
            }


            if (id == R.id.show_result) {//popwindow 显示结果
                showResultWindow();
            }

            if (id == R.id.system_setting) { //系统设置
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                return true;
            }
            if (id == R.id.data_base) { //核素库管理
                Intent intent = new Intent(MainActivity.this, DataBaseActivity.class);
                startActivity(intent);
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        private void generateValues() {//已知y_value,要求出各种变量，并且默认一个赋值，保持已知量不变
            /*
            平滑次数处理
            平滑方式选择
             */
            if(base_num_int!=0) {
                for (int i = 0; i < smooth_times_int; i++) {
                    switch (smooth_index) {
                        case 0:
                            result_back = smoothSpectrum_average(y_value);
                            break;
                        case 1:
                            result_back = smoothSpectrum_15point(y_value);
                            break;
                        case 2:
                            result_back = smoothSpectrum_gravity(y_value);
                            break;
                        case 3:
                            result_back = smoothSpectrum_least_square(y_value);
                            break;
                    }
                }

            /*
            处理背景扣除
             */
                if (reduce_index == 1) {
                    result_back = reduceBackground(result_back);
                }
            /*
            谱线漂移
             */

                if (move_int != 0) {
                    result_back = moveSpectrum(move_int, result_back);
                }
            /*
            处理手动寻峰
             */
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

            /*
            处理自动寻峰
             */



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




            data = new LineChartData(lines);//

            Axis axisX = new Axis().setHasLines(true);//新建一个x坐标
            Axis axisY = new Axis().setHasLines(true);//新建y轴，传入线
            axisX.setName("  道            指  ");//坐标值名称
            axisX.setTextColor(ChartUtils.COLOR_BLACK);
            axisY.setTextColor(ChartUtils.COLOR_BLACK);
            axisX.setLineColor(ChartUtils.COLOR_BLACK);
            axisY.setLineColor(ChartUtils.COLOR_BLACK);
            axisY.setName("  计   数   率 （cps） ");
            data.setAxisXBottom(axisX);//给数据这个对象设置下方，传入参数x轴
            data.setAxisYLeft(axisY);//传入参数y轴
            data.setBaseValue(Float.NEGATIVE_INFINITY);//数据的一个方法，传入的是一个固定值
            data.setValueLabelBackgroundEnabled(false);
            data.setValueLabelTypeface(Typeface.MONOSPACE);
            data.setValueLabelsTextColor(ChartUtils.COLOR_BLACK);
            chart.setLineChartData(data);//chart的一个方法，传入的是数据
        }


        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.move_value: {
                    Toast.makeText(getActivity(), "clicked computer", Toast.LENGTH_SHORT).show();
                    popupWindow.dismiss();
                }
                break;

            }
        }

        /**
         * To animate values you have to change targets values and then call {@link Chart#startDataAnimation()}
         * method(don't confuse with View.animate()). If you operate on data that was set before you don't have to call
         * {@link LineChartView#setLineChartData(LineChartData)} again.
         */

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
    }

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

        y_value = StringToInt(y);//转为整数数组，终极结果，哈哈，成功
        return y_value;
    }

    public int[] StringToInt(String[] arrs) {
        int[] ints = new int[arrs.length];
        for (int i = 0; i < arrs.length; i++) {
            ints[i] = Integer.parseInt(arrs[i]);
        }
        return ints;
    }

    public void getPath(String path) {

        if (path == null) {
            y_value = getData(base_path);
        } else {
            y_value = getData(path);
        }
    }

    private int[] smoothSpectrum_average(int[] ints) {
        for (int i = 2; i < ints.length - 2; i++) {
            ints[i] = (-3*ints[i - 2] + 12*ints[i - 1] +17* ints[i] + 12*ints[i + 1] -3* ints[i + 2]) /35;
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

    private int[] derivative_15(int[]ints){
        for(int i=7;i<ints.length-7;i++){
            ints[i]=(12922*(ints[i-7]-ints[i+7])+4121*(ints[i+6]-ints[i-6])+14150*(ints[i+5]-ints[i-5])+18334*(ints[i+4]-ints[i-4])+17842*(ints[i+3]-ints[i-3])+13843*(ints[i+2]-ints[i-2])+7506*(ints[i+1]-ints[i-1]))/47736;
        }
        return ints;
    }//15点对应的导数公式

    private int[]derivative_5(int[]ints){
        for (int j=3;j<ints.length-3;j++){
        ints[j] = (22 * (y_value[j - 3] - y_value[j + 3]) - 67 * (y_value[j - 2] - y_value[j + 2]) - 58 * (y_value[j - 1] - y_value[j + 1])) / 252;
        }
        return ints;
    }//5点对应求导公式

    private void searchPeak(int[]ints){
        for (int i=7;i<ints.length-2;i++){
            if (ints[i]>80&&ints[i]>ints[i+1]&&ints[i]>ints[i+2]&&ints[i]>ints[i-1]&&ints[i]>ints[i-2]){
                peak_list.add(i);
            }
        }
    }

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

    private void showSetParmWindow() {
        View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.popuplayout, null);
        popupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(contentView);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        final TextView tv1 = (TextView) contentView.findViewById(R.id.move_value);
        final TextView range_tv = (TextView) contentView.findViewById(R.id.range_tv);
        final EditText base_num = (EditText) contentView.findViewById(R.id.base_num);
        final TextView smooth_times = (TextView) contentView.findViewById(R.id.smooth_times);
        final SeekBar seekBar_move = (SeekBar) contentView.findViewById(R.id.seekbar_move);
        final SeekBar seekBar_smooth_times = (SeekBar) contentView.findViewById(R.id.seekbar_smooth_times);
        final SeekBar seekbar_range = (SeekBar) contentView.findViewById(R.id.seekbar_range);
        final RadioGroup reduce_backgroud = (RadioGroup) contentView.findViewById(R.id.reduce_background);
        final RadioGroup smooth_method = (RadioGroup) contentView.findViewById(R.id.smooth_method);
        final RadioButton non_reduce = (RadioButton) contentView.findViewById(R.id.non_reduce);
        final RadioButton line_reduce = (RadioButton) contentView.findViewById(R.id.line_reduce);
        final RadioButton five_point = (RadioButton) contentView.findViewById(R.id.five_point);
        final RadioButton fifth_point = (RadioButton) contentView.findViewById(R.id.fifth_point);
        final RadioButton gravity_smooth = (RadioButton) contentView.findViewById(R.id.gravity_smooth);
        final RadioButton least_square = (RadioButton) contentView.findViewById(R.id.least_square);
        Button pop_ok = (Button) contentView.findViewById(R.id.pop_ok);
        non_reduce.setChecked(true);
        five_point.setChecked(true);
        base_num.setText("0");


        pop_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                base_num_int = Integer.parseInt(base_num.getText().toString());

                Log.e("caicai", "离开了呀，赶紧保存设置的参数");
                Log.e("caicai", "漂移矫正：" + move_int);
                Log.e("caicai", "扣除背景：" + reduce_index);
                Log.e("caicai", "平滑方式：" + smooth_index);
                Log.e("caicai", "平滑次数：" + smooth_times_int);
                Log.e("caicai", "基值：" + base_num_int + "。范围：" + range_int);

                reset();
                popupWindow.dismiss();
            }
        });


        seekbar_range.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                range_tv.setText("寻峰范围：" + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                range_int = seekbar_range.getProgress();
            }
        });
        seekBar_move.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.e("caicai", "数值开始变化" + (i - 10));
                tv1.setText("谱线漂移矫正：" + (i - 10));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e("caicai", "开始变化");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e("caicai", "停止变化");
                move_int = seekBar_move.getProgress() - 10;
            }
        });

        seekBar_smooth_times.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.e("caicai", "数值开始变化" + (i + 1));
                smooth_times.setText("谱线平滑次数：" + (i + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e("caicai", "开始变化");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e("caicai", "停止变化");
                smooth_times_int = seekBar_smooth_times.getProgress() + 1;
            }
        });

        reduce_backgroud.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == line_reduce.getId()) {
                    Log.e("caicai", "直线型背景扣除");
                    reduce_index = 1;
                } else if (i == non_reduce.getId()) {
                    Log.e("caicai", "不进行背景扣除");
                    reduce_index = 0;
                } else {
                    Log.e("caicai", "不进行背景扣除");
                    reduce_index = 0;
                }
            }
        });

        smooth_method.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == fifth_point.getId()) {
                    Log.e("caicai", "15点光滑");
                    smooth_index = 1;
                } else if (i == five_point.getId()) {
                    Log.e("caicai", "5点光滑");
                    smooth_index = 0;
                } else if (i == least_square.getId()) {
                    Log.e("caicai", "最小二乘");
                    smooth_index = 2;
                } else if (i == gravity_smooth.getId()) {
                    Log.e("caicai", "重力");
                    smooth_index = 3;
                } else {
                    Log.e("caicai", "5点光滑");
                    smooth_index = 0;
                }


            }
        });
        View rootView = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
        popupWindow.showAsDropDown(rootView, 0, 0);
        //popupWindow.showAtLocation(rootView, Gravity.HORIZONTAL_GRAVITY_MASK,0,0);
        //popupWindow.showAtLocation(rootView, Gravity.HORIZONTAL_GRAVITY_MASK,0,0);

    }

    private void reset() {

    }

    private void showResultWindow() {
        View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.show_result_window, null);
        popupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(contentView);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        final TextView tv1 = (TextView) contentView.findViewById(R.id.result_tv);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupWindow.dismiss();
            }
        });


        popupWindow.showAsDropDown(contentView,0,180);

    }
}
