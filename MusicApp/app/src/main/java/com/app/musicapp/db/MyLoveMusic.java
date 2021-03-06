package com.app.musicapp.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class MyLoveMusic {
    private Long songid;
    private String songname;
    private String songimg;
    private String songauthor;
    @Generated(hash = 219016785)
    public MyLoveMusic() {
    }
    @Generated(hash = 304020864)
    public MyLoveMusic(Long songid, String songname, String songimg, String songauthor) {
        this.songid = songid;
        this.songname = songname;
        this.songimg = songimg;
        this.songauthor = songauthor;
    }
    public Long getSongid() {
        return songid;
    }

    public void setSongid(Long songid) {
        this.songid = songid;
    }

    public String getSongname() {
        return songname;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }

    public String getSongimg() {
        return songimg;
    }

    public void setSongimg(String songimg) {
        this.songimg = songimg;
    }

    public String getSongauthor() {
        return songauthor;
    }

    public void setSongauthor(String songauthor) {
        this.songauthor = songauthor;
    }
}
