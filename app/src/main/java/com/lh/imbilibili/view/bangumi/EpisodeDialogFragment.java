package com.lh.imbilibili.view.bangumi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lh.imbilibili.R;
import com.lh.imbilibili.model.bangumi.BangumiDetail;
import com.lh.imbilibili.view.adapter.feedback.FeedbackEpAdapter;
import com.lh.imbilibili.view.feedback.FeedbackFragment;

import java.util.List;

/**
 * Created by home on 2016/8/2.
 * 集数选择对话框
 */
public class EpisodeDialogFragment extends DialogFragment implements FeedbackEpAdapter.onEpClickListener {

    public static final String TAG = "EpisodeDialogFragment";

    private static final String EXTRA_DATA = "data";
    private static final String EXTRA_POSITION = "position";

    private RecyclerView mRecyclerView;

    private View mRootView;

    public static EpisodeDialogFragment newInstance(BangumiDetail bangumiDetail, int position) {
        EpisodeDialogFragment fragment = new EpisodeDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_DATA, bangumiDetail);
        bundle.putInt(EXTRA_POSITION, position);
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.TitleDialogTheme);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle("集数选择");
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.dialog_episode_layout, container, false);
            mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
            BangumiDetail bangumiDetail = getArguments().getParcelable(EXTRA_DATA);
            if (bangumiDetail != null) {
                List<BangumiDetail.Episode> episodes = bangumiDetail.getEpisodes();
                FeedbackEpAdapter adapter = new FeedbackEpAdapter(episodes);
                adapter.selectItem(getArguments().getInt(EXTRA_POSITION, 0));
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
                mRecyclerView.setLayoutManager(gridLayoutManager);
                mRecyclerView.setAdapter(adapter);
                adapter.setOnEpClickListener(this);
            }
        }
        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onEpClick(int position) {
        FeedbackFragment fragment = (FeedbackFragment) getFragmentManager().findFragmentByTag(FeedbackFragment.TAG);
        if (fragment != null) {
            fragment.onEpisodeSelect(position);
        }
        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mRootView != null && mRootView.getParent() != null) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }
    }
}
