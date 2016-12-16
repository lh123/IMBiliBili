package com.lh.imbilibili.data;

/**
 * Created by liuhui on 2016/7/5.
 */
public class Constant {

    public static final String APPKEY = "1d8b6e7d45233436";
    public static final String SECRETKEY = "560c52ccd288fed045859ed18bffd973";

    public static final String MINILOAD_SECRETKEY = "1c15888dc316e05a15fdd0a02ed6584f";

    public static final String BASE_URL = "http://bangumi.bilibili.com";
    public static final String API_URL = "http://api.bilibili.com";
    public static final String API_URLS = "https://api.bilibili.com";
    public static final String APP_URL = "http://app.bilibili.com";
    public static final String APP_URLS = "https://app.bilibili.com";
    public static final String COMMENT_URL = "http://comment.bilibili.com";
    public static final String ACCOUNT_URL = "https://account.bilibili.com";

    public static final String BANGUMI_RECOMMEND = "/api/bangumi_recommend";
    public static final String INDEX_PAGE = "/api/app_index_page_v4";
    public static final String BANGUMI_DETAIL = "/api/season_v4";
    public static final String GET_SOURCES = "/api/get_source";
    public static final String SEASON_GROUP = "/api/season_group.json";
    public static final String BANGUMI_INDEX_COND = "/api/bangumi_index_cond";
    public static final String BANGUMI_INDEX = "/api/bangumi_index";
    public static final String MY_FOLLOWS = "/api/my_follows";
    public static final String MY_CONCERNED_SEASON = "/api/get_concerned_season";
    public static final String REPORT_WATCH = "/api/report_watch";
    public static final String UNCONCERN_SEASON = "/api/unconcern_season";
    public static final String CONCERN_SEASON = "/api/concern_season";

    public static final String FEEDBACK = "/x/reply";
    public static final String REPLY_COUNT = "/x/reply/count";
    public static final String SEASON_RECOMMEND = "/api/season/recommend/{season_id}.json";
    public static final String VIDEO_DYNAMIC = "/x/feed/pull";

    public static final String PARTION_INFO = "/x/v2/region/show";
    public static final String PARTION_DYNAMIC = "/x/v2/region/show/dynamic";
    public static final String PARTION_CHILD = "/x/v2/region/show/child";
    public static final String PARTION_CHILD_LIST = "/x/v2/region/show/child/list";

    public static final String USER_SPACE = "/x/v2/space";
    public static final String USER_SPACE_ARCHIVE = "/x/v2/space/archive";
    public static final String USER_SPACE_COIN_ARCHIVE = "/x/v2/space/coinarc";
    public static final String USER_SPACE_BANGUMI = "/x/v2/space/bangumi";
    public static final String USER_SPACE_COMMUNITY = "/x/v2/space/community";
    public static final String USER_SPACE_GAME = "/x/v2/space/game";

    public static final String SEARCH = "/x/v2/search";
    public static final String SEARCH_TYPE = SEARCH + "/type";

    public static final String HISTORY = "/x/v2/history";
    public static final String HISTORY_ADD = HISTORY + "/add";

    public static final String GET_KEY = "/api/oauth2/getKey";
    public static final String LOGIN = "/api/oauth2/login";

    public static final String MY_INFO = "/api/myinfo/v2";

    public static final String VIDEO_DETAIL = "/x/v2/view";

    public static final String PLAY_URL = "/playurl";

    public static final String DANMAKU = "/{cid}.xml";

    public static final String SPLASH_URL = "http://app.bilibili.com/x/splash";

    public static final String SPLASH_FILE = "splash.json";

    public static final String UA = "Android Client/beta (1585086582@qq.com)";

    public static final int PLAT = 0;
    public static final String BUILD = "421000";
    public static final String MOBI_APP = "android";
    public static final String PLATFORM = "android";
    public static final String TYPE_BANGUMI = "bangumi";

    public static final String QUERY_ACCESS_KEY = "access_key";
    public static final String QUERY_APP_KEY = "appkey";
    public static final String QUERY_BUILD = "build";
    public static final String QUERY_MOBI_APP = "mobi_app";
    public static final String QUERY_PLATFORM = "platform";
    public static final String QUERY_TS = "ts";
    public static final String QUERY_SIGN = "sign";
    public static final String QUERY_SEASON_ID = "season_id";
    public static final String QUERY_TYPE = "type";
    public static final String QUERY_EPISODE_ID = "episode_id";
}
