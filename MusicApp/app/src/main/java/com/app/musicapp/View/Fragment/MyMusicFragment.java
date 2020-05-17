package com.app.musicapp.View.Fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.app.musicapp.Adapter.GeDanListBaseAdapter;
import com.app.musicapp.Adapter.NetMusicListAdapter;
import com.app.musicapp.Bean.GeDanBean;
import com.app.musicapp.MyApp;
import com.app.musicapp.R;
import com.app.musicapp.View.Activity.GeDanListActivity;
import com.app.musicapp.View.Activity.GeDanManager;
import com.app.musicapp.View.Activity.MusicTypeActivity;
import com.app.musicapp.View.Activity.NetPlayActivity;
import com.app.musicapp.View.Activity.UserInfoActivity;
import com.app.musicapp.db.Gedandb;
import com.app.musicapp.db.Musicdb;
import com.app.musicapp.db.Userdb;
import com.app.musicapp.gen.GedandbDao;
import com.app.musicapp.gen.MusicdbDao;
import com.bumptech.glide.Glide;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyMusicFragment extends BaseFragment implements View.OnClickListener {
    private View rootView;
    private ArrayList list_path; //轮播图片
    private ArrayList list_title; //对应图片的标题
    private RecyclerView music_gedan_recyclerview; //歌单列表
    private GeDanListBaseAdapter geDanListBaseAdapter = null; //歌单的适配器
    private ImageView more;
    private TextView nonetx;
    private PopupWindow popupWindow;// 声明PopupWindow
    private View popupView;// 声明PopupWindow对应的视图
    private TranslateAnimation animation;// 声明平移动画
    private static int qiehuan = 1;
    private Handler handler;
    List<Gedandb> mydata;
    private HttpReceiver httpReceiver;
    private LinearLayout mylovemusic,myshouc,zuijing;
    public MyMusicFragment() {

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView == null){
            rootView = inflater.inflate(R.layout.main_mymusic_layout,container,false);
            initView(rootView);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }
    private void initView(View rootView) {
        handler = new Handler();
        music_gedan_recyclerview = rootView.findViewById(R.id.music_gedan_recyclerview);
        more = rootView.findViewById(R.id.more);
        nonetx = rootView.findViewById(R.id.nonetx);
        mylovemusic = rootView.findViewById(R.id.mylovemusic);
        myshouc = rootView.findViewById(R.id.myshouc);
        zuijing = rootView.findViewById(R.id.zuijing);
        more.setOnClickListener(this);
        myshouc.setOnClickListener(this);
        mylovemusic.setOnClickListener(this);
        zuijing.setOnClickListener(this);
        mydata = new ArrayList<>();
        httpReceiver = new HttpReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("gedanviewchange");
        getActivity().registerReceiver(httpReceiver, filter);
        //初始化歌单信息
        setGeDanInfo();
    }
    private void setGeDanInfo() {
        mydata = MyApp.getInstances().getDaoSession().getGedandbDao().loadAll();
        geDanListBaseAdapter = new GeDanListBaseAdapter(mydata,getActivity());
        music_gedan_recyclerview.setAdapter(geDanListBaseAdapter);
        music_gedan_recyclerview.setLayoutManager(new GridLayoutManager(getActivity(),qiehuan));
        geDanListBaseAdapter.setOnMyItemClickListener(new GeDanListBaseAdapter.OnMyItemClickListener() {
            @Override
            public void myClick(View v, int pos) {
                if(v.getId()==R.id.item){
                    Log.v("msgmsg",pos+" "+mydata.size());
                    if(pos!=mydata.size()){
                        Intent i = new Intent(getActivity(), GeDanListActivity.class);
                        i.putExtra("gedanid",mydata.get(pos).getGedanid());
                        i.putExtra("gedanname",mydata.get(pos).getGedanname());
                        i.putExtra("gedannum",mydata.get(pos).getGedanhasnum());
                        startActivity(i);
                    }else{
                        creategd();
                    }

                }
            }
        });
    }
    public class HttpReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            if(action.equals("gedanviewchange")){
                  setGeDanInfo();
            }
        }
    }
    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if(isVisible){
            //模拟加载其他的数据
            //setNewsData();
        }else{

        }
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        //setNewsData();
    }
    //监听按钮点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.more:
                // TODO 弹出popupwind选择管理歌单
                changeIcon(view);
                lightoff();
                break;
            case R.id.mylovemusic:
                Intent i1= new Intent(getActivity(), MusicTypeActivity.class);
                i1.putExtra("type",4);
                startActivity(i1);
                break;
            case R.id.myshouc:
                Intent i2= new Intent(getActivity(), MusicTypeActivity.class);
                i2.putExtra("type",5);
                startActivity(i2);
                break;
            case R.id.zuijing:
                Intent i3= new Intent(getActivity(), MusicTypeActivity.class);
                i3.putExtra("type",6);
                startActivity(i3);
                break;
        }
    }
    /**
     * 弹出popupWindow更改头像
     */
    private void changeIcon(View view) {
        if (popupWindow == null) {
            popupView = View.inflate(view.getContext(), R.layout.popupgedan_item, null);
            // 参数2,3：指明popupwindow的宽度和高度
            popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    lighton();
                }
            });
            // 设置背景图片， 必须设置，不然动画没作用
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setFocusable(true);
            // 设置点击popupwindow外屏幕其它地方消失
            popupWindow.setOutsideTouchable(true);
            // 平移动画相对于手机屏幕的底部开始，X轴不变，Y轴从1变0
            animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0,
                    Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_PARENT, 0);
            animation.setInterpolator(new AccelerateInterpolator());
            animation.setDuration(200);
            popupView.findViewById(R.id.creategedan).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点击创建歌单的监听事件
                    creategd();
                    popupWindow.dismiss();
                    lighton();
                }
            });
            popupView.findViewById(R.id.gedanmanager).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点击歌单管理的监听事件
                    Intent intent = new Intent(getActivity(), GeDanManager.class);
                    startActivity(intent);
                    popupWindow.dismiss();
                    lighton();
                }
            });
            popupView.findViewById(R.id.qiehuangedan).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点击切换排列模式监听事件
                    if(qiehuan==1){
                        qiehuan = 2;
                    }else{
                        qiehuan = 1;
                    }
                    setGeDanInfo();
                    popupWindow.dismiss();
                    lighton();
                }
            });
        }

        // 在点击之后设置popupwindow的销毁
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
            lighton();
        }
        // 设置popupWindow的显示位置，此处是在手机屏幕底部且水平居中的位置
        popupWindow.showAtLocation(view ,Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popupView.startAnimation(animation);
    }
    //创建歌单
    private void creategd() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View viewCategory = inflater.inflate(R.layout.gedanadd_layout, null);
        final EditText gedaned = viewCategory.findViewById(R.id.ed);
        new AlertDialog
                .Builder(getContext())
                .setTitle("新建歌单")
                .setView(viewCategory)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Gedandb data = new Gedandb();
                        data.setGedanhasnum(0);
                        data.setGedanname(gedaned.getText().toString());
                        MyApp.getInstances().getDaoSession().insertOrReplace(data);
                        setGeDanInfo();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 设置手机屏幕亮度变暗
     */
    private void lightoff() {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.3f;
        getActivity().getWindow().setAttributes(lp);
    }

    /**
     * 设置手机屏幕亮度显示正常
     */
    private void lighton() {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 1f;
        getActivity().getWindow().setAttributes(lp);
    }
    //刷新列表视图
    Runnable updateview = new Runnable() {
        @Override
        public void run() {
            mydata = MyApp.getInstances().getDaoSession().getGedandbDao().loadAll();
            Log.v("msgmsgdata",mydata.size()+" "+mydata.get(0).toString());
            geDanListBaseAdapter = new GeDanListBaseAdapter(mydata,getActivity());
            music_gedan_recyclerview.setAdapter(geDanListBaseAdapter);
            music_gedan_recyclerview.setLayoutManager(new GridLayoutManager(getActivity(),qiehuan));
            music_gedan_recyclerview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    creategd();
                }
            });
            geDanListBaseAdapter.notifyDataSetChanged();
        }
    };
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
