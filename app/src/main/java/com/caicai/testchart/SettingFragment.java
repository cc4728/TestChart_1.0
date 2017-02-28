package com.caicai.testchart;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by cai on 2017/2/14.
 */

public class SettingFragment extends Fragment {
    public static int voltage, electric_current, measure_time,wifi_state,x_state;
    private ToggleButton togBtnWIFI, togBtnXray;
    private SeekBar seekBar_v, seekBar_i, seekBar_time;
    private TextView show_state;
    private final static String[] STATE = new String[]{"OFF","ON"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.set_frg_layout, container, false);
        togBtnWIFI = (ToggleButton) view.findViewById(R.id.tg_btn_wifi);
        togBtnXray = (ToggleButton) view.findViewById(R.id.tg_btn_x);
        seekBar_i = (SeekBar) view.findViewById(R.id.seek_bar_i);
        seekBar_v = (SeekBar) view.findViewById(R.id.seek_bar_v);
        seekBar_time = (SeekBar) view.findViewById(R.id.seek_bar_time);
        show_state = (TextView) view.findViewById(R.id.set_show_state);
        show_state.setText("WFIF:"+STATE[wifi_state] + "            " + "光管:"+STATE[x_state] + "\n电压：" + voltage + "Kv" + "        " + "电流：" + electric_current + "μA"
                + "\n时间：" + measure_time + "s"
        );
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        togBtnWIFI.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {//被选中
                    wifi_state=1;
                    show_state.setText("WFIF:"+STATE[wifi_state] + "            " + "光管:"+STATE[x_state] + "\n电压：" + voltage + "Kv" + "        " + "电流：" + electric_current + "μA"
                            + "\n时间：" + measure_time + "s"
                    );
                    togBtnWIFI.setButtonDrawable(R.drawable.set2);
                } else {//未被选中
                    wifi_state=0;
                    show_state.setText("WFIF:"+STATE[wifi_state] + "            " + "光管:"+STATE[x_state] + "\n电压：" + voltage + "Kv" + "        " + "电流：" + electric_current + "μA"
                            + "\n时间：" + measure_time + "s"
                    );
                    togBtnWIFI.setButtonDrawable(R.drawable.set);
                }
            }
        });
        togBtnXray.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {//被选中
                    x_state=1;
                    show_state.setText("WFIF:"+STATE[wifi_state] + "            " + "光管:"+STATE[x_state] + "\n电压：" + voltage + "Kv" + "        " + "电流：" + electric_current + "μA"
                            + "\n时间：" + measure_time + "s"
                    );
                    togBtnXray.setButtonDrawable(R.drawable.set2);
                } else {//未被选中
                    x_state=0;
                    show_state.setText("WFIF:"+STATE[wifi_state] + "            " + "光管:"+STATE[x_state] + "\n电压：" + voltage + "Kv" + "        " + "电流：" + electric_current + "μA"
                            + "\n时间：" + measure_time + "s"
                    );
                    togBtnXray.setButtonDrawable(R.drawable.set);
                }
            }
        });

        seekBar_v.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                show_state.setText("WFIF:"+STATE[wifi_state] + "            " + "光管:"+STATE[x_state]  + "\n电压：" + (i+10) + "Kv" + "        " + "电流：" + electric_current + "μA"
                        + "\n时间：" + measure_time + "s"
                );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                voltage = seekBar_v.getProgress()+10;
                show_state.setText("WFIF:"+STATE[wifi_state] + "            " + "光管:"+STATE[x_state]  + "\n电压：" + voltage + "Kv" + "        " + "电流：" + electric_current + "μA"
                        + "\n时间：" + measure_time + "s"
                );
            }
        });

        seekBar_i.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                show_state.setText("WFIF:"+STATE[wifi_state] + "            " + "光管:"+STATE[x_state] + "\n电压：" + voltage + "Kv" + "        " + "电流：" + (i+5) + "μA"
                        + "\n时间：" + measure_time + "s"
                );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                electric_current = seekBar_i.getProgress()+5;
                show_state.setText("WFIF:"+STATE[wifi_state] + "            " + "光管:"+STATE[x_state] + "\n电压：" + voltage + "Kv" + "        " + "电流：" + electric_current + "μA"
                        + "\n时间：" + measure_time + "s"
                );
            }
        });

        seekBar_time.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                show_state.setText("WFIF:"+STATE[wifi_state] + "            " + "光管:"+STATE[x_state]  + "\n电压：" + voltage + "Kv" + "        " + "电流：" + electric_current + "μA"
                        + "\n时间：" + i+ "s"
                );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                measure_time = seekBar_time.getProgress();
                show_state.setText("WFIF:"+STATE[wifi_state] + "            " + "光管:"+STATE[x_state]  + "\n电压：" + voltage + "Kv" + "        " + "电流：" + electric_current + "μA"
                        + "\n时间：" + measure_time + "s"
                );
            }
        });
    }
}
