package com.app.musicapp.View.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.musicapp.Adapter.GeDanMusicAdapter;
import com.app.musicapp.Adapter.NetMusicListAdapter;
import com.app.musicapp.Bean.NetSongBean;
import com.app.musicapp.MyApp;
import com.app.musicapp.R;
import com.app.musicapp.db.Gedandb;
import com.app.musicapp.db.Musicdb;
import com.app.musicapp.gen.GedandbDao;
import com.app.musicapp.gen.MusicdbDao;
import com.bumptech.glide.Glide;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeDanListActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView back,img;
    private TextView songname,songnum,nonetx;
    private RecyclerView listview;
    private GeDanMusicAdapter geDanMusicAdapter;
    private List<Musicdb> mydata;
    private List<Musicdb> viewdata;
    private List<NetSongBean.Info> gnextList;
    private Long gedanid;
    private String gedanname;
    private int gedannum;
    private Button delete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ge_dan_list);
        initView();
        initData();
    }

    private void initData() {
        gedanid = getIntent().getLongExtra("gedanid",-1);
        gedanname = getIntent().getStringExtra("gedanname");
        gedannum = getIntent().getIntExtra("gedannum",0);
        songname.setText(gedanname);
        songnum.setText(gedannum+"首");
        mydata = new ArrayList<>();
        viewdata = new ArrayList<>();
        gnextList = new ArrayList<NetSongBean.Info>();
        mydata = MyApp.getInstances().getDaoSession().getMusicdbDao().loadAll();
        for(int i=0 ; i<mydata.size();i++){
            Log.v("msgmsg",mydata.get(i).getGedanid()+" "+gedanid);
            if(mydata.get(i).getGedanid()==gedanid){
                viewdata.add(mydata.get(i));
                NetSongBean.Info tmp = new NetSongBean.Info(
                        mydata.get(i).getSongid(),
                        mydata.get(i).getSongname(),
                        mydata.get(i).getSongauthor(),
                        mydata.get(i).getPic_small(),
                        "");
                gnextList.add(tmp);
            }
        }

        if(viewdata.size()==0){
            img.setBackgroundResource(R.drawable.music);
            nonetx.setVisibility(View.VISIBLE);
            listview.setVisibility(View.GONE);
        }else{
            Glide.with(this).load(viewdata.get(0).getPic_small()).into(img);
            geDanMusicAdapter = new GeDanMusicAdapter(viewdata,GeDanListActivity.this);
            listview.setAdapter(geDanMusicAdapter);
            listview.setLayoutManager(new LinearLayoutManager(GeDanListActivity.this));
            geDanMusicAdapter.setOnMyItemClickListener(new NetMusicListAdapter.OnMyItemClickListener() {
                @Override
                public void myClick(View v, int pos) {
                    if(v.getId()==R.id.item){
                        Intent intent2=new Intent(GeDanListActivity.this,NetPlayActivity.class);
                        intent2.putExtra("position",pos);
                        intent2.putExtra("songlist",(Serializable) gnextList);
                        startActivity(intent2);
                    }
                }
            });
        }
    }

    private void initView() {
        back = findViewById(R.id.back);
        img = findViewById(R.id.img);
        songname = findViewById(R.id.songname);
        songnum = findViewById(R.id.songnum);
        listview = findViewById(R.id.listview);
        nonetx = findViewById(R.id.nonetx);
        delete = findViewById(R.id.delete);
        delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.delete:
                delcache();
                break;
        }
    }

    private void delcache() {

        new AlertDialog
                .Builder(GeDanListActivity.this)
                .setTitle("提示")
                .setMessage("是否删除选择歌曲？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos) {
                        Map<Integer,Boolean> checkstaus = geDanMusicAdapter.getCheckstaus();
                        for(int i=0;i<checkstaus.size();i++){
                            if(checkstaus.get(i)==true){
                                Musicdb deldata = viewdata.get(i);
                                MyApp.getInstances().getDaoSession().getMusicdbDao().delete(deldata);
                                GedandbDao ud = MyApp.getInstances().getDaoSession().getGedandbDao();
                                QueryBuilder<Gedandb> builder = ud.queryBuilder();
                                Gedandb data = builder.where(GedandbDao.Properties.Gedanid.eq(gedanid)).unique();
                                data.setGedanhasnum(data.getGedanhasnum()-1);
                                MyApp.getInstances().getDaoSession().getGedandbDao().update(data);
                                Intent intent = new Intent();
                                intent.setAction("gedanviewchange");
                                sendBroadcast(intent);
                            }
                        }
                        initData();
                    }
                })
                .setNegativeButton("取消", null)
                .show();

    }
}
