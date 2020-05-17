package com.app.musicapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.musicapp.Bean.GeDanBean;
import com.app.musicapp.Bean.SongBean;
import com.app.musicapp.R;

import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder> {
    private OnMyItemClickListener listener;
    private List<SongBean> mydata;

    public void setOnMyItemClickListener(OnMyItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnMyItemClickListener {
        void myClick(View v, int pos);
    }

    public MusicListAdapter(List<SongBean> mydata) {
        this.mydata = mydata;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView xuhao,musictitle,musicauthor;
        LinearLayout item;
        public ViewHolder(View view){
            super(view);
            xuhao = view.findViewById(R.id.xuhao);
            musictitle = view.findViewById(R.id.musictitle);
            musicauthor = view.findViewById(R.id.musicauthor);
            item = view.findViewById(R.id.item);
        }
    }
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.musicitem_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        SongBean songBean = mydata.get(position);
        holder.xuhao.setText(String.valueOf(position+1));
        holder.musictitle.setText(songBean.getTitle());
        holder.musicauthor.setText(songBean.getArtist());
        if(listener!=null){

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
