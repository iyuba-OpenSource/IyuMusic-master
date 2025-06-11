package com.iyuba.music.data;

import com.iyuba.music.R;
import com.iyuba.music.manager.RuntimeManager;

public class Constant {
    public static String IYUBA_CN = "iyuba.cn/";
    public static String IYUBA_COM = "iyuba.com.cn/";


    public static String DOMAIN = "iyuba.cn";
    public static String URL_DEV = "http://dev." + DOMAIN;


    //广告
    public static final String AD_ADS1 = "ads1";//倍孜
    public static final String AD_ADS2 = "ads2";//创见
    public static final String AD_ADS3 = "ads3";//头条穿山甲
    public static final String AD_ADS4 = "ads4";//广点通优量汇
    public static final String AD_ADS5 = "ads5";//快手

//        public static String APP_NAME_PRIVACY = "用户隐私协议";
    public static String APP_NAME_PRIVACY = "隐私政策";
    public static boolean APP_TENCENT_PRIVACY = true;
    public static boolean APP_HUAWEI_PRIVACY = true;
    public static int APP_TYPE_PRIVACY = 0;
    public static int APP_SHARE_WXMINIPROGRAM = 1;
    public static String WX_KEY = "wx0b03034a2b765b42";
    public static String WX_NAME = "gh_ce4ab26820ab";
    public static boolean devMode = false;
    //评测类型
    public static String EVAL_TYPE = "music";
    public static final String HTTP_SPEECH = "http://iuserspeech.iyuba.cn:9001/";

    public static String EVAL_PREFIX = "http://userspeech." + IYUBA_CN + "voa/"; //评测返回，排行榜，评论,口语圈等语音前缀
    public static String EVALUATE_URL = HTTP_SPEECH + "test/eval/"; //语音评测
    public static String MERGE_URL = HTTP_SPEECH + "test/merge/"; //语音合成

    public static String EVALUATE_URL_NEW = "http://userspeech.iyuba.cn/test/concept/"; //语音评测

    public static String APP_ICON = "http://app." + Constant.IYUBA_CN + "android/images/newconcept/newconcept.png";

    public static String APPID = "209";// 爱语吧id
    public static int APP_ID = 209;// 爱语吧id
    public static String mWeiXinKey = "wx6f3650b6c6690eaa"; //微信支付 与分享暂时不同

    //PDF相关
    public static String PDF_PREFIX = "http://apps." + IYUBA_CN + "iyuba";


    //	public static String envir = ConfigManager.Instance().loadString("envir",
//			RuntimeManager.getContext().getExternalFilesDir(null).toString() + "/");// 文件夹路径
    public final static String envir = RuntimeManager.getContext().getExternalFilesDir(null).getAbsolutePath() + "/music/";//文件夹路径
    public static String APP_Name = "听歌学英语";// 应用名称
    public static String AppName = "class.voa";// 爱语吧承认的英文缩写

    //public static String APPID = "224";
    public static String appfile = "newconcept";// 更新时的前缀名
    public static String append = ".mp3";// 文件append
    public static String videoAddr = envir + "/audio/";// 音频文件存储位置
    public static String picSrcAddr = envir + "/pic/";// 音频文件存储位置
    private static String simRecordAddr = envir + "/audio/sound";
    private static String recordTag = ".amr";// 录音（跟读所用）的位置
//	public static String picAddr = RuntimeManager.getContext()
//			.getExternalCacheDir().getAbsolutePath();// imagedownloader默认缓存图片位置

    public static String recordAddr = envir + "/sound.amr";// 跟读音频
    public static String voiceCommentAddr = envir + "/voicecomment.amr";// 语音评论
    public static String screenShotAddr = envir + "/screenshot.jpg";// 截图位置
    public static int price = 900;// 应用内终身价格

    /**
     * 上传能力测评使用的url
     */
    public static final String url_updateExamRecord = "http://daxue." + Constant.IYUBA_CN + "ecollege/updateExamRecord.jsp";

    //以下为智能能力测试所用常量
    public static String[] ABILITY_TYPE_ARR = {"写作", "单词", "语法", "听力", "口语", "阅读"};
    /**
     * 写作能力测试代码
     */
    public static final int ABILITY_TETYPE_WRITE = 0;
    public static final String ABILITY_WRITE = "X";
    public static final String[] WRITE_ABILITY_ARR = {"写作表达", "写作结构", "写作逻辑", "写作素材"};

    /**
     * 单词测试代码
     */
    public static final int ABILITY_TETYPE_WORD = 1;
    public static final String ABILITY_WORD = "W";
    public static final String[] WORD_ABILITY_ARR = {"中英力", "英中力", "发音力", "音义力", "拼写力", "应用力"};


    /**
     * 语法能力测试代码
     */
    public static final int ABILITY_TETYPE_GRAMMER = 2;
    public static final String ABILITY_GRAMMER = "G";
    public static final String[] GRAM_ABILITY_ARR = {"名词", "代词", "形容词副词", "动词", "时态", "句子"};

    /**
     * 听力能力测试代码
     */
    public static final int ABILITY_TETYPE_LISTEN = 3;
    public static final String ABILITY_LISTEN = "L";
    public static final String[] LIS_ABILITY_ARR = {"准确辨音", "听能逻辑", "音义匹配", "听写"};

    /**
     * 口语能力测试代码
     */
    public static final int ABILITY_TETYPE_SPEAK = 4;
    public static final String ABILITY_SPEAK = "S";
    public static final String[] SPEAK_ABILITY_ARR = {"发音", "表达", "素材", "逻辑"};

    /**
     * 阅读能力测试代码
     */
    public static final int ABILITY_TETYPE_READ = 5;
    public static final String ABILITY_READ = "R";
    public static final String[] READ_ABILITY_ARR = {"词汇认知", "句法理解", "语义和逻辑", "语篇"};


    /**
     * 单选能力测试
     */
    public static final int ABILITY_TESTTYPE_SINGLE = 1;
    /**
     * 填空题
     */
    public static final int ABILITY_TESTTYPE_BLANK = 2;
    /**
     * 选择填空
     */
    public static final int ABILITY_TESTTYPE_BLANK_CHOSE = 3;
    /**
     * 图片选择
     */
    public static final int ABILITY_TESTTYPE_CHOSE_PIC = 4;

    /**
     * 语音评测
     */
    public static final int ABILITY_TESTTYPE_VOICE = 5;
    /**
     * 多选
     */
    public static final int ABILITY_TESTTYPE_MULTY = 6;
    /**
     * 判断题目
     */
    public static final int ABILITY_TESTTYPE_JUDGE = 7;
    /**
     * 单词拼写
     */
    public static final int ABILITY_TESTTYPE_BLANK_WORD = 8;

    /**
     * 新概念能力测试 听力url前缀  http://static2." + Constant.IYUBA_CN + "IELTS/sounds/16819.mp3
     */
    public static final String ABILITY_AUDIO_URL_PRE = "http://static2." + Constant.IYUBA_CN + "NewConcept1/sounds/";
    /**
     * 新概念能力测试 附件url前缀 http://static2." + Constant.IYUBA_CN + "IELTS/attach/9081.txt
     */
    public static final String ABILITY_ATTACH_URL_PRE = "http://static2." + Constant.IYUBA_CN + "NewConcept1/attach/";
    /**
     * 新概念能力测试 图片url前缀  http://static2." + Constant.IYUBA_CN + "IELTS/images/
     */
    public static final String ABILITY_IMAGE_URL_PRE = "http://static2." + Constant.IYUBA_CN + "NewConcept1/images/";

    //发短信
    public final static String SMSAPPID = "1e93e2e17fe3e";
    public final static String SMSAPPSECRET = "20a214f48119fbbc88bbf729590618c0";

    public final static String SMSAPPID2 = "1c64b827590ac";
    public final static String SMSAPPSECRET2 = "6bd3183af2d993d296d23a28ef7aeb13";

    public static int recordId;// 学习记录篇目id，用于主程序
    public static String recordStart;// 学习记录开始时间，用于主程序

    public static int normalColor = 0xff414141;
    public final static int readColor = 0xff2983c1;
    public final static int unreadCnColor = 0xff8A8A8A;
    public final static int selectColor = 0xffde5e5b;
    public final static int unselectColor = 0xff444444;
    public final static int optionItemSelect = 0x7fbdfaf1;
    public final static int optionItemUnselect = 0xff8ab6da;

    public static int textColor = 0xff2983c1;
    public static int textSize = 16;

    public static int mode;// 播放模式
    public static int type;// 听歌播放模式
    public static int download;// 是否下载

    public static final String PROTOCOL_URL_IYUBA = "http://iuserspeech.iyuba.cn:9001/api/protocol.jsp?apptype=";
        public static final String PROTOCOL_VIVO_PRIVACY = "http://iuserspeech.iyuba.cn:9001/api/protocolpri.jsp?company=1&apptype=";
//    public static final String PROTOCOL_VIVO_PRIVACY = "http://www.bbe.net.cn/protocolpri.jsp?apptype=听歌学英语&company=山东爱语吧信息科技有限公司";
    public static final String PROTOCOL_VIVO_USAGE = "http://iuserspeech.iyuba.cn:9001/api/protocoluse666.jsp?company=1&apptype=";
    public static String appUpdateUrl = "http://api." + Constant.IYUBA_CN + "mobile/android/newconcept/islatest.plain?currver=";// 升级地址
    public static String feedBackUrl = "http://api." + Constant.IYUBA_CN + "mobile/android/newconcept/feedback.plain?uid=";// 反馈
    public final static String sound = "http://static2." + Constant.IYUBA_CN + "newconcept/";
    public final static String sound_vip = "http://staticvip2." + Constant.IYUBA_CN + "newconcept/";
    public final static String wordUrl = "http://word." + Constant.IYUBA_CN + "words/apiWord.jsp?q=";

    public final static String addCreditsUrl = "http://api." + Constant.IYUBA_CN + "credits/updateScore.jsp?";


    //爱语微课所用

    //移动课堂所需的相关API
    public final static String MOB_CLASS_DOWNLOAD_PATH = "http://static3." + Constant.IYUBA_CN + "resource/";
    public final static String MOB_CLASS_PAYEDRECORD_PATH = "http://app." + Constant.IYUBA_CN + "pay/apiGetPayRecord.jsp?";
    public final static String MOB_CLASS_PACK_IMAGE = "http://static3." + Constant.IYUBA_CN + "resource/packIcon/";
    public final static String MOB_CLASS_PACK_TYPE_IMAGE = "http://static3." + Constant.IYUBA_CN + "resource/nmicon/";

    public final static String MOB_CLASS_COURSE_IMAGE = "http://static3." + Constant.IYUBA_CN + "resource/";

    public final static String reqPackDesc = "class.jichu";
    public final static int IO_BUFFER_SIZE = 100 * 1024;

    public final static String PIC_BASE_URL = "http://app." + Constant.IYUBA_CN + "dev/";

    public final static String MOB_CLASS_COURSE_RESOURCE_DIR = "http://static3." + Constant.IYUBA_CN + "resource/package";
    public final static String MOB_CLASS_COURSE_RESOURCE_APPEND = ".zip";

    public final static String MOB_CLASS_PACK_BGPIC = "http://static3." + Constant.IYUBA_CN + "resource/categoryIcon/";

    public final static String JLPT1_APPID = "205";//日语一级id
    public final static String JLPT2_APPID = "206";//日语二级id
    public final static String JLPT3_APPID = "203";//日语三级id
    public final static String CET4_APPID = "207";//日语三级id
    public final static String CET6_APPID = "208";//日语三级id

    //日志音频地址 ，非VIP
    public static String AUDIO_ADD = "http://staticvip." + Constant.IYUBA_CN + "sounds";
    //日志音频地址 ，VIP
    public static String AUDIO_VIP_ADD = "http://staticvip." + Constant.IYUBA_CN + "sounds";

    //日志视频地址 ，VIP
    public static String VIDEO_VIP_ADD = "http://staticvip." + Constant.IYUBA_CN + "video";
    //日志视频地址 ，非VIP
    public static String VIDEO_ADD = "http://staticvip." + Constant.IYUBA_CN + "video";
    public static String IMAGE_DOWN_PATH = "http://api." + Constant.IYUBA_COM + "v2/api.iyuba?protocol=10005&size=big&uid=";
    public static String PIC_ABLUM__ADD = "http://static1." + Constant.IYUBA_CN + "data/attachment/album/";

    public static String MicroClassReqPackId = "21";

    public static int testtype = 4;
    public static String urlPerfix = "http://cetsounds." + Constant.IYUBA_CN + "" + testtype + "/";
    public static String vipurlPerfix = "http://cetsoundsvip." + Constant.IYUBA_CN + "" + testtype + "/";

    public static String userimage = "http://api." + Constant.IYUBA_COM + "v2/api.iyuba?protocol=10005&uid=";//用户头像获取地址

    public static String detailUrl = "http://apps." + Constant.IYUBA_CN + "afterclass/getText.jsp?SongId=";//原文地址
    public static String lrcUrl = "http://apps." + Constant.IYUBA_CN + "afterclass/getLyrics.jsp?SongId=";//原文地址，听歌专用
    public static String searchUrl = "http://apps." + Constant.IYUBA_CN + "afterclass/searchApi.jsp?key=";//查询
    public static String titleUrl = "http://apps." + Constant.IYUBA_CN + "afterclass/getSongList.jsp?maxId=";//新闻列表，主程序用
    public static String vipurl = "http://staticvip." + Constant.IYUBA_CN + "sounds/song/";//vip地址
    public static String songurl = "http://staticvip." + Constant.IYUBA_CN + "sounds/song/";//普通地址
    public static String soundurl = "http://static2." + Constant.IYUBA_CN + "go/musichigh/";//1000之前解析地址

    //以上为爱语微课相关
    public static void reLoadData() {
//		envir = ConfigManager.Instance().loadString("envir");// 文件夹路径
        videoAddr = envir + "/audio/";// 音频文件存储位置
        recordAddr = envir + "/sound.amr";// 跟读音频
        voiceCommentAddr = envir + "/voicecomment.amr";// 语音评论
        screenShotAddr = envir + "/screenshot.jpg";// 截图位置
    }


    public static String getsimRecordAddr() {
        return simRecordAddr;
    }

    public static String getrecordTag() {
        return recordTag;
    }

    public static final String NEW_DUBBING_PREFIX = "http://userspeech.iyuba.cn/";

    public static String getNewDubbingUrl(String id) {
        return NEW_DUBBING_PREFIX + id;
    }

    public interface Voa {
        int DEFAULT_UID = 0;
        String VIP_VIDEO_PREFIX = "http://staticvip.iyuba.cn/video/voa/";
        String VIDEO_PREFIX = "http://staticvip.iyuba.cn/video/voa/";
        String VIDEO_PREFIX_NEW = "http://userspeech.iyuba.cn/video/voa/";
        String VIP_SOUND_PREFIX = "http://staticvip.iyuba.cn/sounds/voa/";
        String SOUND_PREFIX = "http://staticvip.iyuba.cn/sounds/voa/";
        String MP4_SUFFIX = ".mp4";
        String MP3_SUFFIX = ".mp3";
        String WAV_SUFFIX = ".wav";
        String AAC_SUFFIX = ".aac";
        String AMR_SUFFIX = ".amr";
        String JPG_SUFFIX = ".jpg";

        String SEPARATOR = "/";
        String TMP_PREFIX = "tmp";
        String SILENT_AAC_NAME = "silent.aac";
        String MERGE_AAC_NAME = "merge.aac";
        int SILENT_PIECE_TIME = 100;
        String COMMENT_VOICE_NAME = "comment_voice";
        String COMMENT_VOICE_SUFFIX = ".amr";
        int MAX_DIFFICULTY = 5;
        String FEEDBACK_END = "\n来自口语秀";
        String MERGE_MP3_NAME = "merge.mp3";
    }
}