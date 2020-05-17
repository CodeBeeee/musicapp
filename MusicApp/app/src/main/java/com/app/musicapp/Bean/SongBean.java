package com.app.musicapp.Bean;

import java.io.Serializable;

public class SongBean implements Serializable {
    private Long id;
    private String title; //歌名
    private String artist; //歌手
    private String album; //专辑
    private String displayName; //显示的文件名
    private Long albumId;
    private Long duration; //时长
    private Long size; //文件大小
    private String url; // 路径
    private int isMusic; //是否音乐
    public  SongBean(){}

    public SongBean(Long id, String title, String artist, String album, String displayName, Long albumId, Long duration, Long size, String url, int isMusic) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.displayName = displayName;
        this.albumId = albumId;
        this.duration = duration;
        this.size = size;
        this.url = url;
        this.isMusic = isMusic;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIsMusic() {
        return isMusic;
    }

    public void setIsMusic(int isMusic) {
        this.isMusic = isMusic;
    }

    @Override
    public String toString() {
        return "SongBean{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", displayName='" + displayName + '\'' +
                ", albumId=" + albumId +
                ", duration=" + duration +
                ", size=" + size +
                ", url='" + url + '\'' +
                ", isMusic=" + isMusic +
                '}';
    }
}
