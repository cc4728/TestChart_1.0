package lecho.lib.hellocharts.formatter;


import lecho.lib.hellocharts.model.PointValue;

public interface LineChartValueFormatter {//接口

    int formatChartValue(char[] formattedValue, PointValue value);//规定了一种格式
}
