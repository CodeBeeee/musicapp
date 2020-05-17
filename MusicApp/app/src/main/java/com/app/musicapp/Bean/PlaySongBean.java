package com.app.musicapp.Bean;

import java.io.Serializable;
import java.util.List;

public class PlaySongBean implements Serializable{
    public String errorCode;
    public D data;
    public PlaySongBean(){}
    public static class D{
        public String xcode;
        public List<Info> songList;
        public static class Info implements Serializable {
            private Long song_id;
            private String songName;
            private String artistName;
            private String songPicSmall;
            private Long time;
            private String lrcLink;
            private String songLink;

            public Info(Long song_id, String songName, String artistName, String songPicSmall, Long time, String lrcLink, String songLink) {
                this.song_id = song_id;
                this.songName = songName;
                this.artistName = artistName;
                this.songPicSmall = songPicSmall;
                this.time = time;
                this.lrcLink = lrcLink;
                this.songLink = songLink;
            }

            public Long getSong_id() {
                return song_id;
            }

            public void setSong_id(Long song_id) {
                this.song_id = song_id;
            }

            public String getSongName() {
                return songName;
            }

            public void setSongName(String songName) {
                this.songName = songName;
            }

            public String getArtistName() {
                return artistName;
            }

            public void setArtistName(String artistName) {
                this.artistName = artistName;
            }

            public String getSongPicSmall() {
                return songPicSmall;
            }

            public void setSongPicSmall(String songPicSmall) {
                this.songPicSmall = songPicSmall;
            }

            public Long getTime() {
                return time;
            }

            public void setTime(Long time) {
                this.time = time;
            }

            public String getLrcLink() {
                return lrcLink;
            }

            public void setLrcLink(String lrcLink) {
                this.lrcLink = lrcLink;
            }

            public String getSongLink() {
                return songLink;
            }

            public void setSongLink(String songLink) {
                this.songLink = songLink;
            }

            @Override
            public String toString() {
                return "Info{" +
                        "song_id=" + song_id +
                        ", songName='" + songName + '\'' +
                        ", artistName='" + artistName + '\'' +
                        ", songPicSmall='" + songPicSmall + '\'' +
                        ", time=" + time +
                        ", lrcLink='" + lrcLink + '\'' +
                        ", songLink='" + songLink + '\'' +
                        '}';
            }
        }
    }





}
