package com.lh.imbilibili.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lh.imbilibili.R;

import java.lang.reflect.Field;

/**
 * Created by liuhui on 2016/7/8.
 */
public class BannerView extends FrameLayout implements ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private LinearLayout dotContainer;

    private Adaper adaper;
    private int currentPosition = 0;
    private boolean isLoop = false;
    private boolean isTouch = false;

    private int halfSpace;
    private long loopTime = 6000;

    private LoopRunnable loopRunnable;

    public BannerView(Context context) {
        super(context);
        init(context);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        halfSpace = getResources().getDimensionPixelSize(R.dimen.item_half_spacing);
        loopRunnable = new LoopRunnable();
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        viewPager = new ViewPager(context);
        viewPager.setLayoutParams(layoutParams);
        viewPager.setPageMargin(halfSpace * 2);
        addView(viewPager);
    }

    public void setAdaper(Adaper adaper) {
        this.adaper = adaper;
        viewPager.setAdapter(adaper);
        viewPager.setCurrentItem(1, false);
        viewPager.addOnPageChangeListener(this);
        initDot();
    }

    private void initDot() {
        dotContainer = new LinearLayout(getContext());
        dotContainer.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        params.rightMargin = halfSpace * 2;
        params.bottomMargin = halfSpace * 3;
        dotContainer.setLayoutParams(params);
        for (int i = 0; i < adaper.getBannerCount(); i++) {
            View view = new View(getContext());
            view.setBackgroundResource(R.drawable.banner_dot);
            view.setEnabled(false);
            LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams((int) (halfSpace * 1.5), (int) (halfSpace * 1.5));
            dotParams.leftMargin = halfSpace / 2;
            dotParams.rightMargin = halfSpace / 2;
            view.setLayoutParams(dotParams);
            dotContainer.addView(view, i);
        }
        dotContainer.getChildAt(0).setEnabled(true);
        addView(dotContainer);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouch = true;
                if (isLoop) {
                    stopLoop();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isTouch = false;
                if (!isLoop) {
                    startLoop(loopTime);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void startLoop(long time) {
        loopTime = time;
        if (!isLoop) {
            isLoop = true;
            postDelayed(loopRunnable, loopTime);
        }
        postInvalidate();
    }

    public void stopLoop() {
        isLoop = false;
        removeCallbacks(loopRunnable);
    }

    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        viewPager.addOnPageChangeListener(listener);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position;
//        System.out.println(position);
        if (position <= adaper.getBannerCount() && position >= 1) {
            selectDot(position - 1);
        } else if (position == 0) {
            selectDot(dotContainer.getChildCount() - 1);
        } else if (position > adaper.getBannerCount()) {
            selectDot(0);
        }
        //dotContainer.getChildAt(position).setEnabled(true);
    }

    private void selectDot(int index) {
        for (int i = 0; i < dotContainer.getChildCount(); i++) {
            if (i == index) {
                dotContainer.getChildAt(i).setEnabled(true);
            } else {
                dotContainer.getChildAt(i).setEnabled(false);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            if (currentPosition == 0) {
                viewPager.setCurrentItem(adaper.getCount() - 2, false);
            } else if (currentPosition == adaper.getCount() - 1) {
                viewPager.setCurrentItem(1, false);
            }
        }
    }

    public void setCurrentItem(int position, boolean smooth) {
        viewPager.setCurrentItem(position, smooth);
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (currentPosition == 0) {
            setCurrentItem(1, false);
        }
        startLoop(loopTime);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopLoop();
    }

    public abstract static class Adaper extends PagerAdapter {

        public abstract int getBannerCount();

        public abstract Object getItemView(ViewGroup container, int position);

        @Override
        public int getCount() {
            if (getBannerCount() == 0) {
                return 0;
            } else {
                return getBannerCount() + 2;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int realPosition;
            if (getCount() == 0) {
                realPosition = 0;
            } else {
                if (position == 0) {
                    realPosition = getBannerCount() - 1;
                } else if (position == getCount() - 1) {
                    realPosition = 0;
                } else {
                    realPosition = position - 1;
                }
            }
            return getItemView(container, realPosition);
        }
    }

    public class LoopRunnable implements Runnable {

        @Override
        public void run() {
            if (!isTouch) {
                currentPosition++;
                try {
                    Field mFirstLayout = ViewPager.class.getDeclaredField("mFirstLayout");
                    mFirstLayout.setAccessible(true);
                    mFirstLayout.set(viewPager, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                viewPager.setCurrentItem(currentPosition, true);
                postDelayed(this, loopTime);
            }
        }
    }
}
