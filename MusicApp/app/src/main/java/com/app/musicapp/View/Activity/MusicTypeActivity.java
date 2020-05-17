package com.app.musicapp.View.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.musicapp.Adapter.NetMusicListAdapter;
import com.app.musicapp.Bean.NetSongBean;
import com.app.musicapp.Bean.SongBean;
import com.app.musicapp.MyApp;
import com.app.musicapp.R;
import com.app.musicapp.Util.AppConstantUtil;
import com.app.musicapp.Util.BaiDuTingApi;
import com.app.musicapp.Util.GsonUtil;
import com.app.musicapp.db.Musicdb;
import com.app.musicapp.db.MyLoveMusic;
import com.app.musicapp.db.PlayMusic;
import com.app.musicapp.service.NetPlayerService;
import com.app.musicapp.service.PlayerService;
import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MusicTypeActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout playdd;
    private ImageView back,ablumimg,playpause;
    private TextView typemusic,nonetx,musictitle,musicartist;
    private RecyclerView list;
    private PlayerReceiver playerReceiver;
    private Handler handler;
    private NetMusicListAdapter netMusicListAdapter;
    private RefreshLayout refreshLayout;
    private NetSongBean netSongBean = new NetSongBean();
    private List<NetSongBean.Info> viewdata = null;
    private int type;
    private boolean flag = false;
    private boolean isPlaying,isPause;
    private int playposition = -1;
    private List<NetSongBean.Info> info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_type);
        initView();
        type = getIntent().getIntExtra("type",0);
        initData(type);
        if(type<=3) getHttpData(type);
        else getcacheData(type);
    }

    private void getcacheData(int type) {
        if(type==4){
            List<MyLoveMusic> musiclist = MyApp.getInstances().getDaoSession().getMyLoveMusicDao().loadAll();
            NetSongBean.Info s ;
            viewdata = new ArrayList<>();
            for(int i=0;i<musiclist.size();i++){
                s = new NetSongBean.Info();
                s.setSong_id(musiclist.get(i).getSongid());
                s.setTitle(musiclist.get(i).getSongname());
                s.setAuthor(musiclist.get(i).getSongauthor());
                s.setPic_small(musiclist.get(i).getSongimg());
                Log.v("msgmsg",s.toString());
                viewdata.add(s);
            }
        }else if(type==5){
            List<Musicdb> musicdbs = MyApp.getInstances().getDaoSession().getMusicdbDao().loadAll();
            NetSongBean.Info s ;
            viewdata = new ArrayList<>();
            for(int i=0;i<musicdbs.size();i++){
                s = new NetSongBean.Info();
                s.setSong_id(musicdbs.get(i).getSongid());
                s.setTitle(musicdbs.get(i).getSongname());
                s.setAuthor(musicdbs.get(i).getSongauthor());
                s.setPic_small(musicdbs.get(i).getPic_small());
                Log.v("msgmsg",s.toString());
                viewdata.add(s);
            }
        }else if(type==6){
            List<PlayMusic> playMusics = MyApp.getInstances().getDaoSession().getPlayMusicDao().loadAll();
            NetSongBean.Info s ;
            viewdata = new ArrayList<>();
            for(int i=0;i<playMusics.size();i++){
                s = new NetSongBean.Info();
                s.setSong_id(playMusics.get(i).getSongid());
                s.setTitle(playMusics.get(i).getSongname());
                s.setAuthor(playMusics.get(i).getSongauthor());
                s.setPic_small(playMusics.get(i).getSongimg());
                Log.v("msgmsg",s.toString());
                viewdata.add(s);
            }
        }
        if (viewdata.size()==0){
            nonetx.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        }else{
            flag = true;
            netMusicListAdapter=new NetMusicListAdapter(viewdata,MusicTypeActivity.this);
            netMusicListAdapter.setOnMyItemClickListener(new NetMusicListAdapter.OnMyItemClickListener() {
                @Override
                public void myClick(View v, int pos) {
                    if(v.getId()==R.id.item){
                        Intent intent2=new Intent(MusicTypeActivity.this,NetPlayActivity.class);
                        intent2.putExtra("position",pos);
                        intent2.putExtra("songlist",(Serializable) viewdata);
                        startActivity(intent2);
                    }else if(v.getId()==R.id.more){
                        Toast.makeText(MusicTypeActivity.this,"asd",Toast.LENGTH_LONG).show();
                    }
                }
            });
            list.setAdapter(netMusicListAdapter);
            list.setLayoutManager(new LinearLayoutManager(MusicTypeActivity.this));
        }
    }

    private void getHttpData(int type) {
        int ran = (int) (Math.random()*20+5); //[5,20]
        int size = (int) (Math.random()*20+10) ; //
        final String method = "baidu.ting.billboard.billList&type="+type+"&size="+size+"&offset="+ran;
        handler.post(new Runnable() {
            @Override
            public void run() {
                String url = BaiDuTingApi.musicApi+method;
                OkHttpClient okHttpClient=new OkHttpClient();
                Request request=new Request.Builder()
                        .url(url)
                        .removeHeader("User-Agent")
                        .addHeader("User-Agent", new WebView(MusicTypeActivity.this).getSettings().getUserAgentString())
                        .build();
                Call call=okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //Toast.makeText(getActivity(),"网络出错",Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData=response.body().string();
                        netSongBean = GsonUtil.GsonToBean(responseData,NetSongBean.class);
                        if(netSongBean.getSong_list().size()==0){
                            nonetx.setVisibility(View.VISIBLE);
                            list.setVisibility(View.GONE);
                        }else{
                            Intent intent = new Intent();
                            intent.setAction("okhttp");
                            sendBroadcast(intent);
                        }

                    }
                });
            }
        });
    }

    private void initData(int type) {
        switch (type){
            case 1:
                typemusic.setText("新歌榜");
                break;
            case 2:
                typemusic.setText("流行音乐");
                break;
            case 4:
                typemusic.setText("我的最爱");
                break;
            case 5:
                typemusic.setText("收藏音乐");
                break;
            case 6:
                typemusic.setText("最近播放");
                break;
        }
    }
    private void initView() {
        back = findViewById(R.id.back);
        typemusic = findViewById(R.id.typemusic);
        list = findViewById(R.id.list);
        nonetx = findViewById(R.id.nonetx);
        refreshLayout = findViewById(R.id.refreshLayout);
        playdd = findViewById(R.id.playdd);
        ablumimg = findViewById(R.id.ablumimg);
        playpause = findViewById(R.id.playpause);
        musictitle = findViewById(R.id.musictitle);
        musicartist = findViewById(R.id.musicartist);
        back.setOnClickListener(this);
        playdd.setOnClickListener(this);
        playpause.setOnClickListener(this);
        musicartist.setText("请前往播放");
        musictitle.setText("");
        playerReceiver = new PlayerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("okhttp");
        filter.addAction("mainplay");
        registerReceiver(playerReceiver, filter);
        Log.v("msgmsg",playerReceiver+"");
        handler = new Handler();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if(type<=3) getHttpData(type);
                else getcacheData(type);
                refreshlayout.finishRefresh(2000,flag);//传入false表示刷新失败
            }
        });
    }
    public class PlayerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            Log.v("actionacas12",action);
            if(action.equals("mainplay")){
                Log.v("actionacas12","asdasd");
            }
            if(action.equals("okhttp")){
                flag = true;
                netMusicListAdapter=new NetMusicListAdapter(netSongBean.getSong_list(),MusicTypeActivity.this);
                netMusicListAdapter.setOnMyItemClickListener(new NetMusicListAdapter.OnMyItemClickListener() {
                    @Override
                    public void myClick(View v, int pos) {
                        if(v.getId()==R.id.item){
                            Intent intent2=new Intent(MusicTypeActivity.this,NetPlayActivity.class);
                            intent2.putExtra("position",pos);
                            intent2.putExtra("songlist",(Serializable) netSongBean.getSong_list());
                            startActivity(intent2);
                        }else if(v.getId()==R.id.more){
                            Toast.makeText(MusicTypeActivity.this,"asd",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                list.setAdapter(netMusicListAdapter);
                list.setLayoutManager(new LinearLayoutManager(MusicTypeActivity.this));
            }else if(action.equals("pause")){
                playpause.setBackgroundResource(R.drawable.ic_play_redcircle_outline_black_24dp);
                isPlaying = false;
                isPause = true;
            }else if(action.equals("play")){
                playpause.setBackgroundResource(R.drawable.ic_redpause_circle_outline_black_24dp);
                isPlaying = true;
                isPause = false;
            }else if(action.equals("musicfrommin")){
                info = (List<NetSongBean.Info>) intent.getSerializableExtra("songlist");
                musictitle.setText(intent.getStringExtra("musictitle"));
                musicartist.setText(intent.getStringExtra("musicartist"));
                playposition = intent.getIntExtra("position",-1);
                Glide.with(MusicTypeActivity.this).load(intent.getStringExtra("musicpath"));
            }
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.playpause:
                if(isPlaying){
                    pause();
                    playpause.setBackgroundResource(R.drawable.ic_play_redcircle_outline_black_24dp);
                    Intent i1 = new Intent();
                    i1.setAction("mainpause");
                    sendBroadcast(i1);
                    isPlaying = false;
                    isPause = true;
                }else if(isPause){
                    resume();
                    playpause.setBackgroundResource(R.drawable.ic_redpause_circle_outline_black_24dp);
                    Intent i1 = new Intent();
                    i1.setAction("mainplay");
                    sendBroadcast(i1);
                    isPlaying = true;
                    isPause = false;
                }
                break;
            case R.id.playdd:
                if(playposition==-1){
                    new AlertDialog
                            .Builder(MusicTypeActivity.this)
                            .setMessage("暂无选取歌曲播放")
                            .setNegativeButton("确定", null)
                            .show();
                }else{
                    Intent intent2=new Intent(MusicTypeActivity.this,NetPlayActivity.class);
                    intent2.putExtra("position",playposition);
                    intent2.putExtra("songlist",(Serializable) info);
                    startActivity(intent2);
                }
                break;
        }
    }

    public void pause(){
        Intent i1=new Intent();
        Intent i2=new Intent();
        i1.setClass(MusicTypeActivity.this, PlayerService.class);
        i2.setClass(MusicTypeActivity.this, NetPlayerService.class);
        i1.setAction("com.lzw.media.MUSIC_SERVICE");
        i2.setAction("com.lzw.media.MUSIC_SERVICE");
        i1.putExtra("MSG", AppConstantUtil.PlayerMsg.PAUSE_MSG);
        i2.putExtra("MSG", AppConstantUtil.PlayerMsg.PAUSE_MSG);
        startService(i1);
        startService(i2);
        isPlaying = false;
        isPause = true;
    }
    private void resume(){
        Intent i1=new Intent();
        Intent i2=new Intent();
        i1.setClass(MusicTypeActivity.this, PlayerService.class);
        i2.setClass(MusicTypeActivity.this, NetPlayerService.class);
        i1.setAction("com.lzw.media.MUSIC_SERVICE");
        i2.setAction("com.lzw.media.MUSIC_SERVICE");
        i1.putExtra("MSG", AppConstantUtil.PlayerMsg.CONTINUE_MSG);
        i2.putExtra("MSG", AppConstantUtil.PlayerMsg.CONTINUE_MSG);
        startService(i1);
        startService(i2);
        isPause = false;
        isPlaying = true;
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
