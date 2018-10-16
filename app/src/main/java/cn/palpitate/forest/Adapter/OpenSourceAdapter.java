package cn.palpitate.forest.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import cn.palpitate.forest.R;

/**
 * Package : cn.palpitate.forest.Adapter
 * Created : 余悸
 * Date : 2018/9/25 10:47
 */
public class OpenSourceAdapter extends RecyclerView.Adapter<OpenSourceAdapter.ViewHolder> {
    private Context mContext;
    private List<Map<String,String>> openSourceList;

    public OpenSourceAdapter(Context mContext, List<Map<String, String>> openSourceList) {
        this.mContext = mContext;
        this.openSourceList = openSourceList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView opName;
        TextView opAuthor;
        TextView opUrl;
        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            opName = itemView.findViewById(R.id.opName);
            opAuthor = itemView.findViewById(R.id.opAuthor);
            opUrl = itemView.findViewById(R.id.opUrl);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.about_recycle_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        //注册点击事件
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String,String> opContent = openSourceList.get(position);
        holder.opName.setText(opContent.get("opName"));
        holder.opAuthor.setText(opContent.get("opAuthor"));
        holder.opUrl.setText(opContent.get("opUrl"));
    }

    @Override
    public int getItemCount() {
        return openSourceList.size();
    }
}
