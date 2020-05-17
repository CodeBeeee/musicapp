package com.app.musicapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.musicapp.MyApp;
import com.app.musicapp.R;
import com.app.musicapp.db.Gedandb;
import com.app.musicapp.db.Musicdb;
import com.app.musicapp.gen.MusicdbDao;
import com.bumptech.glide.Glide;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeDanListManagerAdapter extends RecyclerView.Adapter<GeDanListManagerAdapter.ViewHolder>{
    private NetMusicListAdapter.OnMyItemClickListener listener;
    private List<Gedandb> mydata;
    private Context context;
    private Map<Integer,Boolean> checkstaus = new HashMap<>();
    public void setOnMyItemClickListener(NetMusicListAdapter.OnMyItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnMyItemClickListener {
        void myClick(View v, int pos);
    }

    public GeDanListManagerAdapter(List<Gedandb> mydata,Context context) {
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
        TextView gedanname,gedannum;
        ImageView gedanimg;
        LinearLayout item;
        CheckBox checkBox;
        public ViewHolder(View view){
            super(view);
            gedanimg = view.findViewById(R.id.ablemimg);
            gedanname = view.findViewById(R.id.musictitle);
            gedannum = view.findViewById(R.id.musicauthor);
            checkBox = view.findViewById(R.id.box);
            item = view.findViewById(R.id.item);
        }
    }
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.musicbox, parent, false);
        ViewHolder holder = new GeDanListManagerAdapter.ViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){

        Gedandb info = mydata.get(position);
        MusicdbDao ud = MyApp.getInstances().getDaoSession().getMusicdbDao();
        QueryBuilder<Musicdb> builder = ud.queryBuilder();
        List<Musicdb> tmp = builder.where(MusicdbDao.Properties.Gedanid.eq(info.getGedanid())).list();
        if(tmp.size()!=0) Glide.with(context).load(tmp.get(0).getPic_small()).into(holder.gedanimg);
        holder.gedanname.setText(info.getGedanname());
        holder.gedannum.setText(info.getGedanhasnum()+"");
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
