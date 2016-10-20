package com.lh.imbilibili.view.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lh.imbilibili.R;
import com.lh.imbilibili.view.BaseFragment;
import com.lh.imbilibili.view.activity.PartitionMoreActivity;
import com.lh.imbilibili.view.adapter.categoryfragment.GridViewAdapter;
import com.lh.imbilibili.view.adapter.categoryfragment.model.PartionModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuhui on 2016/9/29.
 * 分区界面
 */

public class CategoryFragment extends BaseFragment implements GridViewAdapter.OnItemClickListener {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    public static CategoryFragment newInstance() {
        return new CategoryFragment();
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_category;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        GridViewAdapter gridViewAdapter = new GridViewAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(gridViewAdapter);
        gridViewAdapter.setOnItemClickListener(this);
    }


    @Override
    public String getTitle() {
        return "分区";
    }

    @Override
    public void onItemClick(int position) {
        PartionModel.Builder builder = new PartionModel.Builder();
        switch (position) {
            case 0://直播
                // TODO: 2016/10/1
                return;
            case 1://番剧
                builder.setName("番剧")
                        .setId(13)
                        .addSubPartion("连载动画", R.mipmap.ic_category_t33, 33)
                        .addSubPartion("完结动画", R.mipmap.ic_category_t32, 32)
                        .addSubPartion("国产动画", R.mipmap.ic_category_t153, 153)
                        .addSubPartion("资讯", R.mipmap.ic_category_t51, 51)
                        .addSubPartion("官方延伸", R.mipmap.ic_category_t152, 152);
                break;
            case 2://动画
                builder.setName("动画")
                        .setId(1)
                        .addSubPartion("MAD·AMV", R.mipmap.ic_category_t24, 24)
                        .addSubPartion("MMD·3D", R.mipmap.ic_category_t25, 25)
                        .addSubPartion("短片·手书·配音", R.mipmap.ic_category_t47, 47)
                        .addSubPartion("综合", R.mipmap.ic_category_t27, 27);
                break;
            case 3://音乐
                builder.setName("音乐")
                        .setId(3)
                        .addSubPartion("翻唱", R.mipmap.ic_category_t31, 31)
                        .addSubPartion("VOVCALOID·UTAU", R.mipmap.ic_category_t30, 30)
                        .addSubPartion("演奏", R.mipmap.ic_category_t59, 59)
                        .addSubPartion("OP/ED/OST", R.mipmap.ic_category_t54, 54)
                        .addSubPartion("原创音乐", R.mipmap.ic_category_t28, 28)
                        .addSubPartion("三次元音乐", R.mipmap.ic_category_t29, 29)
                        .addSubPartion("音乐选集", R.mipmap.ic_category_t130, 130);
                break;
            case 4://舞蹈
                builder.setName("舞蹈")
                        .setId(129)
                        .addSubPartion("宅舞", R.mipmap.ic_category_t20, 20)
                        .addSubPartion("三次元舞蹈", R.mipmap.ic_category_t154, 154)
                        .addSubPartion("舞蹈教程", R.mipmap.ic_category_t156, 156);

                break;
            case 5://游戏
                builder.setName("游戏")
                        .setId(4)
                        .addSubPartion("单机联机", R.mipmap.ic_category_t17, 17)
                        .addSubPartion("网游·竞技", R.mipmap.ic_category_t65, 65)
                        .addSubPartion("音游", R.mipmap.ic_category_t136, 136)
                        .addSubPartion("MUGEN", R.mipmap.ic_category_t19, 19)
                        .addSubPartion("GMV", R.mipmap.ic_category_t121, 121)
                        .addSubPartion("游戏中心", R.mipmap.ic_category_game_center2, -1);

                break;
            case 6://科技
                builder.setName("科技")
                        .setId(36)
                        .addSubPartion("纪录片", R.mipmap.ic_category_t37, 37)
                        .addSubPartion("趣味科普人文", R.mipmap.ic_category_t124, 124)
                        .addSubPartion("野生技术协会", R.mipmap.ic_category_t122, 122)
                        .addSubPartion("演讲·公开课", R.mipmap.ic_category_t39, 39)
                        .addSubPartion("星海", R.mipmap.ic_category_t96, 96)
                        .addSubPartion("数码", R.mipmap.ic_category_t95, 95)
                        .addSubPartion("机械", R.mipmap.ic_category_t98, 98);
                break;
            case 7://生活
                builder.setName("生活")
                        .setId(160)
                        .addSubPartion("搞笑", R.mipmap.ic_category_t138, 138)
                        .addSubPartion("日常", R.mipmap.ic_category_t21, 21)
                        .addSubPartion("美食圈", R.mipmap.ic_category_t76, 76)
                        .addSubPartion("动物圈", R.mipmap.ic_category_t75, 75)
                        .addSubPartion("手工", R.mipmap.ic_category_t161, 161)
                        .addSubPartion("绘画", R.mipmap.ic_category_t162, 162)
                        .addSubPartion("运动", R.mipmap.ic_category_t163, 163);
                break;
            case 8://鬼畜
                builder.setName("鬼畜")
                        .setId(119)
                        .addSubPartion("鬼畜调教", R.mipmap.ic_category_t22, 22)
                        .addSubPartion("音MAD", R.mipmap.ic_category_t26, 26)
                        .addSubPartion("人力VOCALOID", R.mipmap.ic_category_t126, 126)
                        .addSubPartion("教程演示", R.mipmap.ic_category_t127, 127);
                break;
            default:
                return;
        }
        PartitionMoreActivity.startActivity(getContext(), builder.build());
    }
}
