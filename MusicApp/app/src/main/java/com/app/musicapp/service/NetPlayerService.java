package com.app.musicapp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.app.musicapp.Bean.NetSongBean;
import com.app.musicapp.Bean.PlaySongBean;
import com.app.musicapp.Bean.SongBean;
import com.app.musicapp.R;
import com.app.musicapp.Util.AppConstantUtil;
import com.app.musicapp.Util.MediaUtil;

import java.util.ArrayList;
import java.util.List;

public class NetPlayerService extends Service {

    private MediaPlayer mediaPlayer;
    private String path;
    private int msg;
    private boolean isPause;
    private int mCurrentPosition;
    private int currentTime;
    private int duration;
    private String imgpath;
    private List<NetSongBean.Info> mp3Infos;
    public static final String UPDATE_ACTION = "com.lzw.action.UPDATE_ACTION";
    public static final String CTL_ACTION = "com.lzw.action.CTL_ACTION";
    public static final String MUSIC_CURRENT = "com.lzw.action.MUSIC_CURRENT";
    public static final String MUSIC_DURATION = "com.lzw.action.MUSIC_DURATION";

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                if(mediaPlayer != null) {
                    currentTime = mediaPlayer.getCurrentPosition(); // 获取当前音乐播放的位置
                    Intent intent = new Intent();
                    intent.setAction(MUSIC_CURRENT);
                    intent.putExtra("currentTime", currentTime);
                    sendBroadcast(intent); // 给PlayerActivity发送广播
                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            }
        };
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("service", "service created");
        mediaPlayer = new MediaPlayer();
        mp3Infos =  MediaUtil.list;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mCurrentPosition++;
                Log.v("msgweb", mCurrentPosition+"循环");
                if (mCurrentPosition <= mp3Infos.size() - 1) {
                    Intent sendIntent = new Intent("nexmusic");
                    sendBroadcast(sendIntent);
//                    path = mp3Infos.get(mCurrentPosition).getUrl();
//                    play(0);
                }else {
                    mediaPlayer.seekTo(0);
                    mCurrentPosition = 0;
                    Intent sendIntent = new Intent(UPDATE_ACTION);
                    sendIntent.putExtra("current", mCurrentPosition);
                    sendBroadcast(sendIntent);
                }
            }
        });
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if(intent==null){
            stopSelf();
        }else{
            path = intent.getStringExtra("url");
            msg = intent.getIntExtra("MSG", 0);
            mCurrentPosition=intent.getIntExtra("position",-1);
            if (msg == AppConstantUtil.PlayerMsg.PLAY_MSG) {
                play(0);
            } else if (msg == AppConstantUtil.PlayerMsg.PAUSE_MSG) {
                pause();
            } else if (msg == AppConstantUtil.PlayerMsg.STOP_MSG) {
                stop();
            } else if (msg == AppConstantUtil.PlayerMsg.CONTINUE_MSG) {
                resume();
            } else if (msg == AppConstantUtil.PlayerMsg.PROGRESS_CHANGE) {
                currentTime = intent.getIntExtra("progress", -1);
                play(currentTime);
            } else if (msg == AppConstantUtil.PlayerMsg.PLAYING_MSG) {
                handler.sendEmptyMessage(1);
            }
            super.onStart(intent, startId);
        }
    }


    private void play(int currentTime) {
        try {
            Log.v("ok--->",path);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            sendNotificationManager();
            mediaPlayer.setOnPreparedListener(new PreparedListener(currentTime));
            Log.v("msgmsgplay","play");
            Intent intent = new Intent();
            intent.setAction("play");
            sendBroadcast(intent);
            handler.sendEmptyMessage(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPause = true;
            Intent intent = new Intent();
            intent.setAction("pause");
            sendBroadcast(intent);
        }
    }

    private void resume() {
        if (isPause) {
            mediaPlayer.start();
            isPause = false;
            Intent intent = new Intent();
            intent.setAction("play");
            sendBroadcast(intent);
        }
    }


    private void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final class PreparedListener implements MediaPlayer.OnPreparedListener {
        private int currentTime;
        public PreparedListener(int currentTime) {
            this.currentTime = currentTime;
        }
        @Override
        public void onPrepared(MediaPlayer mp) {
            mediaPlayer.start();
            if (currentTime > 0) {
                mediaPlayer.seekTo(currentTime);
            }
            Intent intent = new Intent();
            intent.setAction(MUSIC_DURATION);
            duration = mediaPlayer.getDuration();
            intent.putExtra("duration", duration);
            sendBroadcast(intent);
        }
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
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
