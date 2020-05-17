package com.app.musicapp.View.Fragment;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.app.musicapp.Adapter.GeDanListBaseAdapter;
import com.app.musicapp.Adapter.NetMusicListAdapter;
import com.app.musicapp.Bean.NetSongBean;
import com.app.musicapp.MyApp;
import com.app.musicapp.R;
import com.app.musicapp.Util.BaiDuTingApi;
import com.app.musicapp.Util.GsonUtil;
import com.app.musicapp.Util.MediaUtil;
import com.app.musicapp.View.Activity.GeDanListActivity;
import com.app.musicapp.View.Activity.MusicTypeActivity;
import com.app.musicapp.View.Activity.NetPlayActivity;
import com.app.musicapp.View.Activity.PlayActivity;
import com.app.musicapp.db.Gedandb;
import com.app.musicapp.db.Musicdb;
import com.app.musicapp.db.MyLoveMusic;
import com.app.musicapp.gen.GedandbDao;
import com.mt.mtloadingmanager.LoadingManager;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xuexiang.xui.adapter.simple.AdapterItem;
import com.xuexiang.xui.adapter.simple.XUISimpleAdapter;
import com.xuexiang.xui.utils.DensityUtils;
import com.xuexiang.xui.widget.popupwindow.popup.XUIListPopup;
import com.xuexiang.xui.widget.popupwindow.popup.XUIPopup;
import com.xuexiang.xui.widget.popupwindow.popup.XUISimplePopup;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class WebMusicFragment extends BaseFragment implements View.OnClickListener {
    private View rootView;
    private Handler handler;
    private RecyclerView musicrecyclerview;
    private LinearLayout newsmusic,fastmusic,oldmusic;
    private NetMusicListAdapter netMusicListAdapter;
    private NetSongBean netSongBean = new NetSongBean();
    private LoadingManager loadingManager;
    private HttpReceiver httpReceiver;
    private RefreshLayout refreshLayout;
    private boolean flag = false;
    private PopupWindow popupWindow;// 声明PopupWindow
    private View popupView;// 声明PopupWindow对应的视图
    private TranslateAnimation animation;// 声明平移动画
    private AlertDialog dialog;
    private int selectId;
    public WebMusicFragment() {

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView == null){
            rootView = inflater.inflate(R.layout.main_webmusic_layout,container,false);
            initView(rootView);
            initData();
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.newsmusic:
                Intent i1 = new Intent(getActivity(), MusicTypeActivity.class);
                i1.putExtra("type",1);
                startActivity(i1);
                break;
            case R.id.fastmusic:
                Intent i2 = new Intent(getActivity(), MusicTypeActivity.class);
                i2.putExtra("type",1);
                startActivity(i2);
                break;
            case R.id.oldmusic:
                Intent i3 = new Intent(getActivity(), MusicTypeActivity.class);
                i3.putExtra("type",1);
                startActivity(i3);
                break;
        }
    }

    public class HttpReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            Log.v("action",action);
            if(action.equals("okhttp")){
                netMusicListAdapter=new NetMusicListAdapter(netSongBean.getSong_list(),getActivity());
                netMusicListAdapter.setOnMyItemClickListener(new NetMusicListAdapter.OnMyItemClickListener() {
                    @Override
                    public void myClick(View v, int pos) {
                        if(v.getId()==R.id.item){
                            Intent intent2=new Intent(getActivity(),NetPlayActivity.class);
                            intent2.putExtra("position",pos);
                            intent2.putExtra("songlist",(Serializable) netSongBean.getSong_list());
                            startActivity(intent2);
                        }else if(v.getId()==R.id.more){
                            Log.v("msgmsgmsg",pos+" "+netSongBean.getSong_list().get(pos));
                            selectId = pos;
                            changeIcon(v);
                        }
                    }
                });
                musicrecyclerview.setAdapter(netMusicListAdapter);
                musicrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
            }
        }
    }
    private void initData() {
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int ran = (int) (Math.random()*20+5); //[5,20]
                int size = (int) (Math.random()*20+10) ; //
                String m = "baidu.ting.billboard.billList&type="+23+"&size="+size+"&offset="+ran;
                String url = BaiDuTingApi.musicApi+m;
                OkHttpClient okHttpClient=new OkHttpClient();
                Request request=new Request.Builder()
                        .url(url)
                        .removeHeader("User-Agent")
                        .addHeader("User-Agent", new WebView(getActivity()).getSettings().getUserAgentString())
                        .build();
                Call call=okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //Toast.makeText(getActivity(),"网络出错",Toast.LENGTH_LONG).show();
                        flag = false;
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData=response.body().string();
                        netSongBean = GsonUtil.GsonToBean(responseData,NetSongBean.class);
                        Intent intent = new Intent();
                        MediaUtil.list = netSongBean.getSong_list();
                        intent.setAction("okhttp");
                        getActivity().sendBroadcast(intent);
                        flag =true;
                    }
                });
            }
        });
    }
    private void changeIcon(View view){
        Log.v("msgmsgmsgpos",selectId+" ");
        if (popupWindow == null) {
            popupView = View.inflate(view.getContext(), R.layout.musicmore_layout, null);
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
            popupView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("msgmsgmsgpos123",selectId+" ");
                    MyLoveMusic add = new MyLoveMusic();
                    add.setSongid(netSongBean.getSong_list().get(selectId).getSong_id());
                    add.setSongname(netSongBean.getSong_list().get(selectId).getTitle());
                    add.setSongauthor(netSongBean.getSong_list().get(selectId).getAuthor());
                    add.setSongimg(netSongBean.getSong_list().get(selectId).getPic_small());
                    MyApp.getInstances().getDaoSession().getMyLoveMusicDao().insertOrReplace(add);
                    Log.v("msgmsgmsgdata",add.getSongauthor().toString()+" "+add.getSongname().toString());
                    new AlertDialog
                            .Builder(getContext())
                            .setMessage("添加成功")
                            .setNegativeButton("确定", null)
                            .show();
                    popupWindow.dismiss();
                    lighton();
                }
            });
            popupView.findViewById(R.id.select).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点击选择歌单的监听事件
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    final View viewCategory = inflater.inflate(R.layout.gedandialog, null);
                    final RecyclerView recyclerView = viewCategory.findViewById(R.id.recyclerview);
                    final List<Gedandb> mydata = MyApp.getInstances().getDaoSession().getGedandbDao().loadAll();
                    GeDanListBaseAdapter geDanListBaseAdapter = new GeDanListBaseAdapter(mydata,getActivity());
                    recyclerView.setAdapter(geDanListBaseAdapter);
                    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),1));
                    geDanListBaseAdapter.setOnMyItemClickListener(new GeDanListBaseAdapter.OnMyItemClickListener() {
                        @Override
                        public void myClick(View v, int index) {
                            Log.v("msgmsg",index+" "+mydata.size());
                            if(v.getId()==R.id.item){
                                 if(index!=mydata.size()){
                                     Musicdb add = new Musicdb();
                                     add.setGedanid(mydata.get(index).getGedanid());
                                     add.setSongname(netSongBean.getSong_list().get(selectId).getTitle());
                                     add.setPic_small(netSongBean.getSong_list().get(selectId).getPic_small());
                                     add.setSongauthor(netSongBean.getSong_list().get(selectId).getAuthor());
                                     add.setSongid(netSongBean.getSong_list().get(selectId).getSong_id());
                                     MyApp.getInstances().getDaoSession().getMusicdbDao().insertOrReplace(add);

                                     GedandbDao ud = MyApp.getInstances().getDaoSession().getGedandbDao();
                                     QueryBuilder<Gedandb> builder = ud.queryBuilder();
                                     Gedandb data = builder.where(GedandbDao.Properties.Gedanid.eq(mydata.get(index).getGedanid())).unique();
                                     data.setGedanhasnum(mydata.get(index).getGedanhasnum()+1);
                                     MyApp.getInstances().getDaoSession().getGedandbDao().update(data);
                                     Intent intent = new Intent();
                                     intent.setAction("gedanviewchange");
                                     getActivity().sendBroadcast(intent);
                                     dialog.dismiss();
                                     new AlertDialog
                                             .Builder(getContext())
                                             .setMessage("收藏成功")
                                             .setNegativeButton("确定", null)
                                             .show();
                                 }
                            }
                        }
                    });
                    dialog = new AlertDialog
                            .Builder(getContext())
                            .setTitle("选择歌单")
                            .setView(viewCategory)
                            .setNegativeButton("取消", null)
                            .show();

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
        popupWindow.showAtLocation(view , Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popupView.startAnimation(animation);
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
    public class GetSystemMusicDataTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }

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

    private void initView(View rootView) {
        musicrecyclerview = rootView.findViewById(R.id.musicrecyclerview);
        newsmusic = rootView.findViewById(R.id.newsmusic);
        fastmusic = rootView.findViewById(R.id.fastmusic);
        oldmusic = rootView.findViewById(R.id.oldmusic);
        refreshLayout = rootView.findViewById(R.id.refreshLayout);
        newsmusic.setOnClickListener(this);
        fastmusic.setOnClickListener(this);
        oldmusic.setOnClickListener(this);
        loadingManager = new LoadingManager(this.getActivity());
        httpReceiver = new HttpReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("okhttp");
        getActivity().registerReceiver(httpReceiver, filter);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                initData();
                refreshlayout.finishLoadMore(2000);//传入false表示加载失败
            }
        });
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
}
