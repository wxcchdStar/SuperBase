package wxc.android.base.demo.model;

import java.util.List;

import wxc.android.base.api.model.ApiResultData;

public class MusicRankingBean implements ApiResultData {

    /**
     * pic_s210 : http://business.cdn.qianqian.com/qianqian/pic/bos_client_734232335ef76f5a05179797875817f3.jpg
     * bg_pic : http://business0.qianqian.com/qianqian/file/5bfe4e9aa7496_218.png
     * color : 0xDC5900
     * pic_s444 : http://hiphotos.qianqian.com/ting/pic/item/c83d70cf3bc79f3d98ca8e36b8a1cd11728b2988.jpg
     * count : 4
     * type : 2
     * content : [{"all_rate":"96,128,224,320,flac","song_id":"606149060","rank_change":"0","biaoshi":"lossless,perm-1","author":"展展与罗罗","album_id":"606149057","pic_small":"http://qukufile2.qianqian.com/data2/pic/c9aa6f85bf036735c355a05dd373ff0b/606149058/606149058.png@s_1,w_90,h_90","title":"沙漠骆驼","pic_big":"http://qukufile2.qianqian.com/data2/pic/c9aa6f85bf036735c355a05dd373ff0b/606149058/606149058.png@s_1,w_150,h_150","album_title":"沙漠骆驼"},{"all_rate":"flac,320,128,224,96","song_id":"601427388","rank_change":"0","biaoshi":"lossless,perm-3","author":"火箭少女101","album_id":"601427384","pic_small":"http://qukufile2.qianqian.com/data2/pic/8d356491f24692ff802cc49c80f51fee/601427385/601427385.jpg@s_1,w_90,h_90","title":"卡路里（电影《西虹市首富》插曲）","pic_big":"http://qukufile2.qianqian.com/data2/pic/8d356491f24692ff802cc49c80f51fee/601427385/601427385.jpg@s_1,w_150,h_150","album_title":"卡路里（电影《西虹市首富》插曲）"},{"all_rate":"96,128,224,320,flac","song_id":"604568155","rank_change":"0","biaoshi":"lossless,vip,perm-1","author":"马良,孙茜茹","album_id":"604568152","pic_small":"http://qukufile2.qianqian.com/data2/pic/f53a667bbf3c11df1da0841fd34c4d9d/604568153/604568153.jpg@s_1,w_90,h_90","title":"往后余生","pic_big":"http://qukufile2.qianqian.com/data2/pic/f53a667bbf3c11df1da0841fd34c4d9d/604568153/604568153.jpg@s_1,w_150,h_150","album_title":"往后余生"},{"all_rate":"96,128,224,320,flac","song_id":"598740690","rank_change":"0","biaoshi":"lossless,perm-3","author":"张杰,张碧晨","album_id":"598740686","pic_small":"http://qukufile2.qianqian.com/data2/pic/2854c6d30aab478cec599a174c911eea/598740687/598740687.jpg@s_1,w_90,h_90","title":"只要平凡","pic_big":"http://qukufile2.qianqian.com/data2/pic/2854c6d30aab478cec599a174c911eea/598740687/598740687.jpg@s_1,w_150,h_150","album_title":"只要平凡"}]
     * bg_color : 0xFBEFE6
     * web_url : 
     * name : 热歌榜
     * comment : 该榜单是根据千千音乐平台歌曲每周播放量自动生成的数据榜单，统计范围为千千音乐平台上的全部歌曲，每日更新一次
     * pic_s192 : http://business.cdn.qianqian.com/qianqian/pic/bos_client_1452f36a8dc430ccdb8f6e57be6df2ee.jpg
     * pic_s260 : http://hiphotos.qianqian.com/ting/pic/item/838ba61ea8d3fd1f1326c83c324e251f95ca5f8c.jpg
     */

    public String pic_s210;
    public String bg_pic;
    public String color;
    public String pic_s444;
    public int count;
    public int type;
    public String bg_color;
    public String web_url;
    public String name;
    public String comment;
    public String pic_s192;
    public String pic_s260;
    public List<ContentBean> content;

    public static class ContentBean {
        /**
         * all_rate : 96,128,224,320,flac
         * song_id : 606149060
         * rank_change : 0
         * biaoshi : lossless,perm-1
         * author : 展展与罗罗
         * album_id : 606149057
         * pic_small : http://qukufile2.qianqian.com/data2/pic/c9aa6f85bf036735c355a05dd373ff0b/606149058/606149058.png@s_1,w_90,h_90
         * title : 沙漠骆驼
         * pic_big : http://qukufile2.qianqian.com/data2/pic/c9aa6f85bf036735c355a05dd373ff0b/606149058/606149058.png@s_1,w_150,h_150
         * album_title : 沙漠骆驼
         */

        public String all_rate;
        public String song_id;
        public String rank_change;
        public String biaoshi;
        public String author;
        public String album_id;
        public String pic_small;
        public String title;
        public String pic_big;
        public String album_title;
    }
}
