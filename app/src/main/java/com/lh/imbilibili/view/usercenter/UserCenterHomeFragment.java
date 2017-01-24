package com.lh.imbilibili.view.usercenter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lh.imbilibili.R;
import com.lh.imbilibili.model.user.UserCenter;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.adapter.usercenter.HomeItemDecoration;
import com.lh.imbilibili.view.adapter.usercenter.HomeRecyclerViewAdapter;
import com.lh.imbilibili.view.bangumi.BangumiDetailActivity;
import com.lh.imbilibili.view.video.VideoDetailActivity;
import com.lh.rxbuslibrary.RxBus;
import com.lh.rxbuslibrary.annotation.Subscribe;
import com.lh.rxbuslibrary.event.EventThread;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/16.
 * 用户中心主页
 */

public class UserCenterHomeFragment extends BaseFragment implements HomeRecyclerViewAdapter.OnItemClickListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private HomeRecyclerViewAdapter mAdapter;

    private UserCenter mUserCenter;
    private UserCenterDataProvider mUserCenterProvider;
    private boolean mIsInitData;

    public static UserCenterHomeFragment newInstance() {
        return new UserCenterHomeFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        RxBus.getInstance().register(this);
        if (mUserCenterProvider != null) {
            mUserCenter = mUserCenterProvider.getUserCenter();
            initData();
        }
    }

    @Subscribe(scheduler = EventThread.UI)
    public void OnUserCenterInfoReceiver(UserCenter userCenter){
        mUserCenter = userCenter;
        initData();
    }

    @Override
    public void onStop() {
        super.onStop();
        RxBus.getInstance().unRegister(this);
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mIsInitData = false;
        if (getActivity() instanceof UserCenterDataProvider) {
            mUserCenterProvider = (UserCenterDataProvider) getActivity();
        }
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

    public void initData() {
        if (mUserCenter == null || mIsInitData) {
            return;
        }
        mIsInitData = true;
        mAdapter.setUserCenter(mUserCenter);
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
                RxBus.getInstance().post(new ItemClickEvent(1));
                break;
            case HomeRecyclerViewAdapter.TYPE_FAVOURITE_HEAD:
                RxBus.getInstance().post(new ItemClickEvent(2));
                break;
            case HomeRecyclerViewAdapter.TYPE_FOLLOW_BANGUMI_HEAD:
                RxBus.getInstance().post(new ItemClickEvent(3));
                break;
            case HomeRecyclerViewAdapter.TYPE_COMMUNITY_HEAD:
                RxBus.getInstance().post(new ItemClickEvent(4));
                break;
            case HomeRecyclerViewAdapter.TYPE_COIN_ARCHIVE_HEAD:
                RxBus.getInstance().post(new ItemClickEvent(5));
                break;
            case HomeRecyclerViewAdapter.TYPE_GAME_HEAD:
                RxBus.getInstance().post(new ItemClickEvent(6));
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
