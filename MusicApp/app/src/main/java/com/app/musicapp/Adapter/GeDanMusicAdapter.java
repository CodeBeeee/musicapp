package com.app.musicapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.musicapp.Bean.NetSongBean;
import com.app.musicapp.R;
import com.app.musicapp.db.Musicdb;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeDanMusicAdapter extends RecyclerView.Adapter<GeDanMusicAdapter.ViewHolder>{
    private NetMusicListAdapter.OnMyItemClickListener listener;
    private List<Musicdb> mydata;
    private Context context;
    private Map<Integer,Boolean> checkstaus = new HashMap<>();
    public void setOnMyItemClickListener(NetMusicListAdapter.OnMyItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnMyItemClickListener {
        void myClick(View v, int pos);
    }

    public GeDanMusicAdapter(List<Musicdb> mydata,Context context) {
        this.mydata = mydata;
        this.context = context;
        initCheck(false);
        notifyDataSetChanged();
    }

    private void initCheck(boolean b) {
        for(int i=0;i<mydata.size();i++){
            checkstaus.put(i,b);
        }
    }

    public Map<Integer, Boolean> getCheckstaus() {
        return checkstaus;
    }

    public void setCheckstaus(Map<Integer, Boolean> checkstaus) {
        this.checkstaus = checkstaus;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView musictitle,musicauthor;
        ImageView ablemimg;
        LinearLayout item;
        CheckBox checkBox;
        public ViewHolder(View view){
            super(view);
            ablemimg = view.findViewById(R.id.ablemimg);
            musictitle = view.findViewById(R.id.musictitle);
            musicauthor = view.findViewById(R.id.musicauthor);
            checkBox = view.findViewById(R.id.box);
            item = view.findViewById(R.id.item);
        }
    }
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.musicbox, parent, false);
        ViewHolder holder = new GeDanMusicAdapter.ViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){

        Musicdb info = mydata.get(position);
//        holder.ablemimg.set(String.valueOf(position+1));
        Glide.with(this.context).load(info.getPic_small()).into(holder.ablemimg);
        holder.musictitle.setText(info.getSongname());
        holder.musicauthor.setText(info.getSongauthor());
        holder.checkBox.setText(mydata.get(position).getGedanid()+"");
        holder.checkBox.setOnClickListener(null);
        holder.checkBox.setChecked(checkstaus.get(position));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkstaus.put(position,b);
            }
        });
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
