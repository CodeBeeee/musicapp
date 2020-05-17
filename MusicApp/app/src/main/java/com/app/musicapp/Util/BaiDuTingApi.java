package com.app.musicapp.Util;

public class BaiDuTingApi {
    //format 是返回的数据类型, method= 后面添加下面的api方法
    public final static String musicApi="http://tingapi.ting.baidu.com/v1/restserver/ting?format=json&calback=&from=webapp_music&method=";//百度音乐api
    /*method方法
    1.获取列表
     参数：
     type = 1-新歌榜,2-热歌榜,11-摇滚榜,12-爵士,16-流行,21-欧美金曲榜,22-经典老歌榜,23-情歌对唱榜,24-影视金曲榜,25-网络歌曲榜
     size = 10 //返回条目数量
     offset = 0 //获取偏移
     */
    public final static String getlistmethod="baidu.ting.billboard.billList&type=23&size=20&offset=0";
    /*
    * 参数：query = ” //搜索关键字
    * */
    public final static String searchmethod="baidu.ting.search.catalogSug&query=";
    /*
    * 根据songid获取音乐播放信息
    * */
    public final static String getsongapi="http://music.taihe.com/data/music/links?songIds=";
}
