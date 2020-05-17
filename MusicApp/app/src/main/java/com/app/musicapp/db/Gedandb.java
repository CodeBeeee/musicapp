package com.app.musicapp.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
@Entity
public class Gedandb {
    @Id(autoincrement = true)
    private Long gedanid;
    private String gedanname;
    private int gedanhasnum;
    @Generated(hash = 1679245915)
    public Gedandb() {
    }
    @Generated(hash = 752885119)
    public Gedandb(Long gedanid, String gedanname, int gedanhasnum) {
        this.gedanid = gedanid;
        this.gedanname = gedanname;
        this.gedanhasnum = gedanhasnum;
    }
    public Long getGedanid() {
        return gedanid;
    }

    public void setGedanid(Long gedanid) {
        this.gedanid = gedanid;
    }

    public String getGedanname() {
        return gedanname;
    }

    public void setGedanname(String gedanname) {
        this.gedanname = gedanname;
    }

    public int getGedanhasnum() {
        return gedanhasnum;
    }

    public void setGedanhasnum(int gedanhasnum) {
        this.gedanhasnum = gedanhasnum;
    }
}
