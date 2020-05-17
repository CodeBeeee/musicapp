package com.app.musicapp.View.Activity;
import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.musicapp.Adapter.GeDanListBaseAdapter;
import com.app.musicapp.Bean.NetSongBean;
import com.app.musicapp.Bean.SongBean;
import com.app.musicapp.MyApp;
import com.app.musicapp.R;
import com.app.musicapp.Util.AppConstantUtil;
import com.app.musicapp.View.Fragment.LocalMusicFragment;
import com.app.musicapp.View.Fragment.MyMusicFragment;
import com.app.musicapp.View.Fragment.WebMusicFragment;
import com.app.musicapp.service.NetPlayerService;
import com.app.musicapp.service.PlayerService;
import com.bumptech.glide.Glide;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//首页界面
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout playdd;
    private ImageView ablumimg,playpause;
    private TabLayout myTabLayout;//切换标签
    private ViewPager mViewPager;// 显示的内容页面
    private List<String>myTitle; //标签的名字
    private List<Fragment>myFragment; //每个标签对应一个Fragment页面
    private RadiusImageView ablemimg;
    private TextView musictitle,musicartist;
    private PlayerReceiver playerReceiver;
    private boolean isPlaying,isPause;
    private List<NetSongBean.Info> info;
    private ArrayList<SongBean> musicInfos;
    private int playposition = -1;
    private String playfrom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String userName = "";

        initView();
        initViewData();
    }
    //初始化界面数据
    private void initViewData() {
        checkPermission(); //检查权限
    }
    private void setTabFragment() {
        myTitle = new ArrayList<String>();
        myTitle.add("我的音乐");
        myTitle.add("本地音乐");
        myTitle.add("网络音乐");
        myFragment = new ArrayList<>();
        myFragment.add(new MyMusicFragment());
        myFragment.add(new LocalMusicFragment());
        myFragment.add(new WebMusicFragment());
        //预加载
        mViewPager.setOffscreenPageLimit(myFragment.size());
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return myFragment.get(i);
            }
            @Override
            public int getCount() {
                return myFragment.size();
            }
            @Nullable
            @Override
            public CharSequence getPageTitle(int i) {
                return myTitle.get(i);
            }
        });
        //将TabLayout与Viewager绑定
        myTabLayout.setupWithViewPager(mViewPager);
    }
    //组件初始化
    private void initView() {
        myTabLayout = findViewById(R.id.myTab);
        mViewPager = findViewById(R.id.view_pager);
        playdd = findViewById(R.id.playdd);
        ablemimg = findViewById(R.id.ablemimg);
        playpause = findViewById(R.id.playpause);
        musictitle = findViewById(R.id.musictitle);
        musicartist = findViewById(R.id.musicartist);
        musicartist.setText("请前往播放");
        musictitle.setText("");
        playerReceiver = new PlayerReceiver();
        playpause.setOnClickListener(this);
        playdd.setOnClickListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("play");
        filter.addAction("pause");
        filter.addAction("musicinfo");
        registerReceiver(playerReceiver, filter);
    }

    public class PlayerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.v("msgmsgsss1",action);
            if(action.equals("pause")){
                playpause.setBackgroundResource(R.drawable.ic_play_redcircle_outline_black_24dp);
                isPlaying = false;
                isPause = true;
            }else if(action.equals("play")){
                playpause.setBackgroundResource(R.drawable.ic_redpause_circle_outline_black_24dp);
                isPlaying = true;
                isPause = false;
                Log.v("msgmsg","sd");
            }else if(action.equals("musicinfo")){
                info = (List<NetSongBean.Info>) intent.getSerializableExtra("songlist");
                musicInfos = (ArrayList<SongBean>) intent.getSerializableExtra("localsonglist");
                playfrom = intent.getStringExtra("from");
                musictitle.setText(intent.getStringExtra("musictitle"));
                musicartist.setText(intent.getStringExtra("musicartist"));
                playposition = intent.getIntExtra("position",-1);
                if(playfrom.equals("net")) Glide.with(MainActivity.this).load(intent.getStringExtra("musicpath")).into(ablemimg);
            }
        }
    }

    @Override
    public void onClick(View view){
            switch (view.getId()){
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
                                .Builder(MainActivity.this)
                                .setMessage("暂无选取歌曲播放")
                                .setNegativeButton("确定", null)
                                .show();
                    }else{
                        if(playfrom.equals("net")){
                            Intent intent2=new Intent(MainActivity.this,NetPlayActivity.class);
                            intent2.putExtra("position",playposition);
                            intent2.putExtra("songlist",(Serializable) info);
                            startActivity(intent2);
                        }else if(playfrom.equals("local")){
                            Intent intent1=new Intent(MainActivity.this,PlayActivity.class);
                            intent1.putExtra("position",playposition);
                            intent1.putExtra("songlist",(Serializable) musicInfos);
                            startActivity(intent1);
                        }

                    }
                    break;
            }
    }
    public void pause(){
        Intent i1=new Intent();
        Intent i2=new Intent();
        i1.setClass(MainActivity.this, PlayerService.class);
        i2.setClass(MainActivity.this, NetPlayerService.class);
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
        i1.setClass(MainActivity.this, PlayerService.class);
        i2.setClass(MainActivity.this, NetPlayerService.class);
        i1.setAction("com.lzw.media.MUSIC_SERVICE");
        i2.setAction("com.lzw.media.MUSIC_SERVICE");
        i1.putExtra("MSG", AppConstantUtil.PlayerMsg.CONTINUE_MSG);
        i2.putExtra("MSG", AppConstantUtil.PlayerMsg.CONTINUE_MSG);
        startService(i1);
        startService(i2);
        isPause = false;
        isPlaying = true;
    }
    private int times = 0;
    private final int REQUEST_PHONE_PERMISSIONS = 0;
    private void checkPermission(){
        times++;
        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if((checkSelfPermission(Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.INTERNET);
            if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionsList.size() != 0){
                if(times==1){
                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                            REQUEST_PHONE_PERMISSIONS);
                }else{
                    new AlertDialog.Builder(this)
                            .setCancelable(true)
                            .setTitle("提示")
                            .setMessage("获取不到授权，APP将无法正常使用，请允许APP获取权限！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                                REQUEST_PHONE_PERMISSIONS);
                                    }
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    }).show();
                }
            }else{
                setTabFragment();
            }
        }else{
            setTabFragment();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,  final String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermission();
    }
    protected void onResume(){
        super.onResume();
    }
    protected void onStop(){
        super.onStop();
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent i1 = new Intent(this,PlayerService.class);
        Intent i2 = new Intent(this,NetPlayerService.class);
        stopService(i1);
        stopService(i2);

    }
}
