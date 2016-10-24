package com.lh.imbilibili.view.usercenter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lh.imbilibili.R;
import com.lh.imbilibili.model.user.UserCenter;
import com.lh.imbilibili.utils.BusUtils;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.usercenter.HomeItemDecoration;
import com.lh.imbilibili.view.adapter.usercenter.HomeRecyclerViewAdapter;
import com.lh.imbilibili.view.bangumi.BangumiDetailActivity;
import com.lh.imbilibili.view.video.VideoDetailActivity;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/16.
 */

public class UserCenterHomeFragment extends BaseFragment implements HomeRecyclerViewAdapter.OnItemClickListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private HomeRecyclerViewAdapter mAdapter;
    private boolean mHaveReceiverEvent;

    public static UserCenterHomeFragment newInstance() {
        return new UserCenterHomeFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        BusUtils.getBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BusUtils.getBus().unregister(this);
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mHaveReceiverEvent = false;
        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new HomeRecyclerViewAdapter(getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = mAdapter.getItemViewType(position);
                switch (type) {
                    case HomeRecyclerViewAdapter.TYPE_ARCHIVE_HEAD:
                    case HomeRecyclerViewAdapter.TYPE_ARCHIVE_ITEM:
                    case HomeRecyclerViewAdapter.TYPE_COIN_ARCHIVE_HEAD:
                    case HomeRecyclerViewAdapter.TYPE_COIN_ARCHIVE_ITEM:
                    case HomeRecyclerViewAdapter.TYPE_FAVOURITE_HEAD:
                    case HomeRecyclerViewAdapter.TYPE_FOLLOW_BANGUMI_HEAD:
                    case HomeRecyclerViewAdapter.TYPE_COMMUNITY_HEAD:
                    case HomeRecyclerViewAdapter.TYPE_COMMUNITY_ITEM:
                    case HomeRecyclerViewAdapter.TYPE_GAME_HEAD:
                    case HomeRecyclerViewAdapter.TYPE_GAME_ITEM:
                        return 3;
                    default:
                        return 1;
                }
            }
        });
        mRecyclerView.addItemDecoration(new HomeItemDecoration(getContext()));
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    @Subscribe
    public void onUserCenterDataLoadFinish(UserCenter userCenter) {
        if (mHaveReceiverEvent) {
            return;
        }
        mHaveReceiverEvent = true;
        mAdapter.setUserCenter(userCenter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_user_center_home;
    }

    @Override
    public void onItemClick(int type, String data) {
        switch (type) {
            case HomeRecyclerViewAdapter.TYPE_ARCHIVE_HEAD:
                BusUtils.getBus().post(new ItemClickEvent(1));
                break;
            case HomeRecyclerViewAdapter.TYPE_FAVOURITE_HEAD:
                BusUtils.getBus().post(new ItemClickEvent(2));
                break;
            case HomeRecyclerViewAdapter.TYPE_FOLLOW_BANGUMI_HEAD:
                BusUtils.getBus().post(new ItemClickEvent(3));
                break;
            case HomeRecyclerViewAdapter.TYPE_COMMUNITY_HEAD:
                BusUtils.getBus().post(new ItemClickEvent(4));
                break;
            case HomeRecyclerViewAdapter.TYPE_COIN_ARCHIVE_HEAD:
                BusUtils.getBus().post(new ItemClickEvent(5));
                break;
            case HomeRecyclerViewAdapter.TYPE_GAME_HEAD:
                BusUtils.getBus().post(new ItemClickEvent(6));
                break;
            case HomeRecyclerViewAdapter.TYPE_FOLLOW_BANGUMI_ITEM:
                BangumiDetailActivity.startActivity(getContext(), data);
                break;
            case HomeRecyclerViewAdapter.TYPE_ARCHIVE_ITEM:
            case HomeRecyclerViewAdapter.TYPE_COIN_ARCHIVE_ITEM:
                VideoDetailActivity.startActivity(getContext(), data);
                break;
            case HomeRecyclerViewAdapter.TYPE_COMMUNITY_ITEM:
            case HomeRecyclerViewAdapter.TYPE_GAME_ITEM:
                break;
        }
    }

    /**
     * home中的Item点击位置事件
     */
    public static class ItemClickEvent {
        public int position;

        ItemClickEvent(int position) {
            this.position = position;
        }
    }
}
