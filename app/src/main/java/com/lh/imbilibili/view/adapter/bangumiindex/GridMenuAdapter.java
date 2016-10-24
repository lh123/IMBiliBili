package com.lh.imbilibili.view.adapter.bangumiindex;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lh.imbilibili.R;
import com.lh.imbilibili.model.bangumi.BangumiIndexCond;

import java.util.List;

/**
 * Created by liuhui on 2016/9/3.
 */
public class GridMenuAdapter extends BaseAdapter {
    private List<BangumiIndexCond.Category> mBangumiCategories;
    private String selectedItemId = "0";


    public void setmBangumiCategories(List<BangumiIndexCond.Category> bangumiCategories) {
        mBangumiCategories = bangumiCategories;
    }

    @Override
    public int getCount() {
        if(mBangumiCategories == null){
            return 0;
        }else {
            return mBangumiCategories.size();
        }
    }

    @Override
    public BangumiIndexCond.Category getItem(int position) {
        return mBangumiCategories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void selectItem(String id){
        selectedItemId = id;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bangumi_index_cond_grid_item,parent,false);
            ViewHolder holder = new ViewHolder();
            holder.mTagName = (TextView) convertView.findViewById(R.id.tag_name);
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.mTagName.setText(getItem(position).getTagName());
        if(selectedItemId.equals(getItem(position).getTagId())){
            holder.mTagName.setTextColor(ContextCompat.getColor(parent.getContext(),R.color.white));
            holder.mTagName.setBackgroundResource(R.color.colorPrimary);
        }else {
            holder.mTagName.setTextColor(ContextCompat.getColor(parent.getContext(),R.color.black));
            holder.mTagName.setBackgroundResource(android.R.color.transparent);
        }
        return convertView;
    }

    private static class ViewHolder{
        private TextView mTagName;
    }
}
