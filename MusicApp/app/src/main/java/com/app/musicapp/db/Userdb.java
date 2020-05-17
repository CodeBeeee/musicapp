package com.app.musicapp.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class Userdb {
    @Id(autoincrement = true)
    private Long id;
    private String name;
    private int age;
    @Generated(hash = 584098046)
    public Userdb() {
    }
    @Generated(hash = 1204778914)
    public Userdb(Long id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
    public int getAge() {

        return this.age;
    }

    public void setAge(int age) {

        this.age = age;
    }

    public String getName() {

        return this.name;
    }
    public void setName(String name) {

        this.name = name;
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }

}
