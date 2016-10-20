package com.lh.imbilibili.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lh.imbilibili.R;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup implements View.OnClickListener {

    private int mCurrentSelectPosition = 0;
    private List<Point> points = new ArrayList<>();
    private int space;
    private OnItemClickListener mOnItemClickListener;

    public FlowLayout(Context context) {
        super(context);
        init(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    //
    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.FlowLayout);
            space = a.getDimensionPixelSize(R.styleable.FlowLayout_itemSpace, 5);
            a.recycle();
        }
        setWillNotDraw(false);//在加载scrollBar时用到
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        points.clear();
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() != GONE) {
                int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST);
                int heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
                childView.measure(widthSpec, heightSpec);
            }
        }
        int currentX = getPaddingLeft();
        int currentY = getPaddingTop();
        int lineMaxHeight = 0;
        final int contentX = width - getPaddingLeft() - getPaddingRight();
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (currentX + 2 * space  + childView.getMeasuredWidth() <= contentX) {
                currentX += space;
                points.add(new Point(currentX, currentY + space));
                currentX += (childView.getMeasuredWidth() + space );
                lineMaxHeight = Math.max(lineMaxHeight, childView.getMeasuredHeight()  + 2 * space);
            } else {
                currentX = space + getPaddingLeft();
                currentY += lineMaxHeight;
                lineMaxHeight = childView.getMeasuredHeight() + 2 * space;
                points.add(new Point(currentX, currentY + space));
                currentX += (childView.getMeasuredWidth() + space);
            }
        }
        setMeasuredDimension(width, currentY + lineMaxHeight);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int
            i2, int i3) {
        int childCount = getChildCount();
        for (int j = 0; j < childCount; j++) {
            View childView = getChildAt(j);
            Point point = points.get(j);
            childView.layout(point.x, point.y, point.x + childView.getMeasuredWidth(), point.y + childView.getMeasuredHeight());
        }
    }

    @SuppressWarnings("ResourceType")
    public void addTag(String text, int index) {
        TextView textView = new TextView(getContext());
        MarginLayoutParams params = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textView.setText(text);
        textView.setTag(index);
        textView.setTextColor(Color.BLACK);
        textView.setLayoutParams(params);
        textView.setOnClickListener(this);
        addView(textView,index);
    }

    public void addTag(View view, int index) {
        view.setTag(index);
        view.setOnClickListener(this);
        addView(view, index);
    }

    public void selectTag(int position) {
        int preSelect = mCurrentSelectPosition;
        mCurrentSelectPosition = position;
        View childView = getChildAt(preSelect);
        if(childView instanceof TextView){
            ((TextView) childView).setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        }
        childView = getChildAt(mCurrentSelectPosition);
        if(childView instanceof TextView){
            ((TextView) childView).setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        }
    }

    @Override
    public void onClick(View v) {
        int index = (int) v.getTag();
        selectTag(index);
        if(mOnItemClickListener!=null){
            mOnItemClickListener.onItemClick(this,index,v);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ViewGroup parent,int position,View view);
    }
}
