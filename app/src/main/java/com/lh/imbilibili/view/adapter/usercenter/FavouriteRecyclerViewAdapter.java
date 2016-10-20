package com.lh.imbilibili.view.adapter.usercenter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.lh.imbilibili.model.user.UserCenter;
import com.lh.imbilibili.widget.FavoritesView;

import java.util.List;

/**
 * Created by liuhui on 2016/10/17.
 */

public class FavouriteRecyclerViewAdapter extends RecyclerView.Adapter<FavouriteRecyclerViewAdapter.FavViewHolder> {

    private List<UserCenter.Favourite> mFavourites;

    public void addFavourites(List<UserCenter.Favourite> favourites) {
        if (mFavourites == null) {
            mFavourites = favourites;
        } else {
            mFavourites.addAll(favourites);
        }
    }

    @Override
    public FavViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FavViewHolder(new FavoritesView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(FavViewHolder holder, int position) {
        holder.mFavoritesView.setImages(mFavourites.get(position).getVideos());
    }

    @Override
    public int getItemCount() {
        if (mFavourites == null) {
            return 0;
        }
        return mFavourites.size();
    }

    class FavViewHolder extends RecyclerView.ViewHolder {
        private FavoritesView mFavoritesView;

        public FavViewHolder(View itemView) {
            super(itemView);
            mFavoritesView = (FavoritesView) itemView;
        }
    }
}
