package com.app.musicapp.View.Activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.app.musicapp.Bean.NetSongBean;
import com.app.musicapp.Bean.PlaySongBean;
import com.app.musicapp.Bean.SongBean;
import com.app.musicapp.MyApp;
import com.app.musicapp.R;
import com.app.musicapp.Util.AppConstantUtil;
import com.app.musicapp.Util.BaiDuTingApi;
import com.app.musicapp.Util.GsonUtil;
import com.app.musicapp.Util.MediaUtil;
import com.app.musicapp.db.PlayMusic;
import com.app.musicapp.service.NetPlayerService;
import com.app.musicapp.service.PlayerService;
import com.bumptech.glide.Glide;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import me.wcy.lrcview.LrcView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetPlayActivity extends AppCompatActivity implements View.OnClickListener{
    private RadiusImageView mPlayPause; //暂停按钮
    private RadiusImageView mNext;  //下一首按钮
    private RadiusImageView mPrevious; //上一首按钮
    private int currentTime; //进度条当前播放的时间
    private PlayerReceiver playerReceiver;
    private boolean isPlaying;
    private boolean isPause;
    private Bitmap mResource = null;
    private static int mCurrentPosition=-1;
    private Boolean flag;
    public static final String UPDATE_ACTION = "com.lzw.action.UPDATE_ACTION";
    public static final String CTL_ACTION = "com.lzw.action.CTL_ACTION";
    public static final String MUSIC_CURRENT = "com.lzw.action.MUSIC_CURRENT";
    public static final String MUSIC_DURATION = "com.lzw.action.MUSIC_DURATION";
    public static final String MUSIC_PLAYING = "com.lzw.action.MUSIC_PLAYING";
    private TextView musictitle,musicartist,currentprogress,duration;
    private RadiusImageView albumimg; //专辑图片
    private SeekBar music_progressBar; //进度条
    private LrcView lrcView;//歌词
    private List<NetSongBean.Info> info;
    private Handler handler;
    private PlaySongBean playSongBean = new PlaySongBean();
    private int currentTag = 0 ;
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_play);
        Intent intent=getIntent();
        position=intent.getIntExtra("position", -1);
        info = (List<NetSongBean.Info>) intent.getSerializableExtra("songlist");
        initService();
        initView();
        getHttpData(0);
    }

    private void initService() {
        playerReceiver = new PlayerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_ACTION);
        filter.addAction(MUSIC_CURRENT);
        filter.addAction(MUSIC_DURATION);
        filter.addAction("mainplay");
        filter.addAction("mainpause");
        filter.addAction("netplayokhttp");
        filter.addAction("premusic");
        filter.addAction("nexmusic");
        registerReceiver(playerReceiver, filter);
        Log.v("webmsg","1->");
    }

    private void getHttpData(int mark) {
        //mark==1代表下一首上一首的切换

        if(position!=mCurrentPosition){
            if(mark==0&&position!=-1) mCurrentPosition=position;
            flag=true;
            isPause=false;
            isPlaying=true;
        }else {
            flag=false;
            isPlaying=true;
            isPause=false;
        }
        final Long songid = info.get(mCurrentPosition).getSong_id();
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                String url = BaiDuTingApi.getsongapi+songid;
                OkHttpClient okHttpClient=new OkHttpClient();
                Request request=new Request.Builder()
                        .url(url)
                        .removeHeader("User-Agent")
                        .addHeader("User-Agent", new WebView(getApplication()).getSettings().getUserAgentString())
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
                        Log.v("msgweb",responseData);
                        playSongBean = GsonUtil.GsonToBean(responseData,PlaySongBean.class);
                        Log.v("msgweb",playSongBean.data.songList.get(0).getLrcLink());
                        Intent intent = new Intent();
                        intent.setAction("netplayokhttp");
                        sendBroadcast(intent);
                    }
                });
            }
        });
    }

    private void initView() {
        handler = new Handler();
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
                }
            }
        });
        Log.v("webmsg","3->");
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
                    NetSongBean.Info mSongBean = info.get(mCurrentPosition);
                    musictitle.setText(mSongBean.getTitle());
                    musicartist.setText(mSongBean.getAuthor());
                    music_progressBar.setProgress(0);
                    if (isPlaying) {
                        mPlayPause.setBackgroundResource(R.drawable.ic_redpause_circle_outline_black_24dp);
                    } else {
                        mPlayPause.setBackgroundResource(R.drawable.ic_play_redcircle_outline_black_24dp);
                    }
                    Intent i = new Intent();
                    i.setAction("premusic");
                    sendBroadcast(i);
                }else {
                    mCurrentPosition=0;
                    Toast.makeText(NetPlayActivity.this, "没有上一首了", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_next:
                mCurrentPosition++;
                if(mCurrentPosition<info.size()) {
                    NetSongBean.Info mSongBean=info.get(mCurrentPosition);
                    musictitle.setText(mSongBean.getTitle());
                    music_progressBar.setProgress(0);
                    musicartist.setText(mSongBean.getAuthor());
                    //mResource = info.get(mCurrentPosition).getPic_small();
                    if (isPlaying){
                        mPlayPause.setBackgroundResource(R.drawable.ic_redpause_circle_outline_black_24dp);
                    } else {
                        mPlayPause.setBackgroundResource(R.drawable.ic_play_redcircle_outline_black_24dp);
                    }
                    Intent i = new Intent();
                    i.setAction("nexmusic");
                    sendBroadcast(i);
                    Log.v("msgmsg--","qq"+mCurrentPosition);
                }else{
                    mCurrentPosition = info.size() - 1;
                    Toast.makeText(NetPlayActivity.this, "没有下一首了", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                break;
        }

    }

    private void sendMainActivity(String path) {
        Intent to = new Intent();
        to.putExtra("musictitle",playSongBean.data.songList.get(0).getSongName());
        to.putExtra("musicartist",playSongBean.data.songList.get(0).getArtistName());
        to.putExtra("musicpath",path);
        to.putExtra("songlist",(Serializable) info);
        to.putExtra("position",mCurrentPosition);
        to.putExtra("from","net");
        to.setAction("musicinfo");
        sendBroadcast(to);
    }
    public void play(){
        sendNotificationManager();
        //关闭本地的音乐服务
        Intent stop = new Intent(this,PlayerService.class);
        stop.setAction("com.lzw.media.MUSIC_SERVICE");
        stopService(stop);
        Intent intent = new Intent();
        intent.setAction("com.lzw.media.MUSIC_SERVICE");
        intent.setClass(this, NetPlayerService.class);
        intent.putExtra("url", playSongBean.data.songList.get(0).getSongLink());
        intent.putExtra("position", mCurrentPosition);
        intent.putExtra("MSG", AppConstantUtil.PlayerMsg.PLAY_MSG);
        startService(intent);
        mPlayPause.setBackgroundResource(R.drawable.ic_redpause_circle_outline_black_24dp);
        isPlaying=true;
        isPause=false;
    }
    public void pause(){
        Intent intent=new Intent();
        intent.setClass(NetPlayActivity.this, NetPlayerService.class);
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
        intent.setClass(NetPlayActivity.this, NetPlayerService.class);
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
    }
    public void audioTrackChange(int progress) {
        Intent intent = new Intent();
        intent.setClass(this, NetPlayerService.class);
        intent.setAction("com.lzw.media.MUSIC_SERVICE");
        intent.putExtra("url", playSongBean.data.songList.get(0).getSongLink());
        intent.putExtra("MSG", AppConstantUtil.PlayerMsg.PROGRESS_CHANGE);
        intent.putExtra("position", mCurrentPosition);
        intent.putExtra("progress", progress);
        startService(intent);
    }
    public class PlayerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.v("msgmsgsss2",action);
            if (action.equals(MUSIC_CURRENT)) {
                currentTime = intent.getIntExtra("currentTime", -1);
                Log.v("program",currentTime+" --> ");
                currentprogress.setText(MediaUtil.formatTime(currentTime));
                lrcView.updateTime(currentTime);
                music_progressBar.setProgress(currentTime);
            } else if (action.equals(MUSIC_DURATION)) {
                int d = intent.getIntExtra("duration", -1);
                Log.v("msgduration",d+" ");
                music_progressBar.setMax(d);
                duration.setText(MediaUtil.formatTime(d));
            } else if (action.equals(UPDATE_ACTION)) {
                mCurrentPosition = intent.getIntExtra("current", -1);
                NetSongBean.Info mSongBean=info.get(mCurrentPosition);
                if (mCurrentPosition >= 0) {
                    musictitle.setText(mSongBean.getTitle());
                    musicartist.setText(mSongBean.getAuthor());
                    Glide.with(NetPlayActivity.this).load(info.get(mCurrentPosition).getPic_small()).into(albumimg);
                    setViewContent(mResource);
                }
                if (mCurrentPosition == 0) {
                    duration.setText(MediaUtil.formatTime(playSongBean.data.songList.get(0).getTime()));
                    mPlayPause.setBackgroundResource(R.drawable.ic_redpause_circle_outline_black_24dp);
                    isPause = true;
                }
            }else if(action.equals("netplayokhttp")){

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                startplay();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });

            }else if(action.equals("premusic")||action.equals("nexmusic")){
                getHttpData(1);
            }if(action.equals("pause")){
                mPlayPause.setBackgroundResource(R.drawable.ic_play_redcircle_outline_black_24dp);
                isPlaying = false;
                isPause = true;
            }else if(action.equals("play")){
                mPlayPause.setBackgroundResource(R.drawable.ic_redpause_circle_outline_black_24dp);
                isPlaying = true;
                isPause = false;
                Log.v("msgmsg","sd");
            }
        }
    }
    public void saveplaymusic(List<NetSongBean.Info> info){
        PlayMusic add = new PlayMusic();
        add.setSongid(info.get(mCurrentPosition).getSong_id());
        add.setSongname(info.get(mCurrentPosition).getTitle());
        add.setSongauthor(info.get(mCurrentPosition).getAuthor());
        add.setSongimg(info.get(mCurrentPosition).getPic_small());
        MyApp.getInstances().getDaoSession().insertOrReplace(add);
    }
    private void sendNotificationManager(){
        // 获取通知管理器
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        // 设置通知栏图片
        mBuilder.setSmallIcon(R.drawable.ic_album_black_24dp);
        Notification notification = mBuilder.build();
        // RemoteViews中自定义Notification布局
        RemoteViews cv = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification_layout);
        // 设置下拉后通知栏图片
        cv.setImageViewResource(R.id.ablumimg, R.drawable.ic_album_black_24dp);
        Intent intent = new Intent(getApplicationContext(),NetPlayerService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        cv.setOnClickPendingIntent(R.id.ablumimg,pendingIntent);
        // 通知时间
        notification.when = System.currentTimeMillis();
        // 设置内容
        notification.contentView = cv;
        // 通过特定id来发送这个通知
        manager.notify(1, notification);
        Log.v("msgmsgtongzhi","123o");
    }
    @SuppressLint("NewApi")
    private void startplay() throws ExecutionException, InterruptedException {
        Glide.with(NetPlayActivity.this).load(info.get(mCurrentPosition).getPic_small()).into(albumimg);
        Log.v("webmsg","4->"+playSongBean.data.songList.get(0).toString());
//        setViewContent(mResource);
        sendMainActivity(info.get(mCurrentPosition).getPic_small());//将播放信息传给底部固定播放栏
        currentprogress.setText(MediaUtil.formatTime(playSongBean.data.songList.get(0).getTime()));
        musictitle.setText(playSongBean.data.songList.get(0).getSongName());
        musicartist.setText(playSongBean.data.songList.get(0).getArtistName());
        music_progressBar.setMax(Math.toIntExact(playSongBean.data.songList.get(0).getTime()*1000));
        duration.setText(MediaUtil.formatTime(Math.toIntExact(playSongBean.data.songList.get(0).getTime()*1000)));
        String lrc = playSongBean.data.songList.get(0).getLrcLink();
        if(lrc.indexOf(".")!=-1){
            if(lrc.substring(lrc.indexOf(".")).equals("txt")){
                lrcView.loadLrc(lrc);
            }else{
                lrcView.loadLrcByUrl(lrc);
            }
        }else{
            lrcView.loadLrcByUrl(lrc);
        }
        lrcView.updateTime(0);
        if(flag){
            Log.v("webmsg","51->");
            play();
            saveplaymusic(info);
            isPlaying=true;
            isPause=false;
        }else{
            Log.v("webmsg","52->");
            Intent intent2=new Intent(this,NetPlayerService.class);
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

}
