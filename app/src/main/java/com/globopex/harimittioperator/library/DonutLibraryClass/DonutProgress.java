package com.globopex.harimittioperator.library.DonutLibraryClass;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.globopex.harimittioperator.R;

import java.util.Timer;
import java.util.TimerTask;

public class DonutProgress extends View {
    private Paint finishedPaint;
    private Paint unfinishedPaint;
    private Paint innerCirclePaint;
    protected Paint textPaint;
    protected Paint innerBottomTextPaint;

    protected Timer timer;

    private RectF finishedOuterRect = new RectF();
    private RectF unfinishedOuterRect = new RectF();

    private String text = "";
    private String suffix = "";
    private String prefix = "";
    private float textSize;
    private int textColor;
    private int innerBottomTextColor;
    private int progress = 0;
    private float startAngle = 0;
    private int max;
    private int finishedStrokeColor;
    private int unfinishedStrokeColor;
    private float finishedStrokeWidth;
    private float unfinishedStrokeWidth;
    private int innerBackgroundColor;
    private float innerBottomTextSize;
    private String innerBottomText1;
    private String innerBottomText2;
    private float innerBottomTextHeight;

    private final float default_stroke_width;
    private final int default_finished_color = Color.rgb(66, 145, 241);
    private final int default_unfinished_color = Color.rgb(204, 204, 204);
    private final int default_text_color = Color.rgb(0, 0, 0);
    private final int default_inner_bottom_text_color = Color.rgb(66, 145, 241);
    private final int default_inner_background_color = Color.TRANSPARENT;
    private final int default_max = 100;
    private final float default_text_size;
    private final float default_inner_bottom_text_size;
    private final int min_size;


    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_TEXT = "text";
    private static final String INSTANCE_SUFFIX = "suffix";
    private static final String INSTANCE_PREFIX = "prefix";
    private static final String INSTANCE_TEXT_COLOR = "text_color";
    private static final String INSTANCE_TEXT_SIZE = "text_size";
    private static final String INSTANCE_INNER_BOTTOM_TEXT_SIZE = "inner_bottom_text_size";
    private static final String INSTANCE_INNER_BOTTOM_TEXT1 = "inner_bottom_text1";
    private static final String INSTANCE_INNER_BOTTOM_TEXT2 = "inner_bottom_text2";
    private static final String INSTANCE_INNER_BOTTOM_TEXT_COLOR = "inner_bottom_text_color";
    private static final String INSTANCE_FINISHED_STROKE_COLOR = "finished_stroke_color";
    private static final String INSTANCE_UNFINISHED_STROKE_COLOR = "unfinished_stroke_color";
    private static final String INSTANCE_MAX = "max";
    private static final String INSTANCE_PROGRESS = "progress";
    private static final String INSTANCE_START_ANGLE = "start_angle";

    private static final String INSTANCE_FINISHED_STROKE_WIDTH = "finished_stroke_width";
    private static final String INSTANCE_UNFINISHED_STROKE_WIDTH = "unfinished_stroke_width";
    private static final String INSTANCE_BACKGROUND_COLOR = "inner_background_color";

    public DonutProgress(Context context) {
        this(context, null);
    }

    public DonutProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DonutProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        default_text_size = getTextRatio(getResources(), 18);
        min_size = (int) getHorizontalRatio(getResources(), 100);
        default_stroke_width = getHorizontalRatio(getResources(), 10);
        default_inner_bottom_text_size = getTextRatio(getResources(), 18);

        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DonutProgress, defStyleAttr, 0);
        initByAttributes(attributes);
        attributes.recycle();

        initPainters();
    }

    protected void initPainters() {
        textPaint = new TextPaint();
        textPaint.setColor(textColor);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);

        innerBottomTextPaint = new TextPaint();
        innerBottomTextPaint.setColor(innerBottomTextColor);
        innerBottomTextPaint.setTextSize(innerBottomTextSize);
        innerBottomTextPaint.setAntiAlias(true);

        finishedPaint = new Paint();
        finishedPaint.setColor(finishedStrokeColor);
        finishedPaint.setStyle(Paint.Style.STROKE);
        finishedPaint.setAntiAlias(true);
        finishedPaint.setStrokeWidth(finishedStrokeWidth);

        unfinishedPaint = new Paint();
        unfinishedPaint.setColor(unfinishedStrokeColor);
        unfinishedPaint.setStyle(Paint.Style.STROKE);
        unfinishedPaint.setAntiAlias(true);
        unfinishedPaint.setStrokeWidth(unfinishedStrokeWidth);

        innerCirclePaint = new Paint();
        innerCirclePaint.setColor(innerBackgroundColor);
        innerCirclePaint.setAntiAlias(true);
    }

    protected void initByAttributes(TypedArray attributes) {
        finishedStrokeColor = attributes.getColor(R.styleable.DonutProgress_donut_finished_color, default_finished_color);
        unfinishedStrokeColor = attributes.getColor(R.styleable.DonutProgress_donut_unfinished_color, default_unfinished_color);
        if (attributes.getString(R.styleable.DonutProgress_donut_text) != null) {
            text = attributes.getString(R.styleable.DonutProgress_donut_text);
        }

        if (attributes.getString(R.styleable.DonutProgress_donut_prefix) != null) {
            prefix = attributes.getString(R.styleable.DonutProgress_donut_prefix);
        }

        if (attributes.getString(R.styleable.DonutProgress_donut_suffix) != null) {
            suffix = attributes.getString(R.styleable.DonutProgress_donut_suffix);
        }

        textColor = attributes.getColor(R.styleable.DonutProgress_donut_text_color, default_text_color);
        textSize = attributes.getDimension(R.styleable.DonutProgress_donut_text_size, default_text_size);

        setMax(attributes.getInt(R.styleable.DonutProgress_donut_max, default_max));
        setProgress(attributes.getInt(R.styleable.DonutProgress_donut_progress, 0));
        startAngle = attributes.getFloat(R.styleable.DonutProgress_donut_start_angle, 0);

        finishedStrokeWidth = attributes.getDimension(R.styleable.DonutProgress_donut_finished_stroke_width, default_stroke_width);
        unfinishedStrokeWidth = attributes.getDimension(R.styleable.DonutProgress_donut_unfinished_stroke_width, default_stroke_width);
        innerBackgroundColor = attributes.getColor(R.styleable.DonutProgress_donut_background_color, default_inner_background_color);

        innerBottomTextSize = attributes.getDimension(R.styleable.DonutProgress_donut_inner_bottom_text_size, default_inner_bottom_text_size);
        innerBottomTextColor = attributes.getColor(R.styleable.DonutProgress_donut_inner_bottom_text_color, default_inner_bottom_text_color);
        innerBottomText1 = attributes.getString(R.styleable.DonutProgress_donut_inner_bottom_text);
        innerBottomText2 = attributes.getString(R.styleable.DonutProgress_donut_inner_bottom_text);
    }

    @Override
    public void invalidate() {
        initPainters();
        super.invalidate();
    }

    public float getFinishedStrokeWidth() {
        return finishedStrokeWidth;
    }

    public void setFinishedStrokeWidth(float finishedStrokeWidth) {
        this.finishedStrokeWidth = finishedStrokeWidth;
        this.invalidate();
    }

    public float getUnfinishedStrokeWidth() {
        return unfinishedStrokeWidth;
    }

    public void setUnfinishedStrokeWidth(float unfinishedStrokeWidth) {
        this.unfinishedStrokeWidth = unfinishedStrokeWidth;
        this.invalidate();
    }

    private float getProgressAngle() {
        return getProgress() / (float) max * 360f;
    }

    public int getProgress() {
        return progress;
    }

    /**
     * If the start angle is negative or = 360, the start angle is treated
     * as start angle modulo 360.
     * <p>
     * The arc is drawn clockwise. An angle of 0 degrees correspond to the
     * 12 o'clock on a watch.
     *
     * @param startAngle Starting angle (in degrees) where the arc begins
     */
    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
        invalidate();
    }

    public float getStartAngle() {
        return this.startAngle;
    }

    /**
     * Sets the string value of the DonutProgress.
     *
     * @param text ref android.R.styleable#DonutProgress_donut_text
     */
    public void setText(String text) {
        this.text = prefix + text + suffix;
        invalidate();
    }

    public String getText() {
        return this.text;
    }

    /**
     * Sets the suffix value of the text.
     *
     * @param suffix ref android.R.styleable#DonutProgress_donut_suffix
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    /**
     * Sets the prefix value of the text.
     *
     * @param prefix ref android.R.styleable#DonutProgress_donut_prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return this.prefix;
    }

    /**
     * Sets the progress value of the DonutProgress.
     * By default, animation is disable
     *
     * @param progress ref android.R.styleable#DonutProgress_donut_progress
     */
    public void setProgress(int progress) {
        setProgressWithAnimation(progress, 0);
    }

    /**
     * Sets the progress value of the DonutProgress and launch animation.
     *
     * @param progress ref android.R.styleable#DonutProgress_donut_progress.
     * @param period   amount of time in milliseconds between subsequent executions.
     */
    public void setProgressWithAnimation(int progress, int period) {
        this.progress = progress;
        this.setText("" + progress);
        if (this.progress > getMax()) {
            this.progress %= getMax();
        }

        if (period > 0) {
            startAnimation(period);
        } else {
            invalidate();
        }
    }

    private void startAnimation(int period) {

        final int finalProgress = getProgress();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            private Handler handler = new Handler(Looper.getMainLooper());

            int initProgress = 0;

            @Override
            public void run() {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (initProgress < finalProgress) {
                            initProgress++;
                            setProgress(initProgress);
                            setText(initProgress + "");
                        } else {
                            timer.cancel();
                        }
                    }
                });
            }
        }, 0, period);
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        if (max > 0) {
            this.max = max;
            invalidate();
        }
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        this.invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        this.invalidate();
    }

    public int getFinishedStrokeColor() {
        return finishedStrokeColor;
    }

    public void setFinishedStrokeColor(int finishedStrokeColor) {
        this.finishedStrokeColor = finishedStrokeColor;
        this.invalidate();
    }

    public int getUnfinishedStrokeColor() {
        return unfinishedStrokeColor;
    }

    public void setUnfinishedStrokeColor(int unfinishedStrokeColor) {
        this.unfinishedStrokeColor = unfinishedStrokeColor;
        this.invalidate();
    }

    public int getInnerBackgroundColor() {
        return innerBackgroundColor;
    }

    public void setInnerBackgroundColor(int innerBackgroundColor) {
        this.innerBackgroundColor = innerBackgroundColor;
        this.invalidate();
    }


    public String getInnerBottomText1() {
        return innerBottomText1;
    }

    public String getInnerBottomText2() {
        return innerBottomText2;
    }

    public void setInnerBottomText(String innerBottomText1, String innerBottomText2) {
        this.innerBottomText1 = innerBottomText1;
        this.innerBottomText2 = innerBottomText2;
        this.invalidate();
    }


    public float getInnerBottomTextSize() {
        return innerBottomTextSize;
    }

    public void setInnerBottomTextSize(float innerBottomTextSize) {
        this.innerBottomTextSize = innerBottomTextSize;
        this.invalidate();
    }

    public int getInnerBottomTextColor() {
        return innerBottomTextColor;
    }

    public void setInnerBottomTextColor(int innerBottomTextColor) {
        this.innerBottomTextColor = innerBottomTextColor;
        this.invalidate();
    }

    /**
     * Utils
     */

    public float getTextRatio(Resources resources, int pix) {
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int width = metrics.widthPixels;
        float pixm = (float) (((double) pix / (double) 320) * width);
        if (pixm == 0.0f) {
            pixm = 1;
        }
        return pixm;
    }

    public int getHorizontalRatio(Resources resources, int pix) {
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int width = metrics.widthPixels;
        int pixm = (int) (((double) pix / (double) 320) * width);
        if (pixm == 0) {
            pixm = 1;
        }
        return pixm;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));

        //TODO calculate inner circle height and then position bottom text at the bottom (3/4)
        innerBottomTextHeight = getHeight() - (getHeight() * 3) / 5;
    }

    private int measure(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = min_size;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float delta = Math.max(finishedStrokeWidth, unfinishedStrokeWidth);
        finishedOuterRect.set(delta,
                delta,
                getWidth() - delta,
                getHeight() - delta);
        unfinishedOuterRect.set(delta,
                delta,
                getWidth() - delta,
                getHeight() - delta);

        float innerCircleRadius = (getWidth() - Math.min(finishedStrokeWidth, unfinishedStrokeWidth) + Math.abs(finishedStrokeWidth - unfinishedStrokeWidth)) / 2f;
        canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, innerCircleRadius, innerCirclePaint);
        canvas.drawArc(finishedOuterRect, startAngle - 90, getProgressAngle(), false, finishedPaint);
        canvas.drawArc(unfinishedOuterRect, getProgressAngle() + startAngle - 90, 360 - getProgressAngle(), false, unfinishedPaint);

        if (!TextUtils.isEmpty(text) && !TextUtils.isEmpty(getInnerBottomText2())) {
            float textHeight = textPaint.descent() + textPaint.ascent();
            canvas.drawText(getInnerBottomText2(), (getWidth() - textPaint.measureText(text)) / 1.8f, (getWidth() - textHeight) / 4.0f, innerBottomTextPaint);
            canvas.drawText(text, (getWidth() - textPaint.measureText(text)) / 2.0f, (getWidth() - textHeight) / 2.1f, textPaint);
        }

        if (!TextUtils.isEmpty(getInnerBottomText1())) {
            innerBottomTextPaint.setTextSize(innerBottomTextSize);
            float bottomTextBaseline = getHeight() - innerBottomTextHeight - (textPaint.descent() + textPaint.ascent()) / 2;
            float bottomTextBaseline1 = (float) (getHeight() - innerBottomTextHeight - (textPaint.descent() + textPaint.ascent()) / 0.8);
            float width = (getWidth() - innerBottomTextPaint.measureText(getInnerBottomText1())) / 2.0f;
            canvas.drawText(getInnerBottomText1(), (getWidth() - innerBottomTextPaint.measureText(getInnerBottomText1())) / 2.0f, bottomTextBaseline, innerBottomTextPaint);
            canvas.drawText("PRESENT", (getWidth() - innerBottomTextPaint.measureText(getInnerBottomText1())) / 1.8f, bottomTextBaseline1, innerBottomTextPaint);
        }

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putString(INSTANCE_TEXT, getText());
        bundle.putString(INSTANCE_PREFIX, getPrefix());
        bundle.putString(INSTANCE_SUFFIX, getPrefix());
        bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor());
        bundle.putFloat(INSTANCE_TEXT_SIZE, getTextSize());
        bundle.putFloat(INSTANCE_INNER_BOTTOM_TEXT_SIZE, getInnerBottomTextSize());
        bundle.putFloat(INSTANCE_INNER_BOTTOM_TEXT_COLOR, getInnerBottomTextColor());
        bundle.putString(INSTANCE_INNER_BOTTOM_TEXT1, getInnerBottomText1());
        bundle.putString(INSTANCE_INNER_BOTTOM_TEXT2, getInnerBottomText2());
        bundle.putInt(INSTANCE_INNER_BOTTOM_TEXT_COLOR, getInnerBottomTextColor());
        bundle.putInt(INSTANCE_FINISHED_STROKE_COLOR, getFinishedStrokeColor());
        bundle.putInt(INSTANCE_UNFINISHED_STROKE_COLOR, getUnfinishedStrokeColor());
        bundle.putInt(INSTANCE_MAX, getMax());
        bundle.putInt(INSTANCE_PROGRESS, getProgress());
        bundle.putFloat(INSTANCE_START_ANGLE, getStartAngle());
        bundle.putFloat(INSTANCE_FINISHED_STROKE_WIDTH, getFinishedStrokeWidth());
        bundle.putFloat(INSTANCE_UNFINISHED_STROKE_WIDTH, getUnfinishedStrokeWidth());
        bundle.putInt(INSTANCE_BACKGROUND_COLOR, getInnerBackgroundColor());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            text = bundle.getString(INSTANCE_TEXT);
            prefix = bundle.getString(INSTANCE_PREFIX);
            suffix = bundle.getString(INSTANCE_SUFFIX);
            textColor = bundle.getInt(INSTANCE_TEXT_COLOR);
            textSize = bundle.getFloat(INSTANCE_TEXT_SIZE);
            innerBottomTextSize = bundle.getFloat(INSTANCE_INNER_BOTTOM_TEXT_SIZE);
            innerBottomText1 = bundle.getString(INSTANCE_INNER_BOTTOM_TEXT1);
            innerBottomText2 = bundle.getString(INSTANCE_INNER_BOTTOM_TEXT2);
            innerBottomTextColor = bundle.getInt(INSTANCE_INNER_BOTTOM_TEXT_COLOR);
            finishedStrokeColor = bundle.getInt(INSTANCE_FINISHED_STROKE_COLOR);
            unfinishedStrokeColor = bundle.getInt(INSTANCE_UNFINISHED_STROKE_COLOR);
            finishedStrokeWidth = bundle.getFloat(INSTANCE_FINISHED_STROKE_WIDTH);
            unfinishedStrokeWidth = bundle.getFloat(INSTANCE_UNFINISHED_STROKE_WIDTH);
            innerBackgroundColor = bundle.getInt(INSTANCE_BACKGROUND_COLOR);
            initPainters();
            setMax(bundle.getInt(INSTANCE_MAX));
            setProgress(bundle.getInt(INSTANCE_PROGRESS));
            setStartAngle(bundle.getFloat(INSTANCE_START_ANGLE));
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

}
