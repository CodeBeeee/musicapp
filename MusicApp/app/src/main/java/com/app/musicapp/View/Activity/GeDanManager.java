package com.app.musicapp.View.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.app.musicapp.Adapter.GeDanListBaseAdapter;
import com.app.musicapp.Adapter.GeDanListManagerAdapter;
import com.app.musicapp.Adapter.GeDanMusicAdapter;
import com.app.musicapp.MyApp;
import com.app.musicapp.R;
import com.app.musicapp.db.Gedandb;
import com.app.musicapp.db.Musicdb;
import com.app.musicapp.gen.GedandbDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;
import java.util.Map;

public class GeDanManager extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private GeDanListManagerAdapter geDanMusicAdapter;
    private Button delete;
    private List<Gedandb> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ge_dan_manager);
        recyclerView = findViewById(R.id.recyclerview);
        delete = findViewById(R.id.delete);
        delete.setOnClickListener(this);
        initData();
    }

    private void initData() {
        list = MyApp.getInstances().getDaoSession().getGedandbDao().loadAll();
        geDanMusicAdapter = new GeDanListManagerAdapter(list,GeDanManager.this);
        recyclerView.setAdapter(geDanMusicAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(GeDanManager.this));
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
                .Builder(GeDanManager.this)
                .setTitle("提示")
                .setMessage("是否删除选择歌曲？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos) {
                        Map<Integer,Boolean> checkstaus = geDanMusicAdapter.getCheckstaus();
                        for(int i=0;i<checkstaus.size();i++){
                            if(checkstaus.get(i)==true){
                                Gedandb deldata= list.get(i);
                                MyApp.getInstances().getDaoSession().getGedandbDao().delete(deldata);
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
