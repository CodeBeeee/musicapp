package com.app.musicapp.View.Activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.musicapp.Bean.SongBean;
import com.app.musicapp.MyApp;
import com.app.musicapp.R;
import com.app.musicapp.Util.AppConstantUtil;
import com.app.musicapp.Util.GaussianBlurUtil;
import com.app.musicapp.Util.MediaUtil;
import com.app.musicapp.db.PlayMusic;
import com.app.musicapp.service.NetPlayerService;
import com.app.musicapp.service.PlayerService;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

import me.wcy.lrcview.LrcView;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {
    private RadiusImageView mPlayPause; //暂停按钮
    private RadiusImageView mNext;  //下一首按钮
    private RadiusImageView mPrevious; //上一首按钮
    private int currentTime; //进度条当前播放的时间
    private PlayerReceiver playerReceiver;
    private boolean isPlaying;
    private boolean isPause;
    private Bitmap mResource;
    private static int mCurrentPosition=-1;
    private Boolean flag;
    private String url;
    private SongBean mSongBean;
    private ArrayList<SongBean> musicInfos;
    public static final String UPDATE_ACTION = "com.lzw.action.UPDATE_ACTION";
    public static final String CTL_ACTION = "com.lzw.action.CTL_ACTION";
    public static final String MUSIC_CURRENT = "com.lzw.action.MUSIC_CURRENT";
    public static final String MUSIC_DURATION = "com.lzw.action.MUSIC_DURATION";
    public static final String MUSIC_PLAYING = "com.lzw.action.MUSIC_PLAYING";
    private TextView musictitle,musicartist,currentprogress,duration;
    private RadiusImageView albumimg; //专辑图片
    private SeekBar music_progressBar; //进度条
    private LrcView lrcView;//歌词
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initData();
        initView();
    }

    private void initData() {

        Intent intent=getIntent();
        int position=intent.getIntExtra("position", -1);
        if(position!=mCurrentPosition){
            mCurrentPosition=position;
            flag=true;
            isPause=false;
            isPlaying=true;
        }else {
            flag=false;
            isPlaying=true;
            isPause=false;
        }
        musicInfos=MediaUtil.getAllSongs(this);
        mSongBean=musicInfos.get(mCurrentPosition);

        Log.v("msgmsg",mSongBean.toString());
    }

    @SuppressLint("NewApi")
    private void initView() {
        lrcView = findViewById(R.id.lrc_view);
        mPlayPause = findViewById(R.id.btn_play_pause);
        mPrevious = findViewById(R.id.btn_previous);
        mNext = findViewById(R.id.btn_next);
        music_progressBar = findViewById(R.id.music_progressBar);
        albumimg = findViewById(R.id.albumimg);
        musictitle = findViewById(R.id.musictitle);
        musicartist = findViewById(R.id.musicartist);
        currentprogress = findViewById(R.id.currenttime);
        duration = findViewById(R.id.duration);
        mPrevious.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mPlayPause.setOnClickListener(this);
        mResource = MediaUtil.getArtwork(this, mSongBean.getId(), mSongBean.getAlbumId(),true);
        if(mResource==null){
            Log.v("msgimg","null");
            mResource = BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_album_black_24dp);
        }
        Log.v("msgmsgweb","1");
        setViewContent(mResource);
        sendMainActivity(mResource);//将播放信息传给底部固定播放栏
        musictitle.setText(mSongBean.getTitle());
        musicartist.setText(mSongBean.getArtist());
        music_progressBar.setMax(Math.toIntExact(mSongBean.getDuration()));
        duration.setText(MediaUtil.formatTime(Math.toIntExact(mSongBean.getDuration())));
        music_progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                if (fromUser) {
                    mPlayPause.setBackgroundResource(R.drawable.ic_redpause_circle_outline_black_24dp);
                    isPlaying=true;
                    isPause=false;
                    audioTrackChange(progress);
                    lrcView.updateTime(progress);
//                    lrcView.updateTime(seekBar.getProgress());
                }
            }
        });
        initLrcText(); //获取歌词
        playerReceiver = new PlayerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_ACTION);
        filter.addAction(MUSIC_CURRENT);
        filter.addAction(MUSIC_DURATION);
        filter.addAction("mainplay");
        filter.addAction("mainpause");
        registerReceiver(playerReceiver, filter);
        if(flag){
            play();
            isPlaying=true;
            isPause=false;
        }else{
            Intent intent2=new Intent(this,PlayerService.class);
            intent2.putExtra("MSG", AppConstantUtil.PlayerMsg.PLAYING_MSG);
            intent2.putExtra("position", mCurrentPosition);
            intent2.setAction("com.lzw.media.MUSIC_SERVICE");
            startService(intent2);
            isPlaying=true;
            isPause=false;
        }
        if(isPause){
            mPlayPause.setBackgroundResource(R.drawable.ic_play_redcircle_outline_black_24dp);
        }else{
            mPlayPause.setBackgroundResource(R.drawable.ic_redpause_circle_outline_black_24dp);
        }
    }
    private void initLrcText() {
//        String mainLrcText = getLrcText("send_it_cn.lrc");
//        lrcView.loadLrc(mainLrcText);
//        lrcView.updateTime(0);
    }

    private void sendMainActivity(Bitmap bitmap) {
        Intent to = new Intent();
        to.putExtra("musictitle",mSongBean.getTitle());
        to.putExtra("musicartist",mSongBean.getArtist());
        to.putExtra("position",mCurrentPosition);
        to.putExtra("localsonglist",(Serializable) musicInfos);
        to.putExtra("from","local");
        to.setAction("musicinfo");
        sendBroadcast(to);
    }

    private void setViewContent(Bitmap mResource) {
        //setBackgroundBitmap(bitmap);

        setAvatarBitmap(mResource);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_play_pause:
                if(isPlaying){
                    pause();
                }else if (isPause){
                    resume();
                }else{
                    play();
                }
                break;

            case R.id.btn_previous:
                mCurrentPosition--;
                if(mCurrentPosition>=0) {
                    mSongBean=musicInfos.get(mCurrentPosition);
                    musictitle.setText(mSongBean.getTitle());
                    musicartist.setText(mSongBean.getArtist());
                    music_progressBar.setProgress(0);
                    mResource = MediaUtil.getArtwork(this, mSongBean.getId(), mSongBean.getAlbumId(), true);
                    previous(mResource);
                    if (isPlaying) {
                        mPlayPause.setBackgroundResource(R.drawable.ic_redpause_circle_outline_black_24dp);
                    } else {
                        mPlayPause.setBackgroundResource(R.drawable.ic_play_redcircle_outline_black_24dp);
                    }
                }else {
                    mCurrentPosition=0;
                    Toast.makeText(PlayActivity.this, "没有上一首了", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_next:
                mCurrentPosition++;
                if(mCurrentPosition<musicInfos.size()) {
                    mSongBean=musicInfos.get(mCurrentPosition);
                    musictitle.setText(mSongBean.getTitle());
                    music_progressBar.setProgress(0);
                    musicartist.setText(mSongBean.getArtist());
                    mResource = MediaUtil.getArtwork(this, mSongBean.getId(), mSongBean.getAlbumId(), true);
                    next(mResource);
                    if (isPlaying){
                        mPlayPause.setBackgroundResource(R.drawable.ic_redpause_circle_outline_black_24dp);
                    } else {
                        mPlayPause.setBackgroundResource(R.drawable.ic_play_redcircle_outline_black_24dp);
                    }
                }else{
                    mCurrentPosition = musicInfos.size() - 1;
                    Toast.makeText(PlayActivity.this, "没有下一首了", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                break;
        }

    }
    public void setBackgroundBitmap(Bitmap bitmap){
        //mBackground.setBackgroundDrawable(GaussianBlurUtil.BoxBlurFilter(bitmap));
    }

    public void play(){
        //关闭网络的音乐服务
        Intent stop = new Intent(this,NetPlayerService.class);
        stop.setAction("com.lzw.media.MUSIC_SERVICE");
        stopService(stop);
        saveplaymusic(mSongBean);
        Intent intent = new Intent();
        intent.setAction("com.lzw.media.MUSIC_SERVICE");
        intent.setClass(this, PlayerService.class);
        intent.putExtra("url", mSongBean.getUrl());
        intent.putExtra("position", mCurrentPosition);
        intent.putExtra("MSG", AppConstantUtil.PlayerMsg.PLAY_MSG);
        startService(intent);
        mPlayPause.setBackgroundResource(R.drawable.ic_redpause_circle_outline_black_24dp);
        isPlaying=true;
        isPause=false;
    }
    public void pause(){
        Intent intent=new Intent();
        intent.setClass(PlayActivity.this, PlayerService.class);
        intent.setAction("com.lzw.media.MUSIC_SERVICE");
        intent.putExtra("MSG", AppConstantUtil.PlayerMsg.PAUSE_MSG);
        startService(intent);
        isPlaying = false;
        isPause = true;
        mPlayPause.setBackgroundResource(R.drawable.ic_play_redcircle_outline_black_24dp);
    }
    private void resume(){
        Intent intent=new Intent();
        intent.setAction("com.lzw.media.MUSIC_SERVICE");
        intent.setClass(PlayActivity.this, PlayerService.class);
        intent.putExtra("MSG", AppConstantUtil.PlayerMsg.CONTINUE_MSG);
        startService(intent);
        isPause = false;
        isPlaying = true;
        mPlayPause.setBackgroundResource(R.drawable.ic_redpause_circle_outline_black_24dp);
    }
    public void previous(Bitmap bitmap){
        //pause();
        changeImage(bitmap);
        play();
    }

    public void next(Bitmap bitmap) {
        //pause();
        changeImage(bitmap);
        play();
    }
    public void saveplaymusic(SongBean info){
        PlayMusic add = new PlayMusic();
        add.setSongid(info.getAlbumId());
        add.setSongname(info.getDisplayName());
        add.setSongauthor(info.getArtist());
        add.setSongimg(info.getUrl());
        MyApp.getInstances().getDaoSession().insertOrReplace(add);
    }
    public  void setAvatarBitmap(Bitmap bitmap){
        if(bitmap==null){
            albumimg.setBackgroundResource(R.drawable.ic_album_black_24dp);
        }else{
            albumimg.setImageBitmap(bitmap);
        }

    }

    private void changeImage(final Bitmap bitmap){
        albumimg.postDelayed(new Runnable() {

            @Override
            public void run() {
                setAvatarBitmap(bitmap);
            }
        }, 100);
//        mBackground.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                setBackgroundBitmap(bitmap);
//            }
//        }, 100);
    }

    public void audioTrackChange(int progress) {
        Intent intent = new Intent();
        intent.setClass(this, PlayerService.class);
        intent.setAction("com.lzw.media.MUSIC_SERVICE");
        intent.putExtra("url", mSongBean.getUrl());
        intent.putExtra("MSG", AppConstantUtil.PlayerMsg.PROGRESS_CHANGE);
        intent.putExtra("position", mCurrentPosition);
        intent.putExtra("progress", progress);
        startService(intent);
    }

    public class PlayerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MUSIC_CURRENT)) {
                currentTime = intent.getIntExtra("currentTime", -1);
                currentprogress.setText(MediaUtil.formatTime(currentTime));
                lrcView.updateTime(currentTime);
                music_progressBar.setProgress(currentTime);
                Log.v("msgmsgweb","12");
            } else if (action.equals(MUSIC_DURATION)) {
                int d = intent.getIntExtra("duration", -1);
                music_progressBar.setMax(d);
                duration.setText(MediaUtil.formatTime(d));
            } else if (action.equals(UPDATE_ACTION)) {
                mCurrentPosition = intent.getIntExtra("current", -1);
                mSongBean=musicInfos.get(mCurrentPosition);
                url = mSongBean.getUrl();
                if (mCurrentPosition >= 0) {
                    musictitle.setText(mSongBean.getTitle());
                    musicartist.setText(mSongBean.getArtist());
                    mResource = MediaUtil.getArtwork(PlayActivity.this, mSongBean.getId(), mSongBean.getAlbumId(),true);
                    setViewContent(mResource);
                }
                if (mCurrentPosition == 0) {
                    duration.setText(MediaUtil.formatTime(musicInfos.get(
                            mCurrentPosition).getDuration()));
                    mPlayPause.setBackgroundResource(R.drawable.ic_redpause_circle_outline_black_24dp);
                    isPause = true;
                }
            }
        }
    }



}
