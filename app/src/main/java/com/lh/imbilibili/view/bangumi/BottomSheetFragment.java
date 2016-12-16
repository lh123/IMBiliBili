package com.lh.imbilibili.view.bangumi;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.lh.imbilibili.R;
import com.lh.imbilibili.view.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/11/13.
 */

public class BottomSheetFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = "BottomSheetFragment";

    @BindView(R.id.bottom_sheet_container)
    ViewGroup mContainer;
    @BindView(R.id.bottom_sheet)
    ViewGroup mBottomSheet;
    @BindView(R.id.touch_outside)
    View mTouchOutside;
    @BindView(R.id.close)
    ImageButton mBtnClose;

    private BottomSheetBehavior<ViewGroup> mBottomSheetBehavior;

    public static BottomSheetFragment newInstance() {
        return new BottomSheetFragment();
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mTouchOutside.setOnClickListener(this);
        mBtnClose.setOnClickListener(this);
        mBottomSheet.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_bottom));
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    getFragmentManager().beginTransaction().remove(BottomSheetFragment.this).commit();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_video_bangumi_download;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.touch_outside:
            case R.id.close:
                break;
        }
    }
}
