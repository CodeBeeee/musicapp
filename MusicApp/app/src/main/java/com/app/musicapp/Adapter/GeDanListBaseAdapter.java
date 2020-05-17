package com.app.musicapp.Adapter;

import android.content.Context;
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
import com.app.musicapp.MyApp;
import com.app.musicapp.R;
import com.app.musicapp.db.Gedandb;
import com.app.musicapp.db.Musicdb;
import com.app.musicapp.gen.MusicdbDao;
import com.bumptech.glide.Glide;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class GeDanListBaseAdapter extends RecyclerView.Adapter<GeDanListBaseAdapter.ViewHolder> {
    private OnMyItemClickListener listener;
    private List<Gedandb> mydata;
    private Context context;
    public void setOnMyItemClickListener(OnMyItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnMyItemClickListener {
        void myClick(View v, int pos);
    }

    public GeDanListBaseAdapter(List<Gedandb> mydata,Context context) {
        this.mydata = mydata;
        this.context = context;
        Log.i("size", String.valueOf(mydata.size()));
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView gedanname, gedannum;
        ImageView gedanimg;
        LinearLayout item;
        public ViewHolder(View view){
            super(view);
            gedanimg = view.findViewById(R.id.gedanimg);
            gedanname = view.findViewById(R.id.gedanname);
            gedannum = view.findViewById(R.id.gedannum);
            item = view.findViewById(R.id.item);
        }
    }
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gedan_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
          Log.v("msgmsgpost",position+" ");

          if(position == getItemCount()-1){
              holder.gedanimg.setBackgroundResource(R.drawable.ic_add_box_black_24dp);
              holder.gedanname.setText("添加歌单");
              holder.gedannum.setText("");
          }else {
              Gedandb data = mydata.get(position);
              MusicdbDao ud = MyApp.getInstances().getDaoSession().getMusicdbDao();
              QueryBuilder<Musicdb> builder = ud.queryBuilder();
              List<Musicdb> tmp = builder.where(MusicdbDao.Properties.Gedanid.eq(data.getGedanid())).list();
              if(tmp.size()!=0) Glide.with(context).load(tmp.get(0).getPic_small()).into(holder.gedanimg);
              holder.gedanname.setText(data.getGedanname());
              holder.gedannum.setText(data.getGedanhasnum()+"首");
          }
        if(listener!=null){
            holder.gedanimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.myClick(view,position);
                }
            });
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.myClick(view,position);
                }
            });
        }

    }
    public int getItemCount() {
        return mydata.size() + 1;  //比集合里的数据多一条，显示自定义的添加图片按钮
    }
}
