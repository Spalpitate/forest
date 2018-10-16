package cn.palpitate.forest.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.palpitate.forest.Activity.MainActivity;
import cn.palpitate.forest.Activity.PlayActivity;
import cn.palpitate.forest.Class.ForestSeed;
import cn.palpitate.forest.R;

/**
 * Package : cn.palpitate.forest.Adapter
 * Created : 余悸
 * Date : 2018/9/24 10:47
 */
public class ContentRVAdapter extends RecyclerView.Adapter<ContentRVAdapter.ViewHolder> {
    private Context mContext;
    private List<ForestSeed> randomForestSeedList;      //随机推荐6个过来

    public ContentRVAdapter(Context mContext, List<ForestSeed> randomForestSeedList) {
        this.mContext = mContext;
        this.randomForestSeedList = randomForestSeedList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView recycle_video_title;
        ImageView recycle_video_image;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            recycle_video_image = itemView.findViewById(R.id.recycle_video_image);
            recycle_video_title = itemView.findViewById(R.id.recycle_video_title);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_recycle_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        //注册点击事件
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayActivity.class);
                int position = holder.getAdapterPosition();
                intent.putExtra("position",position);
                intent.putExtra("which","Play");
                intent.putExtra("ForestData",randomForestSeedList.get(position));       //直接传递对象
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ForestSeed forestSeed = randomForestSeedList.get(position);
        if (!MainActivity.hideImage) {
            Glide.with(mContext).load(forestSeed.getImageUrl()).into(holder.recycle_video_image);
        }
        holder.recycle_video_title.setText(forestSeed.getTitle());
    }


    @Override
    public int getItemCount() {
        return randomForestSeedList.size();
    }


}
