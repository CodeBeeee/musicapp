package com.app.musicapp.Bean;

import android.graphics.Bitmap;

public class GeDanBean {
    private String gedanname; //歌单的名字
    private Integer musicnum; //歌单的收藏数量
    private int gedanimg;//歌单的图片
    public GeDanBean(){}

    public GeDanBean(String gedanname, Integer musicnum, int gedanimg) {
        this.gedanname = gedanname;
        this.musicnum = musicnum;
        this.gedanimg = gedanimg;
    }

    public String getGedanname() {
        return gedanname;
    }

    public void setGedanname(String gedanname) {
        this.gedanname = gedanname;
    }

    public Integer getMusicnum() {
        return musicnum;
    }

    public void setMusicnum(Integer musicnum) {
        this.musicnum = musicnum;
    }

    public int getGedanimg() {
        return gedanimg;
    }

    public void setGedanimg(int gedanimg) {
        this.gedanimg = gedanimg;
    }

    @Override
    public String toString() {
        return "GeDanBean{" +
                "gedanname='" + gedanname + '\'' +
                ", musicnum=" + musicnum +
                ", gedanimg=" + gedanimg +
                '}';
    }
}