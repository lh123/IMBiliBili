package com.lh.imbilibili.view.adapter.usercenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lh.imbilibili.R;
import com.lh.imbilibili.model.user.UserCenter;
import com.lh.imbilibili.utils.DisplayUtils;
import com.lh.imbilibili.utils.transformation.RoundedCornersTransformation;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/10/17.
 */

public class GameRecyclerViewAdapter extends RecyclerView.Adapter<GameRecyclerViewAdapter.GameViewHolder> {
    private Context mContext;

    private List<UserCenter.Game> mGames;

    public GameRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    public void addGames(List<UserCenter.Game> games) {
        if (mGames == null) {
            mGames = games;
        } else {
            mGames.addAll(games);
        }
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GameViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_center_community_game_item, parent, false));
    }

    @Override
    public void onBindViewHolder(GameViewHolder holder, int position) {
        UserCenter.Game game = mGames.get(position);
        Glide.with(mContext)
                .load(game.getIcon())
                .transform(new RoundedCornersTransformation(mContext.getApplicationContext(), DisplayUtils.dip2px(mContext.getApplicationContext(), 15)))
                .into(holder.mIvCover);
        holder.mTvTitle.setText(game.getName());
        holder.mTvDesc.setText(game.getSummary());
    }

    @Override
    public int getItemCount() {
        if (mGames == null) {
            return 0;
        }
        return mGames.size();
    }

    class GameViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cover)
        ImageView mIvCover;
        @BindView(R.id.title)
        TextView mTvTitle;
        @BindView(R.id.description)
        TextView mTvDesc;

        public GameViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
