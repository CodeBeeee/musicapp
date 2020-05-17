package com.app.musicapp.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class Musicdb {
    @Id(autoincrement = true)
    private Long id;
    private Long songid;
    private String pic_small;
    private String songname;
    private String songauthor;
    private Long gedanid;
    @Generated(hash = 991043955)
    public Musicdb() {
    }
    @Generated(hash = 433842945)
    public Musicdb(Long id, Long songid, String pic_small, String songname, String songauthor,
            Long gedanid) {
        this.id = id;
        this.songid = songid;
        this.pic_small = pic_small;
        this.songname = songname;
        this.songauthor = songauthor;
        this.gedanid = gedanid;
    }
    public Long getSongid() {
        return songid;
    }

    public void setSongid(Long songid) {
        this.songid = songid;
    }

    public String getPic_small() {
        return pic_small;
    }

    public void setPic_small(String pic_small) {
        this.pic_small = pic_small;
    }

    public String getSongname() {
        return songname;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }

    public String getSongauthor() {
        return songauthor;
    }

    public void setSongauthor(String songauthor) {
        this.songauthor = songauthor;
    }

    public Long getGedanid() {
        return gedanid;
    }

    public void setGedanid(Long gedanid) {
        this.gedanid = gedanid;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
}
