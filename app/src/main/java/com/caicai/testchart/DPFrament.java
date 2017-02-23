package com.caicai.testchart;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by cai on 2017/2/14.
 */

public class DPFrament extends Fragment {

    private TextView move_show, reduce_show, smooth_show, byHand_show;
    private SeekBar seekMove, seekRange, seekBase, seekTimes;
    private RadioButton fivePoint, fifPoint, gravity, leastSquare, nonReduce, lineReduce;
    private RadioGroup smoothGrop, reduceGroup;
    private Button ok,addROI;
    private int[] temp = new int[3051];



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dp_frg_layout, container, false);
        DataProvide dataProvide = new DataProvide();
        dataProvide.setDefaultData();//初始化能谱分析设置
        move_show = (TextView) view.findViewById(R.id.move_show);
        reduce_show = (TextView) view.findViewById(R.id.reduce_show);
        smooth_show = (TextView) view.findViewById(R.id.smooth_show);
        byHand_show = (TextView) view.findViewById(R.id.byHandShow);
        seekMove = (SeekBar) view.findViewById(R.id.seekbar_move);
        seekRange = (SeekBar) view.findViewById(R.id.seekbar_range);
        seekBase = (SeekBar) view.findViewById(R.id.seekbar_base);
        seekTimes = (SeekBar) view.findViewById(R.id.seekbar_smooth_times);
        fivePoint = (RadioButton) view.findViewById(R.id.five_point);
        fifPoint = (RadioButton) view.findViewById(R.id.fifth_point);
        gravity = (RadioButton) view.findViewById(R.id.gravity_smooth);
        leastSquare = (RadioButton) view.findViewById(R.id.least_square);
        nonReduce = (RadioButton) view.findViewById(R.id.non_reduce);
        lineReduce = (RadioButton) view.findViewById(R.id.line_reduce);
        smoothGrop = (RadioGroup) view.findViewById(R.id.smooth_group);
        reduceGroup = (RadioGroup) view.findViewById(R.id.reduce_group);
        addROI=(Button)view.findViewById(R.id.process_add_roi);
        ok = (Button) view.findViewById(R.id.process_ok);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        smoothGrop.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == fivePoint.getId()) {
                    DataProvide.smoothStyle = 1;
                    smooth_show.setText(DataProvide.SMOOTHSTYLE[DataProvide.smoothStyle] + DataProvide.smoothTimes + "次");

                }
                if (i == fifPoint.getId()) {
                    DataProvide.smoothStyle = 2;
                    smooth_show.setText(DataProvide.SMOOTHSTYLE[DataProvide.smoothStyle] + DataProvide.smoothTimes + "次");

                }
                if (i == gravity.getId()) {
                    DataProvide.smoothStyle = 3;
                    smooth_show.setText(DataProvide.SMOOTHSTYLE[DataProvide.smoothStyle] + DataProvide.smoothTimes + "次");
                }
                if (i == leastSquare.getId()) {
                    DataProvide.smoothStyle = 4;
                    smooth_show.setText(DataProvide.SMOOTHSTYLE[DataProvide.smoothStyle] + DataProvide.smoothTimes + "次");

                }

            }
        });
        reduceGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == nonReduce.getId()) {
                    DataProvide.reduce = false;
                    reduce_show.setText("扣除背景：无");

                }
                if (i == lineReduce.getId()) {
                    DataProvide.reduce = true;
                    reduce_show.setText("扣除背景：直线法");

                }
            }
        });
        seekMove.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                move_show.setText("漂移矫正：" + (i - 50));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                DataProvide.moveValue = seekMove.getProgress() - 50;
                //upDataChart();
            }
        });
        seekTimes.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                smooth_show.setText(DataProvide.SMOOTHSTYLE[DataProvide.smoothStyle] + i + "次");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                DataProvide.smoothTimes = seekTimes.getProgress();

            }
        });
        seekBase.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                byHand_show.setText("ROI： Start：" + i *10+ ";  End：" + DataProvide.byHandRange);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                DataProvide.byHandBase = seekBase.getProgress()*10;

            }
        });
        seekRange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                byHand_show.setText("ROI   Start：" + DataProvide.byHandBase + "; End：" + i*10);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                DataProvide.byHandRange = seekRange.getProgress()*10;

            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upDataChart();
                //更新波形
            }
        });
        addROI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataProvide.byHandRange - DataProvide.byHandBase > 0) {
                    int count=0;
                    //找出数组为0的索引值
                    for (int i=0;i<100;i++) {
                        if (DataProvide.reignOfInteresting[i] == 0) {
                            count=i;
                        }
                        break;
                    }
                    DataProvide.reignOfInteresting[count-2]=DataProvide.byHandBase;//左边界
                    DataProvide.reignOfInteresting[count-1]=DataProvide.byHandRange;//右边界
                    Toast.makeText(getActivity(),"ROI加入成功！!",Toast.LENGTH_SHORT).show();

                    DataProvide.byHandBase=0;
                    DataProvide.byHandRange=0;//进入界面，清零，推出界面也清理
                }else
                    Toast.makeText(getActivity(),"重新输入，注意开始道指必须小于结束道指",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void upDataChart() {//更新chart
        //更新after_smooth[]
        //将flag设置为1，
        DataProvide dataProvide = new DataProvide();
        ChartFragment.flag = 1;
        if (DataProvide.reduce) {//背景扣除
            temp = dataProvide.reduceBackground(DataProvide.rawValue);
            Log.e("caicai", "数据背景扣除");
        } else
            temp = dataProvide.rawValue;
        if (DataProvide.smoothTimes != 0) {
            int time = DataProvide.smoothTimes;
            while (time > 0) {
                switch (DataProvide.smoothStyle) {
                    case 1:
                        temp = dataProvide.smoothSpectrum_average(temp);
                        break;
                    case 2:
                        temp = dataProvide.smoothSpectrum_15point(temp);
                        break;
                    case 3:
                        temp = dataProvide.smoothSpectrum_gravity(temp);
                        break;
                    case 4:
                        temp = dataProvide.smoothSpectrum_least_square(temp);
                        break;
                    default:
                        break;
                }
                time--;
            }
        }
        if (DataProvide.moveValue != 0) {
            //最后一步平移
            temp = dataProvide.moveSpectrum(DataProvide.moveValue, DataProvide.rawValue);
        }
        System.arraycopy(temp, 0, DataProvide.afterSmooth, 0, 3051);
        Log.e("caicai", "更新after Smooth[]成功");
        getFragmentManager().beginTransaction().replace(R.id.container,new ChartFragment()).commit();
        Log.e("caicai", "提交更新的chart");
    }
}
