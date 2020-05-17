package com.app.musicapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.app.musicapp.gen.DaoMaster;
import com.app.musicapp.gen.DaoSession;
import com.xuexiang.xui.XUI;


public class MyApp extends Application {
    private int times = 0;
    private final int REQUEST_PHONE_PERMISSIONS = 0;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    public static MyApp instances;
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        XUI.init(this); //初始化UI框架
        XUI.debug(true);  //开启UI框架调试日志
        instances = this;
        setDatabase();
    }
    public static MyApp getInstances(){
        return instances;
    }
    /**
     * 设置greenDao
     */
    private void setDatabase() {
        mHelper = new DaoMaster.DevOpenHelper(this, "leidong.db", null);
        db = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }
    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}

