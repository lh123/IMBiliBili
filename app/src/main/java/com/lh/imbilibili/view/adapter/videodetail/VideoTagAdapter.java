package com.lh.imbilibili.view.adapter.videodetail;

import android.view.View;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;

/**
 * Created by liuhui on 2016/10/27.
 */

public class VideoTagAdapter extends TagAdapter<String> {

    public VideoTagAdapter(String[] datas) {
        super(datas);
    }

    @Override
    public View getView(FlowLayout parent, int position, String s) {
        TextView textView = new TextView(parent.getContext());
        textView.setText(s);

        textView.setBackgroundResource(R.drawable.selector_white_round_bg);
        return textView;
    }
}
