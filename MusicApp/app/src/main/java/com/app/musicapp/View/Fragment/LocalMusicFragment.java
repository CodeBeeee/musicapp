package com.app.musicapp.View.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.musicapp.Adapter.MusicListAdapter;
import com.app.musicapp.Bean.SongBean;
import com.app.musicapp.R;
import com.app.musicapp.Util.MediaUtil;
import com.app.musicapp.View.Activity.PlayActivity;
import com.bumptech.glide.Glide;
import com.mt.mtloadingmanager.LoadingDialog;
import com.mt.mtloadingmanager.LoadingManager;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import static com.xuexiang.xui.XUI.getContext;

public class LocalMusicFragment extends BaseFragment implements OnBannerListener {
    private View rootView;
    private Banner banner;
    private List list_img;
    private List list_title;
    private ArrayList<SongBean> arrayList;
    private MusicListAdapter musicListAdapter; //音乐的列表适配器
    private RecyclerView musicrecyclerview;
    private LoadingManager loadingManager;
    public LocalMusicFragment() {
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView == null){
            rootView = inflater.inflate(R.layout.main_localmusic_layout,container,false);
            initView(rootView);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    private void initView(View rootView) {
        banner = rootView.findViewById(R.id.banner);
        musicrecyclerview = rootView.findViewById(R.id.musicrecyclerview);
        loadingManager = new LoadingManager(getContext());
        setBanner();
        musicrecyclerview.setHasFixedSize(true);
        new GetSystemMusicDataTask().execute();
    }

    private void setBanner() {
        //放图片地址的集合
        list_img = new ArrayList<>();
        //放标题的集合
        list_title = new ArrayList<>();
        list_img.add(R.drawable.m1);
        list_img.add(R.drawable.m2);
        list_img.add(R.drawable.m3);
        //设置内置样式，共有六种可以点入方法内逐一体验使用。
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器，图片加载器在下方
        banner.setImageLoader(new MyLoader());
        //设置图片网址或地址的集合
        banner.setImages(list_img);
        //设置轮播的动画效果，内含多种特效，可点入方法内查找后内逐一体验
        banner.setBannerAnimation(Transformer.Default);
        //设置轮播间隔时间
        banner.setDelayTime(3000);
        //设置是否为自动轮播，默认是“是”。
        banner.isAutoPlay(true);
        //设置不能手动影响 默认是手指触摸 轮播图不能翻页
        banner.setViewPagerIsScroll(false);
        //设置指示器的位置，小点点，左中右。
        banner.setIndicatorGravity(BannerConfig.CENTER)
//以上内容都可写成链式布局，这是轮播图的监听。比较重要。方法在下面。
                .setOnBannerListener(this)
//必须最后调用的方法，启动轮播图。
                .start();
    }
    //自定义的图片加载器
    private class MyLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context).load(path).into(imageView);
        }
    }

    public class GetSystemMusicDataTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            arrayList= MediaUtil.getAllSongs(getContext());
            return null;
        }

        @Override
        protected void onPreExecute() {
            arrayList=new ArrayList<SongBean>();
            loadingManager.show("正在加载中...");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loadingManager.hide(null);
            musicListAdapter=new MusicListAdapter(arrayList);
            musicListAdapter.setOnMyItemClickListener(new MusicListAdapter.OnMyItemClickListener() {
                @Override
                public void myClick(View v, int pos) {
                    if(v.getId()==R.id.item){
                        Intent intent2=new Intent(getContext(),PlayActivity.class);
                        intent2.putExtra("position",pos);
                        startActivity(intent2);
                    }else if(v.getId()==R.id.more){
                        Toast.makeText(getActivity(),"asd",Toast.LENGTH_LONG).show();
                    }
                }
            });
            musicrecyclerview.setAdapter(musicListAdapter);
            musicrecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
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

    @Override
    public void OnBannerClick(int position) {

    }
}
