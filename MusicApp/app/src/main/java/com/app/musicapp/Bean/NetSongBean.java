package com.app.musicapp.Bean;

import java.io.Serializable;
import java.util.List;

public class NetSongBean implements Serializable {
     public List<Info> song_list;
     public static class Info implements Serializable{
         private Long song_id;
         private String title;
         private String author;
         private String pic_small;
         private String file_duration;
         public Info(){}
         public Info(Long song_id, String title, String author, String pic_small, String file_duration) {
             this.song_id = song_id;
             this.title = title;
             this.author = author;
             this.pic_small = pic_small;
             this.file_duration = file_duration;
         }

         public Long getSong_id() {
             return song_id;
         }

         public void setSong_id(Long song_id) {
             this.song_id = song_id;
         }

         public String getTitle() {
             return title;
         }

         public void setTitle(String title) {
             this.title = title;
         }

         public String getAuthor() {
             return author;
         }

         public void setAuthor(String author) {
             this.author = author;
         }

         public String getPic_small() {
             return pic_small;
         }

         public void setPic_small(String pic_small) {
             this.pic_small = pic_small;
         }

         public String getFile_duration() {
             return file_duration;
         }

         public void setFile_duration(String file_duration) {
             this.file_duration = file_duration;
         }

         @Override
         public String toString() {
             return "Info{" +
                     "song_id=" + song_id +
                     ", title='" + title + '\'' +
                     ", author='" + author + '\'' +
                     ", pic_small='" + pic_small + '\'' +
                     ", file_duration='" + file_duration + '\'' +
                     '}';
         }
     }
     public NetSongBean(){}

    public NetSongBean(List<Info> song_list) {
        this.song_list = song_list;
    }

    public List<Info> getSong_list() {
        return song_list;
    }

    public void setSong_list(List<Info> song_list) {
        this.song_list = song_list;
    }

    @Override
    public String toString() {
        return "NetSongBean{" +
                "song_list=" + song_list +
                '}';
    }
}
