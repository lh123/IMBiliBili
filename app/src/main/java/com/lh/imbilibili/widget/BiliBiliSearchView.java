package com.lh.imbilibili.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.lh.imbilibili.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/6.
 * SearchView
 */

public class BiliBiliSearchView extends DialogFragment implements DialogInterface.OnKeyListener, View.OnClickListener {

    public static final String TAG = BiliBiliSearchView.class.getName();

    @BindView(R.id.back)
    View mBack;
    @BindView(R.id.search_layout)
    View mSearchLayout;
    @BindView(R.id.search_bar)
    EditText mEdSearchBar;
    @BindView(R.id.qr_search)
    View mSearch;
    @BindView(R.id.search_suggest)
    RecyclerView mRecyclerView;
    @BindView(R.id.divider)
    View mDivider;

    private String mHint;
    private String mKeyWord;

    private View mRootView;

    private OnSearchListener mOnSearchListener;

    public static BiliBiliSearchView newInstance() {
        BiliBiliSearchView searchView = new BiliBiliSearchView();
        searchView.setStyle(STYLE_NO_TITLE, 0);
        return searchView;
    }


    public void setOnSearchListener(OnSearchListener listener) {
        mOnSearchListener = listener;
    }

    public void setHint(String hint) {
        mHint = hint;
    }

    public void setKeyWord(String keyWord) {
        mKeyWord = keyWord;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.search_widget_layout, container, false);
            ButterKnife.bind(this, mRootView);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSearchView();
    }

    private void initSearchView() {
        mBack.setOnClickListener(this);
        mSearch.setOnClickListener(this);
        mEdSearchBar.setHint(mHint);
        getDialog().setOnKeyListener(this);
        mEdSearchBar.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethod = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethod.showSoftInput(mEdSearchBar, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            dismiss();
        } else if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
            if (mOnSearchListener != null && !TextUtils.isEmpty(mEdSearchBar.getText())) {
                mOnSearchListener.onSearch(mEdSearchBar.getText().toString());
                dismiss();
            }
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.TOP);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mEdSearchBar.setText(mKeyWord);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mRootView != null && mRootView.getParent() != null) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                dismiss();
                break;
            case R.id.qr_search:
                if (mOnSearchListener != null && !TextUtils.isEmpty(mEdSearchBar.getText())) {
                    mOnSearchListener.onSearch(mEdSearchBar.getText().toString());
                    dismiss();
                }
                break;
        }
    }

    public void show(FragmentManager manager) {
        super.show(manager, TAG);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mEdSearchBar.setText("");
    }

    public interface OnSearchListener {
        void onSearch(String keyWord);
    }
}
