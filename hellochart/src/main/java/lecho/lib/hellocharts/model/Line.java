package lecho.lib.hellocharts.model;

import android.graphics.PathEffect;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.formatter.LineChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleLineChartValueFormatter;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;

/**
 * Single line for line chart.
 */
public class Line {
    private static final int DEFAULT_LINE_STROKE_WIDTH_DP = 1;//设置线的宽度
    private static final int DEFAULT_POINT_RADIUS_DP = 4;//设置点的宽度
    private static final int DEFAULT_AREA_TRANSPARENCY = 64;//area transparency
    public static final int UNINITIALIZED = 0;
    private int color = ChartUtils.DEFAULT_COLOR;//设置线条颜色
    private int pointColor = UNINITIALIZED;//点的颜色
    private int darkenColor = ChartUtils.DEFAULT_DARKEN_COLOR;
    /**
     * Transparency of area when line is filled. *
     */
    private int areaTransparency = DEFAULT_AREA_TRANSPARENCY;//区域透明
    private int strokeWidth = DEFAULT_LINE_STROKE_WIDTH_DP;
    private int pointRadius = DEFAULT_POINT_RADIUS_DP;//点的半径
    private boolean hasPoints = false;
    private boolean hasLines = true;
    private boolean hasLabels = false;
    private boolean hasLabelsOnlyForSelected = false;
    private boolean isCubic = false;
    private boolean isSquare = false;
    private boolean isFilled = false;
    private ValueShape shape = ValueShape.CIRCLE;//点的形状
    private PathEffect pathEffect;
    private LineChartValueFormatter formatter = new SimpleLineChartValueFormatter();//曲线图值的格式
    private List<PointValue> values = new ArrayList<PointValue>();//点值

    public Line() {//无参构造方法

    }

    public Line(List<PointValue> values) {//另一个有参构造方法
        setValues(values);//设置值值
    }

    public Line(Line line) {//又一个构造方法，传入的参数不同，要做深入了解
        this.color = line.color;
        this.pointColor = line.pointColor;
        this.darkenColor = line.darkenColor;
        this.areaTransparency = line.areaTransparency;
        this.strokeWidth = line.strokeWidth;
        this.pointRadius = line.pointRadius;
        this.hasPoints = line.hasPoints;
        this.hasLines = line.hasLines;
        this.hasLabels = line.hasLabels;
        this.hasLabelsOnlyForSelected = line.hasLabelsOnlyForSelected;
        this.isSquare = line.isSquare;
        this.isCubic = line.isCubic;
        this.isFilled = line.isFilled;
        this.shape = line.shape;
        this.pathEffect = line.pathEffect;
        this.formatter = line.formatter;

        for (PointValue pointValue : line.values) {
            this.values.add(new PointValue(pointValue));
        }
    }//第三个构造方法，里面写了哪些东西，要与MainActivity对比看

    public void update(float scale) {//定义了一个更新的方法，传入的是浮点比例
        for (PointValue value : values) {//如果两个相等则为真
            value.update(scale);//执行放大，比例为参数
        }
    }

    public void finish() {//定义一个完成的方法
        for (PointValue value : values) {//如果两个值相等
            value.finish();//执行完成的方法
        }
    }

    public List<PointValue> getValues() {
        return this.values;
    }//获取值

    public void setValues(List<PointValue> values) {
        if (null == values) {
            this.values = new ArrayList<PointValue>();
        } else {
            this.values = values;
        }
    }//设置值

    public int getColor() {
        return color;
    }//获取颜色，整形

    public Line setColor(int color) {
        this.color = color;
        if (pointColor == UNINITIALIZED) {
            this.darkenColor = ChartUtils.darkenColor(color);
        }
        return this;
    }//设置颜色，构造方法

    public int getPointColor() {
        if (pointColor == UNINITIALIZED) {
            return color;
        } else {
            return pointColor;
        }
    }

    public Line setPointColor(int pointColor) {
        this.pointColor = pointColor;
        if (pointColor == UNINITIALIZED) {
            this.darkenColor = ChartUtils.darkenColor(color);
        } else {
            this.darkenColor = ChartUtils.darkenColor(pointColor);
        }
        return this;
    }//构造方法

    public int getDarkenColor() {
        return darkenColor;
    }

    /**
     * @see #setAreaTransparency(int)
     */
    public int getAreaTransparency() {
        return areaTransparency;
    }

    /**
     * Set area transparency(255 is full opacity) for filled lines
     *
     * @param areaTransparency
     * @return
     */
    public Line setAreaTransparency(int areaTransparency) {
        this.areaTransparency = areaTransparency;
        return this;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public Line setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        return this;
    }

    public boolean hasPoints() {
        return hasPoints;
    }

    public Line setHasPoints(boolean hasPoints) {
        this.hasPoints = hasPoints;
        return this;
    }

    public boolean hasLines() {
        return hasLines;
    }

    public Line setHasLines(boolean hasLines) {
        this.hasLines = hasLines;
        return this;
    }

    public boolean hasLabels() {
        return hasLabels;
    }

    public Line setHasLabels(boolean hasLabels) {
        this.hasLabels = hasLabels;
        if (hasLabels) {//如果设置有labels，则点击不显示，避免重复显示
            this.hasLabelsOnlyForSelected = false;
        }
        return this;
    }

    /**
     * @see #setHasLabelsOnlyForSelected(boolean)
     */
    public boolean hasLabelsOnlyForSelected() {
        return hasLabelsOnlyForSelected;
    }//点击显示labels

    /**
     * Set true if you want to show value labels only for selected value, works best when chart has
     * isValueSelectionEnabled set to true {@link Chart#setValueSelectionEnabled(boolean)}.
     */
    public Line setHasLabelsOnlyForSelected(boolean hasLabelsOnlyForSelected) {
        this.hasLabelsOnlyForSelected = hasLabelsOnlyForSelected;
        if (hasLabelsOnlyForSelected) {
            this.hasLabels = false;
        }
        return this;
    }//构造方法

    public int getPointRadius() {
        return pointRadius;
    }

    /**
     * Set radius for points for this line.
     *
     * @param pointRadius
     * @return
     */
    public Line setPointRadius(int pointRadius) {//设置点的半径，没有用到
        this.pointRadius = pointRadius;
        return this;
    }

    public boolean isCubic() {
        return isCubic;
    }

    public Line setCubic(boolean isCubic) {
        this.isCubic = isCubic;
        if (isSquare)
            setSquare(false);
        return this;
    }//构造方法

    public boolean isSquare() {
        return isSquare;
    }

    public Line setSquare(boolean isSquare) {
        this.isSquare = isSquare;
        if (isCubic)
            setCubic(false);
        return this;
    }//构造方法设置方块

    public boolean isFilled() {
        return isFilled;
    }

    public Line setFilled(boolean isFilled) {
        this.isFilled = isFilled;
        return this;
    }//构造方法，填充

    /**
     * @see #setShape(ValueShape)
     */
    public ValueShape getShape() {
        return shape;
    }

    /**
     * Set shape for points, possible values: SQUARE, CIRCLE
     *
     * @param shape
     * @return
     */
    public Line setShape(ValueShape shape) {
        this.shape = shape;
        return this;
    }//这个是构造方法吗？为什么出现在其他类里面

    public PathEffect getPathEffect() {
        return pathEffect;
    }

    /**
     * Set path effect for this line, note: it will slow down drawing, try to not use complicated effects,
     * DashPathEffect should be safe choice.设置高效线，这将降低绘制速度，尽量不完全使用effects
     *
     * @param pathEffect
     */
    public void setPathEffect(PathEffect pathEffect) {
        this.pathEffect = pathEffect;
    }

    public LineChartValueFormatter getFormatter() {
        return formatter;
    }//获取格式

    public Line setFormatter(LineChartValueFormatter formatter) {
        if (null != formatter) {
            this.formatter = formatter;
        }
        return this;
    }//设置格式
}
