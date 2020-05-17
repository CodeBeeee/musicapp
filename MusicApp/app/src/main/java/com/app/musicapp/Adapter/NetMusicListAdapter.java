package com.app.musicapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.musicapp.Bean.NetSongBean;
import com.app.musicapp.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class NetMusicListAdapter extends RecyclerView.Adapter<NetMusicListAdapter.ViewHolder> {
    private OnMyItemClickListener listener;
    private List<NetSongBean.Info> mydata;
    private Context context;
    public void setOnMyItemClickListener(OnMyItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnMyItemClickListener {
        void myClick(View v, int pos);
    }

    public NetMusicListAdapter(List<NetSongBean.Info> mydata,Context context) {
        this.mydata = mydata;
        this.context = context;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView musictitle,musicauthor;
        ImageView more,ablemimg;
        LinearLayout item;
        public ViewHolder(View view){
            super(view);
            ablemimg = view.findViewById(R.id.ablemimg);
            musictitle = view.findViewById(R.id.musictitle);
            musicauthor = view.findViewById(R.id.musicauthor);
            more = view.findViewById(R.id.more);
            item = view.findViewById(R.id.item);
        }
    }
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.netmusicitem_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        NetSongBean.Info info = mydata.get(position);
//        holder.ablemimg.set(String.valueOf(position+1));
        Glide.with(this.context).load(info.getPic_small()).into(holder.ablemimg);
        holder.musictitle.setText(info.getTitle());
        holder.musicauthor.setText(info.getAuthor());
        if(listener!=null){
            holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    listener.myClick(view, position);
                }
            });
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    listener.myClick(view, position);
                }
            });
        }
    }
    public int getItemCount() {
        return mydata.size() ;
    }
}

