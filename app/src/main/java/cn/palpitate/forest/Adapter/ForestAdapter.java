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
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.palpitate.forest.Activity.MainActivity;
import cn.palpitate.forest.Activity.PlayActivity;
import cn.palpitate.forest.Class.ForestSeed;
import cn.palpitate.forest.Class.LocalForestSeed;
import cn.palpitate.forest.R;

/**
 * Package : cn.palpitate.forest.Adapter
 * Created : 余悸
 * Date : 2018/9/23 15:51
 */
public class ForestAdapter extends RecyclerView.Adapter<ForestAdapter.ViewHolder> {
    private List<ForestSeed> forestSeedList;
    private Context mContext;
    public ForestAdapter(List<ForestSeed> forestSeedList,Context context) {
        this.forestSeedList = forestSeedList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recycle_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //每项注册点击事件
                int position = holder.getAdapterPosition();
             //   Toast.makeText(mContext, "You Clicked " + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, PlayActivity.class);
                intent.putExtra("position",position);
                intent.putExtra("which","Main");
                intent.putExtra("ForestData",forestSeedList.get(position));
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ForestSeed forestSeed = forestSeedList.get(position);
        if (!MainActivity.hideImage) {
            Glide.with(mContext).load(forestSeed.getImageUrl()).into(holder.main_image);
        }
        holder.classFor.setText(forestSeed.getClassFor());
        holder.title.setText(forestSeed.getTitle());
        holder.updateTime.setText(forestSeed.getUpdateTime());
        holder.viewers.setText(forestSeed.getViewers());
    }

    @Override
    public int getItemCount() {
        return forestSeedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        ImageView main_image;
        TextView classFor;
        TextView title;
        TextView updateTime;
        TextView viewers;
        public ViewHolder(View view) {
            super(view);
            this.view = view;
            main_image = view.findViewById(R.id.main_image);
            classFor = view.findViewById(R.id.classFor);
            title = view.findViewById(R.id.main_title);
            updateTime = view.findViewById(R.id.updateTime);
            viewers = view.findViewById(R.id.viewers);
        }
    }
    public void setList(List<ForestSeed> forestSeedList) {
        this.forestSeedList = forestSeedList;
        notifyDataSetChanged();
    }
}
