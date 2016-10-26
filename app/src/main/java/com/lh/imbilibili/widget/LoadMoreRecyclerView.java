package com.lh.imbilibili.widget;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/6.
 */

public class LoadMoreRecyclerView extends RecyclerView {

    public static final int TYPE_LOAD_MORE = -1;

    public static final int STATE_REFRESHING = 1;
    public static final int STATE_FAIL = 2;
    public static final int STATE_RETRY = 3;
    public static final int STATE_NO_MORE = 4;

    private boolean mIsLoading = false;
    private boolean mEnableLoadMore = true;
    private boolean mShowLoadingView = true;

    private LoadMoreAdapter mAdapter;
    private String mLoadMoreViewText;

    private boolean mShowProgressBar = true;

    private OnLoadMoreLinstener mOnLoadMoreLinstener;
    private OnLoadMoreViewClickListener mOnLoadMoreViewClickListener;

    private boolean mLoadViewClickable;

    private int mCurrentLoadViewState;

    public LoadMoreRecyclerView(Context context) {
        super(context);
        init();
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mLoadMoreViewText = getResources().getString(R.string.loading);
        mShowProgressBar = true;
        mLoadViewClickable = false;
        mCurrentLoadViewState = STATE_REFRESHING;
        addOnScrollListener(new LoadMoreScrollLinstener());
    }


    @SuppressWarnings("unchecked")
    public void setAdapter(Adapter adapter) {
        if (adapter == null) {
            return;
        }
        mAdapter = new LoadMoreAdapter(adapter);
        adapter.registerAdapterDataObserver(new AdapterDataObserver() {
            @Override
            public void onChanged() {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                mAdapter.notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                mAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mAdapter.notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                mAdapter.notifyItemRangeRemoved(positionStart, itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                mAdapter.notifyItemMoved(fromPosition, toPosition);
            }
        });
        super.setAdapter(mAdapter);
    }

    public void setEnableLoadMore(boolean enable) {
        if (mEnableLoadMore == enable) {
            return;
        }
        mEnableLoadMore = enable;
    }

    private void setLoadView(String text, boolean showProgress) {
        mLoadMoreViewText = text;
        mShowProgressBar = showProgress;
        if (getLayoutManager().getChildCount() == 0 || !mShowLoadingView || mAdapter == null) {
            return;
        }
        mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
    }

    public void setLodingViewState(@IntRange(from = 0, to = 4) int state) {
        if (mCurrentLoadViewState == state) {
            return;
        }
        mCurrentLoadViewState = state;
        switch (state) {
            case STATE_REFRESHING:
                mLoadViewClickable = false;
                setLoadView(getResources().getString(R.string.loading), true);
                break;
            case STATE_FAIL:
                mLoadViewClickable = false;
                setLoadView(getResources().getString(R.string.load_error), false);
                break;
            case STATE_RETRY:
                mLoadViewClickable = true;
                setLoadView(getResources().getString(R.string.load_failed_with_click), false);
                break;
            case STATE_NO_MORE:
                mLoadViewClickable = false;
                setLoadView(getResources().getString(R.string.no_data_tips), false);
                break;
        }
    }

    public void setShowLoadingView(boolean show) {
        if (mShowLoadingView == show) {
            return;
        }
        mShowLoadingView = show;
        if (getLayoutManager().getChildCount() == 0 || mAdapter == null) {
            return;
        }
        if (mShowLoadingView) {
            mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
        } else {
            mAdapter.notifyItemRemoved(mAdapter.getItemCount());
        }
    }

    public void setOnLoadMoreLinstener(OnLoadMoreLinstener linstener) {
        mOnLoadMoreLinstener = linstener;
    }

    public void setOnLoadMoreViewClickListener(OnLoadMoreViewClickListener listener) {
        mOnLoadMoreViewClickListener = listener;
    }

    public int getItemViewType(int position) {
        if (mAdapter == null) {
            return TYPE_LOAD_MORE;
        } else {
            return mAdapter.getItemViewType(position);
        }
    }

    public void setLoading(boolean loading) {
        mIsLoading = loading;
    }

    public class LoadMoreAdapter extends Adapter<ViewHolder> {

        private Adapter<ViewHolder> mInternalAdapter;

        LoadMoreAdapter(Adapter<ViewHolder> innerAdapter) {
            mInternalAdapter = innerAdapter;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_LOAD_MORE) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_load_more_item, parent, false);
                return new LoadMoreViewHolder(view);
            } else {
                return mInternalAdapter.onCreateViewHolder(parent, viewType);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (getItemViewType(position) == TYPE_LOAD_MORE) {
                LoadMoreViewHolder loadMoreViewHolder = (LoadMoreViewHolder) holder;
                loadMoreViewHolder.textView.setText(mLoadMoreViewText);
                loadMoreViewHolder.progressBar.setVisibility(mShowProgressBar ? VISIBLE : GONE);
                loadMoreViewHolder.itemView.setClickable(mLoadViewClickable);
            } else {
                mInternalAdapter.onBindViewHolder(holder, position);
            }
        }

        @Override
        public int getItemCount() {
            if (mShowLoadingView) {
                return mInternalAdapter.getItemCount() + 1;
            } else {
                return mInternalAdapter.getItemCount();
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (mShowLoadingView && position == getItemCount() - 1) {
                return TYPE_LOAD_MORE;
            } else {
                return mInternalAdapter.getItemViewType(position);
            }
        }

        class LoadMoreViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

            @BindView(R.id.progressbar)
            ProgressBar progressBar;
            @BindView(R.id.text)
            TextView textView;

            LoadMoreViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (mOnLoadMoreViewClickListener != null) {
                    mOnLoadMoreViewClickListener.onLoadMoreViewClick();
                }
            }
        }
    }

    public interface OnLoadMoreLinstener {
        void onLoadMore();
    }

    public interface OnLoadMoreViewClickListener {
        void onLoadMoreViewClick();
    }

    public class LoadMoreScrollLinstener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager.getChildCount() <= 0) {
                return;
            }
            if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                    !mIsLoading && mOnLoadMoreLinstener != null &&
                    mEnableLoadMore && !canScrollVertically(1) &&
                    mAdapter != null && mAdapter.getItemCount() > 0) {
                mIsLoading = true;
                ToastUtils.showToastShort("refresh");
                mOnLoadMoreLinstener.onLoadMore();
            }
        }
    }
}
