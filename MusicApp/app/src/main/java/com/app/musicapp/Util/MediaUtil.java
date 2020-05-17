package com.app.musicapp.Util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import com.app.musicapp.Bean.NetSongBean;
import com.app.musicapp.Bean.PlaySongBean;
import com.app.musicapp.Bean.SongBean;
import com.app.musicapp.R;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/*
* 用来获取所有本地歌曲的MediaUtil.
* */
public class MediaUtil {
    public static List<NetSongBean.Info> list;
    //Android音乐媒体文件是数据库管理的，可以通过源码得到uri为下面默认的连接 ，从数据库获取到我们所有本地的音频
    private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
    //获取网络音乐数据
    public MediaUtil(List<NetSongBean.Info> list) {
        this.list = list;
    }


    //从数据库遍历所有的本地音频文件数据
    public static ArrayList<SongBean> getAllSongs(Context context) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        ArrayList<SongBean> mp3Infos = new ArrayList<SongBean>();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            SongBean mp3Info = new SongBean();
            long id = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID));
            String title = cursor.getString((cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));
            String artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ALBUM));
            String displayName = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            long albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            long duration = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));
            long size = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.SIZE));
            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));
            int isMusic = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
            Log.v("msgmusic", String.valueOf(isMusic));
            if (isMusic != 0) {
                mp3Info.setId(id);
                mp3Info.setTitle(title);
                mp3Info.setArtist(artist);
                mp3Info.setAlbum(album);
                mp3Info.setDisplayName(displayName);
                mp3Info.setAlbumId(albumId);
                mp3Info.setDuration(duration);
                mp3Info.setSize(size);
                mp3Info.setUrl(url);
                mp3Infos.add(mp3Info);
                Log.v("musicmsg", String.valueOf(isMusic));
            }
        }
        Log.v("musicmsg", String.valueOf(mp3Infos));
        return mp3Infos;
    }

    //加工音频数据，得到map返回
    public static List<HashMap<String, String>> getMusicMaps(
            List<SongBean> mp3Infos) {
        List<HashMap<String, String>> mp3list = new ArrayList<HashMap<String, String>>();
        for (Iterator iterator = mp3Infos.iterator(); iterator.hasNext();) {
            SongBean mp3Info = (SongBean) iterator.next();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("title", mp3Info.getTitle());
            map.put("Artist", mp3Info.getArtist());
            map.put("album", mp3Info.getAlbum());
            map.put("displayName", mp3Info.getDisplayName());
            map.put("albumId", String.valueOf(mp3Info.getAlbumId()));
            map.put("duration", formatTime(mp3Info.getDuration()));
            map.put("size", String.valueOf(mp3Info.getSize()));
            map.put("url", mp3Info.getUrl());
            mp3list.add(map);
        }
        return mp3list;
    }
    public static String formatTime(long time) {

        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        } else {
            min = time / (1000 * 60) + "";
        }
        if (sec.length() == 4) {
            sec = "0" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 3) {
            sec = "00" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 2) {
            sec = "000" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 1) {
            sec = "0000" + (time % (1000 * 60)) + "";
        }
        return min + ":" + sec.trim().substring(0, 2);
    }
    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
    //获取专辑图片，若为null就以默认的图片显示
    public static Bitmap getArtwork(Context context, long song_id, long album_id,
                                    boolean allowdefault) {
        if (album_id < 0) {
            if (song_id >= 0) {
                Bitmap bm = getArtworkFromFile(context, song_id, -1);
                if (bm != null) {
                    return bm;
                }
            }
            if (allowdefault) {
                return getDefaultArtwork(context);
            }
            return null;
        }
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        Log.v("msggetimg","1-->>");
        if (uri != null) {
            InputStream in = null;
            try {
                in = res.openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(in, null, sBitmapOptions);
                if (bmp == null) {
                    bmp = getDefaultArtwork(context);
                }
                return bmp;
            } catch (FileNotFoundException ex) {
                Bitmap bm = getArtworkFromFile(context, song_id, album_id);
                if (bm != null) {
                    if (bm.getConfig() == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false);
                        if (bm == null && allowdefault) {
                            return getDefaultArtwork(context);
                        }
                    }
                } else if (allowdefault) {
                    bm = getDefaultArtwork(context);
                }
                Log.v("msggetimg","3-->>");
                return bm;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.v("msggetimg","4-->>");
            }
        }
        Log.v("msggetimg","2-->>null");
        return null;
    }

    private static Bitmap getArtworkFromFile(Context context, long songid, long albumid) {
        Bitmap bm = null;
        if (albumid < 0 && songid < 0) {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }
        try {
            if (albumid < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver()
                        .openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            } else {
                Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);
                ParcelFileDescriptor pfd = context.getContentResolver()
                        .openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            }
        } catch (FileNotFoundException ex) {
            Log.v("msggetimg","6-->>");
        }
        Log.v("msggetimg","5-->>");
        return bm;
    }

    @SuppressLint("ResourceType")
    private static Bitmap getDefaultArtwork(Context context) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            return BitmapFactory.decodeStream(
                    context.getResources().openRawResource(R.drawable.ic_album_black_24dp), null,
                    opts);
    }
}
