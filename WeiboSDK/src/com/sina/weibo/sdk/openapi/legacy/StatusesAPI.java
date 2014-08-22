/*
 * Copyright (C) 2010-2013 The SINA WEIBO Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package com.sina.weibo.sdk.openapi.legacy;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.AbsOpenAPI;

/**
 * 璇ョ被灏佽浜嗗井鍗氭帴鍙ｃ�
 * 璇︽儏璇峰弬鑰�a href="http://t.cn/8F3e7SE">寰崥鎺ュ彛</a>
 * 
 * @author SINA
 * @date 2014-03-03
 */
public class StatusesAPI extends AbsOpenAPI {

    /** 杩囨护绫诲瀷ID锛�锛氬叏閮ㄣ�1锛氬師鍒涖�2锛氬浘鐗囥�3锛氳棰戙�4锛氶煶涔�*/
    public static final int FEATURE_ALL      = 0;
    public static final int FEATURE_ORIGINAL = 1;
    public static final int FEATURE_PICTURE  = 2;
    public static final int FEATURE_VIDEO    = 3;
    public static final int FEATURE_MUSICE   = 4;

    /** 浣滆�绛涢�绫诲瀷锛�锛氬叏閮ㄣ�1锛氭垜鍏虫敞鐨勪汉銆�锛氶檶鐢熶汉 */
    public static final int AUTHOR_FILTER_ALL        = 0;
    public static final int AUTHOR_FILTER_ATTENTIONS = 1;
    public static final int AUTHOR_FILTER_STRANGER   = 2;

    /** 鏉ユ簮绛涢�绫诲瀷锛�锛氬叏閮ㄣ�1锛氭潵鑷井鍗氱殑璇勮銆�锛氭潵鑷井缇ょ殑璇勮 */
    public static final int SRC_FILTER_ALL      = 0;
    public static final int SRC_FILTER_WEIBO    = 1;
    public static final int SRC_FILTER_WEIQUN   = 2;

    /** 鍘熷垱绛涢�绫诲瀷锛�锛氬叏閮ㄥ井鍗氥�1锛氬師鍒涚殑寰崥銆�*/
    public static final int TYPE_FILTER_ALL     = 0;
    public static final int TYPE_FILTER_ORIGAL  = 1;

    /** 鑾峰彇绫诲瀷锛�锛氬井鍗氥�2锛氳瘎璁恒�3锛氱淇★紝榛樿涓�銆�*/
    public static final int TYPE_STATUSES   = 1;
    public static final int TYPE_COMMENTS   = 2;
    public static final int TYPE_MESSAGE    = 3;

    /** 鏍囪瘑鏄惁鍦ㄨ浆鍙戠殑鍚屾椂鍙戣〃璇勮锛�锛氬惁銆�锛氳瘎璁虹粰褰撳墠寰崥銆�锛氳瘎璁虹粰鍘熷井鍗氥�3锛氶兘璇勮锛岄粯璁や负0 */
    public static final int COMMENTS_NONE           = 0;
    public static final int COMMENTS_CUR_STATUSES   = 1;
    public static final int COMMENTS_RIGAL_STATUSES = 2;
    public static final int COMMENTS_BOTH           = 3;

    /** 琛ㄦ儏绫诲埆锛宖ace锛氭櫘閫氳〃鎯呫�ani锛氶瓟娉曡〃鎯呫�cartoon锛氬姩婕〃鎯咃紝榛樿涓篺ace銆�*/
    public static final String EMOTION_TYPE_FACE    = "face";
    public static final String EMOTION_TYPE_ANI     = "ani";
    public static final String EMOTION_TYPE_CARTOON = "cartoon";

    /** 璇█绫诲埆锛宑nname锛氱畝浣撱�twname锛氱箒浣擄紝榛樿涓篶nname銆�*/
    public static final String LANGUAGE_CNNAME = "cnname";
    public static final String LANGUAGE_TWNAME = "twname";

    public StatusesAPI(Oauth2AccessToken accessToken) {
        super(accessToken);
    }

    private static final String SERVER_URL_PRIX = API_SERVER + "/statuses";

    /**
     * 杩斿洖鏈�柊鐨勫叕鍏卞井鍗氥�
     * 
     * @param count     鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0
     * @param page      杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param base_app  鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void publicTimeline(int count, int page, boolean base_app, RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        params.put("count", count);
        params.put("page", page);
        params.put("base_app", base_app ? 1 : 0);
        requestAsync(SERVER_URL_PRIX + "/public_timeline.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇褰撳墠鐧诲綍鐢ㄦ埛鍙婂叾鎵�叧娉ㄧ敤鎴风殑鏈�柊寰崥銆�     * 
     * @param since_id      鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID姣攕ince_id澶х殑寰崥锛堝嵆姣攕ince_id鏃堕棿鏅氱殑寰崥锛夛紝榛樿涓�
     * @param max_id        鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID灏忎簬鎴栫瓑浜巑ax_id鐨勫井鍗氾紝榛樿涓�
     * @param count         鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0
     * @param page          杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param base_app      鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param featureType   杩囨护绫诲瀷ID锛�锛氬叏閮ㄣ�1锛氬師鍒涖�2锛氬浘鐗囥�3锛氳棰戙�4锛氶煶涔愶紝榛樿涓�銆�
     *                      <li> {@link #FEATURE_ALL}
     *                      <li> {@link #FEATURE_ORIGINAL}
     *                      <li> {@link #FEATURE_PICTURE}
     *                      <li> {@link #FEATURE_VIDEO}
     *                      <li> {@link #FEATURE_MUSICE}
     * @param trim_user     杩斿洖鍊间腑user瀛楁寮�叧锛宖alse锛氳繑鍥炲畬鏁磚ser瀛楁銆乼rue锛歶ser瀛楁浠呰繑鍥瀠ser_id锛岄粯璁や负false
     * @param listener      寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void friendsTimeline(long since_id, long max_id, int count, int page, boolean base_app, int featureType,
            boolean trim_user, RequestListener listener) {
        WeiboParameters params = buildTimeLineWithAppTrim(since_id, max_id, count, page, base_app, trim_user, featureType);
        requestAsync(SERVER_URL_PRIX + "/friends_timeline.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇褰撳墠鐧诲綍鐢ㄦ埛鍙婂叾鎵�叧娉ㄧ敤鎴风殑鏈�柊寰崥鐨処D銆�     * 
     * @param since_id      鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID姣攕ince_id澶х殑寰崥锛堝嵆姣攕ince_id鏃堕棿鏅氱殑寰崥锛夛紝榛樿涓�
     * @param max_id        鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID灏忎簬鎴栫瓑浜巑ax_id鐨勫井鍗氾紝榛樿涓�
     * @param count         鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0
     * @param page          杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param base_app      鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param featureType   杩囨护绫诲瀷ID锛�锛氬叏閮ㄣ�1锛氬師鍒涖�2锛氬浘鐗囥�3锛氳棰戙�4锛氶煶涔愶紝榛樿涓�
     *                      <li> {@link #FEATURE_ALL}
     *                      <li> {@link #FEATURE_ORIGINAL}
     *                      <li> {@link #FEATURE_PICTURE}
     *                      <li> {@link #FEATURE_VIDEO}
     *                      <li> {@link #FEATURE_MUSICE}
     * @param listener      寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void friendsTimelineIds(long since_id, long max_id, int count, int page, boolean base_app, int featureType,
            RequestListener listener) {
        WeiboParameters params = buildTimeLineWithApp(since_id, max_id, count, page, base_app, featureType);
        requestAsync(SERVER_URL_PRIX + "/friends_timeline/ids.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇褰撳墠鐧诲綍鐢ㄦ埛鍙婂叾鎵�叧娉ㄧ敤鎴风殑鏈�柊寰崥銆�     * 
     * @param since_id      鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID姣攕ince_id澶х殑寰崥锛堝嵆姣攕ince_id鏃堕棿鏅氱殑寰崥锛夛紝榛樿涓�
     * @param max_id        鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID灏忎簬鎴栫瓑浜巑ax_id鐨勫井鍗氾紝榛樿涓�
     * @param count         鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0
     * @param page          杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param base_app      鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param featureType   杩囨护绫诲瀷ID锛�锛氬叏閮ㄣ�1锛氬師鍒涖�2锛氬浘鐗囥�3锛氳棰戙�4锛氶煶涔愶紝榛樿涓�
     *                      <li> {@link #FEATURE_ALL}
     *                      <li> {@link #FEATURE_ORIGINAL}
     *                      <li> {@link #FEATURE_PICTURE}
     *                      <li> {@link #FEATURE_VIDEO}
     *                      <li> {@link #FEATURE_MUSICE}
     * @param trim_user     杩斿洖鍊间腑user瀛楁寮�叧锛宖alse锛氳繑鍥炲畬鏁磚ser瀛楁銆乼rue锛歶ser瀛楁浠呰繑鍥瀠ser_id锛岄粯璁や负false
     * @param listener      寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void homeTimeline(long since_id, long max_id, int count, int page, boolean base_app, int featureType,
            boolean trim_user, RequestListener listener) {
        WeiboParameters params = buildTimeLineWithAppTrim(since_id, max_id, count, page, base_app, trim_user,
                featureType);
        requestAsync(SERVER_URL_PRIX + "/home_timeline.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇鏌愪釜鐢ㄦ埛鏈�柊鍙戣〃鐨勫井鍗氬垪琛ㄣ�
     * 
     * @param uid           闇�鏌ヨ鐨勭敤鎴稩D
     * @param since_id      鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID姣攕ince_id澶х殑寰崥锛堝嵆姣攕ince_id鏃堕棿鏅氱殑寰崥锛夛紝榛樿涓�
     * @param max_id        鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID灏忎簬鎴栫瓑浜巑ax_id鐨勫井鍗氾紝榛樿涓�
     * @param count         鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0
     * @param page          杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param base_app      鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param featureType   杩囨护绫诲瀷ID锛�锛氬叏閮ㄣ�1锛氬師鍒涖�2锛氬浘鐗囥�3锛氳棰戙�4锛氶煶涔愶紝榛樿涓�
     *                      <li> {@link #FEATURE_ALL}
     *                      <li> {@link #FEATURE_ORIGINAL}
     *                      <li> {@link #FEATURE_PICTURE}
     *                      <li> {@link #FEATURE_VIDEO}
     *                      <li> {@link #FEATURE_MUSICE}
     * @param trim_user     杩斿洖鍊间腑user瀛楁寮�叧锛宖alse锛氳繑鍥炲畬鏁磚ser瀛楁銆乼rue锛歶ser瀛楁浠呰繑鍥瀠ser_id锛岄粯璁や负false
     * @param listener      寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void userTimeline(long uid, long since_id, long max_id, int count, int page, boolean base_app,
            int featureType, boolean trim_user, RequestListener listener) {
        WeiboParameters params = buildTimeLineWithAppTrim(since_id, max_id, count, page, base_app, trim_user,
                featureType);
        params.put("uid", uid);
        requestAsync(SERVER_URL_PRIX + "/user_timeline.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇鏌愪釜鐢ㄦ埛鏈�柊鍙戣〃鐨勫井鍗氬垪琛ㄣ�
     * 
     * @param screen_name   闇�鏌ヨ鐨勭敤鎴锋樀绉�     * @param since_id      鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID姣攕ince_id澶х殑寰崥锛堝嵆姣攕ince_id鏃堕棿鏅氱殑寰崥锛夛紝榛樿涓�
     * @param max_id        鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID灏忎簬鎴栫瓑浜巑ax_id鐨勫井鍗氾紝榛樿涓�
     * @param count         鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0
     * @param page          杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param base_app      鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param featureType   杩囨护绫诲瀷ID锛�锛氬叏閮ㄣ�1锛氬師鍒涖�2锛氬浘鐗囥�3锛氳棰戙�4锛氶煶涔愶紝榛樿涓�
     *                      <li> {@link #FEATURE_ALL}
     *                      <li> {@link #FEATURE_ORIGINAL}
     *                      <li> {@link #FEATURE_PICTURE}
     *                      <li> {@link #FEATURE_VIDEO}
     *                      <li> {@link #FEATURE_MUSICE}
     * @param trim_user     杩斿洖鍊间腑user瀛楁寮�叧锛宖alse锛氳繑鍥炲畬鏁磚ser瀛楁銆乼rue锛歶ser瀛楁浠呰繑鍥瀠ser_id锛岄粯璁や负false
     * @param listener      寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void userTimeline(String screen_name, long since_id, long max_id, int count, int page, boolean base_app,
            int featureType, boolean trim_user, RequestListener listener) {
        WeiboParameters params = buildTimeLineWithAppTrim(since_id, max_id, count, page, base_app, trim_user,
                featureType);
        params.put("screen_name", screen_name);
        requestAsync(SERVER_URL_PRIX + "/user_timeline.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇褰撳墠鐢ㄦ埛鏈�柊鍙戣〃鐨勫井鍗氬垪琛�     * 
     * @param screen_name   闇�鏌ヨ鐨勭敤鎴锋樀绉�     * @param since_id      鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID姣攕ince_id澶х殑寰崥锛堝嵆姣攕ince_id鏃堕棿鏅氱殑寰崥锛夛紝榛樿涓�
     * @param max_id        鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID灏忎簬鎴栫瓑浜巑ax_id鐨勫井鍗氾紝榛樿涓�
     * @param count         鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0
     * @param page          杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param base_app      鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param featureType   杩囨护绫诲瀷ID锛�锛氬叏閮ㄣ�1锛氬師鍒涖�2锛氬浘鐗囥�3锛氳棰戙�4锛氶煶涔愶紝榛樿涓�
     *                      <li> {@link #FEATURE_ALL}
     *                      <li> {@link #FEATURE_ORIGINAL}
     *                      <li> {@link #FEATURE_PICTURE}
     *                      <li> {@link #FEATURE_VIDEO}
     *                      <li> {@link #FEATURE_MUSICE}
     * @param trim_user     杩斿洖鍊间腑user瀛楁寮�叧锛宖alse锛氳繑鍥炲畬鏁磚ser瀛楁銆乼rue锛歶ser瀛楁浠呰繑鍥瀠ser_id锛岄粯璁や负false
     * @param listener      寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void userTimeline(long since_id, long max_id, int count, int page, boolean base_app, int featureType,
            boolean trim_user, RequestListener listener) {
        WeiboParameters params = buildTimeLineWithAppTrim(since_id, max_id, count, page, base_app, trim_user,
                featureType);
        requestAsync(SERVER_URL_PRIX + "/user_timeline.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇鐢ㄦ埛鍙戝竷鐨勫井鍗氱殑ID
     * 
     * @param uid           闇�鏌ヨ鐨勭敤鎴稩D
     * @param since_id      鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID姣攕ince_id澶х殑寰崥锛堝嵆姣攕ince_id鏃堕棿鏅氱殑寰崥锛夛紝榛樿涓�
     * @param max_id        鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID灏忎簬鎴栫瓑浜巑ax_id鐨勫井鍗氾紝榛樿涓�
     * @param count         鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0
     * @param page          杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param base_app      鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param featureType   杩囨护绫诲瀷ID锛�锛氬叏閮ㄣ�1锛氬師鍒涖�2锛氬浘鐗囥�3锛氳棰戙�4锛氶煶涔愶紝榛樿涓�
     *                      <li> {@link #FEATURE_ALL}
     *                      <li> {@link #FEATURE_ORIGINAL}
     *                      <li> {@link #FEATURE_PICTURE}
     *                      <li> {@link #FEATURE_VIDEO}
     *                      <li> {@link #FEATURE_MUSICE}
     * @param listener      寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void userTimelineIds(long uid, long since_id, long max_id, int count, int page, boolean base_app,
            int featureType, RequestListener listener) {
        WeiboParameters params = buildTimeLineWithApp(since_id, max_id, count, page, base_app, featureType);
        params.put("uid", uid);
        requestAsync(SERVER_URL_PRIX + "/user_timeline/ids.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇鐢ㄦ埛鍙戝竷鐨勫井鍗氱殑ID
     * 
     * @param screen_name   闇�鏌ヨ鐨勭敤鎴锋樀绉�     * @param since_id      鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID姣攕ince_id澶х殑寰崥锛堝嵆姣攕ince_id鏃堕棿鏅氱殑寰崥锛夛紝榛樿涓�
     * @param max_id        鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID灏忎簬鎴栫瓑浜巑ax_id鐨勫井鍗氾紝榛樿涓�
     * @param count         鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0
     * @param page          杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param base_app      鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param featureType   杩囨护绫诲瀷ID锛�锛氬叏閮ㄣ�1锛氬師鍒涖�2锛氬浘鐗囥�3锛氳棰戙�4锛氶煶涔愶紝榛樿涓�
     *                      <li> {@link #FEATURE_ALL}
     *                      <li> {@link #FEATURE_ORIGINAL}
     *                      <li> {@link #FEATURE_PICTURE}
     *                      <li> {@link #FEATURE_VIDEO}
     *                      <li> {@link #FEATURE_MUSICE}
     * @param listener      寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void userTimelineIds(String screen_name, long since_id, long max_id, int count, int page, boolean base_app,
            int featureType, RequestListener listener) {
        WeiboParameters params = buildTimeLineWithApp(since_id, max_id, count, page, base_app, featureType);
        params.put("screen_name", screen_name);
        requestAsync(SERVER_URL_PRIX + "/user_timeline/ids.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇鎸囧畾寰崥鐨勮浆鍙戝井鍗氬垪琛�     * 
     * @param id            闇�鏌ヨ鐨勫井鍗欼D銆�     * @param since_id      鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID姣攕ince_id澶х殑寰崥锛堝嵆姣攕ince_id鏃堕棿鏅氱殑寰崥锛夛紝榛樿涓�
     * @param max_id        鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID灏忎簬鎴栫瓑浜巑ax_id鐨勫井鍗氾紝榛樿涓�
     * @param count         鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0
     * @param page          杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param authorType    浣滆�绛涢�绫诲瀷锛�锛氬叏閮ㄣ�1锛氭垜鍏虫敞鐨勪汉銆�锛氶檶鐢熶汉锛岄粯璁や负0銆傚彲涓轰互涓嬪嚑绉嶏細
     *                      <li> {@link #AUTHOR_FILTER_ALL}
     *                      <li> {@link #AUTHOR_FILTER_ATTENTIONS}
     *                      <li> {@link #AUTHOR_FILTER_STRANGER}
     * @param listener      寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void repostTimeline(long id, long since_id, long max_id, int count, int page, int authorType,
            RequestListener listener) {
        WeiboParameters params = buildTimeLineBase(since_id, max_id, count, page);
        params.put("id", id);
        params.put("filter_by_author", authorType);
        requestAsync(SERVER_URL_PRIX + "/repost_timeline.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇涓�潯鍘熷垱寰崥鐨勬渶鏂拌浆鍙戝井鍗氱殑ID銆�     * 
     * @param id            闇�鏌ヨ鐨勫井鍗欼D
     * @param since_id      鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID姣攕ince_id澶х殑寰崥锛堝嵆姣攕ince_id鏃堕棿鏅氱殑寰崥锛夛紝榛樿涓�
     * @param max_id        鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID灏忎簬鎴栫瓑浜巑ax_id鐨勫井鍗氾紝榛樿涓�
     * @param count         鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0
     * @param page          杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param authorType    浣滆�绛涢�绫诲瀷锛�锛氬叏閮ㄣ�1锛氭垜鍏虫敞鐨勪汉銆�锛氶檶鐢熶汉锛岄粯璁や负0銆傚彲涓轰互涓嬪嚑绉嶏細
     *                      <li> {@link #AUTHOR_FILTER_ALL}
     *                      <li> {@link #AUTHOR_FILTER_ATTENTIONS}
     *                      <li> {@link #AUTHOR_FILTER_STRANGER}
     * @param listener      寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void repostTimelineIds(long id, long since_id, long max_id, int count, int page, int authorType,
            RequestListener listener) {
        WeiboParameters params = buildTimeLineBase(since_id, max_id, count, page);
        params.put("id", id);
        params.put("filter_by_author", authorType);
        requestAsync(SERVER_URL_PRIX + "/repost_timeline/ids.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇褰撳墠鐢ㄦ埛鏈�柊杞彂鐨勫井鍗氬垪琛ㄣ�
     * 
     * @param since_id      鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID姣攕ince_id澶х殑寰崥锛堝嵆姣攕ince_id鏃堕棿鏅氱殑寰崥锛夛紝榛樿涓�
     * @param max_id        鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID灏忎簬鎴栫瓑浜巑ax_id鐨勫井鍗氾紝榛樿涓�
     * @param count         鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0
     * @param page          杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param listener      寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void repostByMe(long since_id, long max_id, int count, int page, RequestListener listener) {
        WeiboParameters params = buildTimeLineBase(since_id, max_id, count, page);
        requestAsync(SERVER_URL_PRIX + "/repost_by_me.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇鏈�柊鐨勬彁鍒扮櫥褰曠敤鎴风殑寰崥鍒楄〃锛屽嵆@鎴戠殑寰崥銆�     * 
     * @param since_id      鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID姣攕ince_id澶х殑寰崥锛堝嵆姣攕ince_id鏃堕棿鏅氱殑寰崥锛夛紝榛樿涓�
     * @param max_id        鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID灏忎簬鎴栫瓑浜巑ax_id鐨勫井鍗氾紝榛樿涓�
     * @param count         鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0
     * @param page          杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param authorType    浣滆�绛涢�绫诲瀷锛�锛氬叏閮ㄣ�1锛氭垜鍏虫敞鐨勪汉銆�锛氶檶鐢熶汉锛岄粯璁や负0銆傚彲涓轰互涓嬪嚑绉嶏細
     *                      <li> {@link #AUTHOR_FILTER_ALL}
     *                      <li> {@link #AUTHOR_FILTER_ATTENTIONS}
     *                      <li> {@link #AUTHOR_FILTER_STRANGER}
     * @param sourceType    鏉ユ簮绛涢�绫诲瀷锛�锛氬叏閮ㄣ�1锛氭潵鑷井鍗氱殑璇勮銆�锛氭潵鑷井缇ょ殑璇勮銆傚彲鍒嗕负浠ヤ笅鍑犵锛�     *                      <li> {@link #SRC_FILTER_ALL}
     *                      <li> {@link #SRC_FILTER_WEIBO}
     *                      <li> {@link #SRC_FILTER_WEIQUN}
     * @param filterType    鍘熷垱绛涢�绫诲瀷锛�锛氬叏閮ㄥ井鍗氥�1锛氬師鍒涚殑寰崥锛岄粯璁や负0銆傚彲鍒嗕负浠ヤ笅鍑犵锛�     *                      <li> {@link #TYPE_FILTER_ALL}
     *                      <li> {@link #TYPE_FILTER_ORIGAL}
     * @param trim_user     杩斿洖鍊间腑user瀛楁寮�叧锛宖alse锛氳繑鍥炲畬鏁磚ser瀛楁銆乼rue锛歶ser瀛楁浠呰繑鍥瀠ser_id锛岄粯璁や负false
     * @param listener      寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void mentions(long since_id, long max_id, int count, int page, int authorType, int sourceType,
            int filterType, boolean trim_user, RequestListener listener) {
        WeiboParameters params = buildTimeLineBase(since_id, max_id, count, page);
        params.put("filter_by_author", authorType);
        params.put("filter_by_source", sourceType);
        params.put("filter_by_type", filterType);
        params.put("trim_user", trim_user ? 1 : 0);
        requestAsync(SERVER_URL_PRIX + "/mentions.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇@褰撳墠鐢ㄦ埛鐨勬渶鏂板井鍗氱殑ID銆�     * 
     * @param since_id      鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID姣攕ince_id澶х殑寰崥锛堝嵆姣攕ince_id鏃堕棿鏅氱殑寰崥锛夛紝榛樿涓�
     * @param max_id        鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID灏忎簬鎴栫瓑浜巑ax_id鐨勫井鍗氾紝榛樿涓�
     * @param count         鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0
     * @param page          杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param authorType    浣滆�绛涢�绫诲瀷锛�锛氬叏閮ㄣ�1锛氭垜鍏虫敞鐨勪汉銆�锛氶檶鐢熶汉锛岄粯璁や负0銆傚彲涓轰互涓嬪嚑绉嶏細
     *                      <li> {@link #AUTHOR_FILTER_ALL}
     *                      <li> {@link #AUTHOR_FILTER_ATTENTIONS}
     *                      <li> {@link #AUTHOR_FILTER_STRANGER}
     * @param sourceType    鏉ユ簮绛涢�绫诲瀷锛�锛氬叏閮ㄣ�1锛氭潵鑷井鍗氱殑璇勮銆�锛氭潵鑷井缇ょ殑璇勮銆傚彲鍒嗕负浠ヤ笅鍑犵锛�     *                      <li> {@link #SRC_FILTER_ALL}
     *                      <li> {@link #SRC_FILTER_WEIBO}
     *                      <li> {@link #SRC_FILTER_WEIQUN}
     * @param filterType    鍘熷垱绛涢�绫诲瀷锛�锛氬叏閮ㄥ井鍗氥�1锛氬師鍒涚殑寰崥锛岄粯璁や负0銆傚彲鍒嗕负浠ヤ笅鍑犵锛�     *                      <li> {@link #TYPE_FILTER_ALL}
     *                      <li> {@link #TYPE_FILTER_ORIGAL}
     * @param listener      寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void mentionsIds(long since_id, long max_id, int count, int page, int authorType, int sourceType,
            int filterType, RequestListener listener) {
        WeiboParameters params = buildTimeLineBase(since_id, max_id, count, page);
        params.put("filter_by_author", authorType);
        params.put("filter_by_source", sourceType);
        params.put("filter_by_type", filterType);
        requestAsync(SERVER_URL_PRIX + "/mentions/ids.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇鍙屽悜鍏虫敞鐢ㄦ埛鐨勬渶鏂板井鍗氥�
     * 
     * @param since_id      鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID姣攕ince_id澶х殑寰崥锛堝嵆姣攕ince_id鏃堕棿鏅氱殑寰崥锛夛紝榛樿涓�
     * @param max_id        鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID灏忎簬鎴栫瓑浜巑ax_id鐨勫井鍗氾紝榛樿涓�
     * @param count         鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0
     * @param page          杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param base_app      鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param featureType   杩囨护绫诲瀷ID锛�锛氬叏閮ㄣ�1锛氬師鍒涖�2锛氬浘鐗囥�3锛氳棰戙�4锛氶煶涔愶紝榛樿涓�
     *                      <li> {@link #FEATURE_ALL}
     *                      <li> {@link #FEATURE_ORIGINAL}
     *                      <li> {@link #FEATURE_PICTURE}
     *                      <li> {@link #FEATURE_VIDEO}
     *                      <li> {@link #FEATURE_MUSICE}
     * @param trim_user     杩斿洖鍊间腑user瀛楁寮�叧锛宖alse锛氳繑鍥炲畬鏁磚ser瀛楁銆乼rue锛歶ser瀛楁浠呰繑鍥瀠ser_id锛岄粯璁や负false
     * @param listener      寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void bilateralTimeline(long since_id, long max_id, int count, int page, boolean base_app, int featureType,
            boolean trim_user, RequestListener listener) {
        WeiboParameters params = buildTimeLineWithAppTrim(since_id, max_id, count, page, base_app, trim_user,
                featureType);
        requestAsync(SERVER_URL_PRIX + "/bilateral_timeline.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鏍规嵁寰崥ID鑾峰彇鍗曟潯寰崥鍐呭銆�     * 
     * @param id        闇�鑾峰彇鐨勫井鍗欼D
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void show(long id, RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        params.put("id", id);
        requestAsync(SERVER_URL_PRIX + "/show.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 閫氳繃寰崥锛堣瘎璁恒�绉佷俊锛塈D鑾峰彇鍏禡ID銆�     * 
     * @param ids       闇�鏌ヨ鐨勫井鍗氾紙璇勮銆佺淇★級ID锛屾渶澶氫笉瓒呰繃20涓�
     * @param type      鑾峰彇绫诲瀷锛�锛氬井鍗氥�2锛氳瘎璁恒�3锛氱淇★紝榛樿涓�銆傚彲涓哄嚑涓嬪嚑绉嶏細 
     *                  <li> {@link #TYPE_STATUSES}
     *                  <li> {@link #TYPE_COMMENTS}
     *                  <li> {@link #TYPE_MESSAGE}
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void queryMID(long[] ids, int type, RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        if (1 == ids.length) {
            params.put("id", ids[0]);
        } else {
            params.put("is_batch", 1);
            StringBuilder strb = new StringBuilder();
            for (long id : ids) {
                strb.append(id).append(",");
            }
            strb.deleteCharAt(strb.length() - 1);
            params.put("id", strb.toString());
        }
        params.put("type", type);
        requestAsync(SERVER_URL_PRIX + "/querymid.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 閫氳繃寰崥锛堣瘎璁恒�绉佷俊锛塎ID鑾峰彇鍏禝D,褰㈠鈥�z4efAo4lk鈥濈殑MID鍗充负缁忚繃base62杞崲鐨凪ID銆�     * 
     * @param mids      闇�鏌ヨ鐨勫井鍗氾紙璇勮銆佺淇★級MID锛屾渶澶氫笉瓒呰繃20涓�     * @param type      鑾峰彇绫诲瀷锛�锛氬井鍗氥�2锛氳瘎璁恒�3锛氱淇★紝榛樿涓�銆傚彲涓哄嚑涓嬪嚑绉嶏細 
     *                  <li> {@link #TYPE_STATUSES}
     *                  <li> {@link #TYPE_COMMENTS}
     *                  <li> {@link #TYPE_MESSAGE}
     * @param inbox     浠呭绉佷俊鏈夋晥锛屽綋MID绫诲瀷涓虹淇℃椂鐢ㄦ鍙傛暟锛�锛氬彂浠剁銆�锛氭敹浠剁锛岄粯璁や负0
     * @param isBase62  MID鏄惁鏄痓ase62缂栫爜锛�锛氬惁銆�锛氭槸锛岄粯璁や负0
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void queryID(String[] mids, int type, boolean inbox, boolean isBase62, RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        if (mids != null) {
            if (1 == mids.length) {
                params.put("mid", mids[0]);
            } else {
                params.put("is_batch", 1);
                StringBuilder strb = new StringBuilder();
                for (String mid : mids) {
                    strb.append(mid).append(",");
                }
                strb.deleteCharAt(strb.length() - 1);
                params.put("mid", strb.toString());
            }
        }

        params.put("type", type);
        params.put("inbox", inbox ? 1 : 0);
        params.put("isBase62", isBase62 ? 1 : 0);
        requestAsync(SERVER_URL_PRIX + "/queryid.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鎸夊ぉ杩斿洖鐑棬寰崥杞彂姒滅殑寰崥鍒楄〃銆�     * 
     * @param count     杩斿洖鐨勮褰曟潯鏁帮紝鏈�ぇ涓嶈秴杩�0锛岄粯璁や负20
     * @param base_app  鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void hotRepostDaily(int count, boolean base_app, RequestListener listener) {
        WeiboParameters params = buildHotParams(count, base_app);
        requestAsync(SERVER_URL_PRIX + "/hot/repost_daily.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鎸夊懆杩斿洖鐑棬寰崥杞彂姒滅殑寰崥鍒楄〃銆�     * 
     * @param count     杩斿洖鐨勮褰曟潯鏁帮紝鏈�ぇ涓嶈秴杩�0锛岄粯璁や负20
     * @param base_app  鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void hotRepostWeekly(int count, boolean base_app, RequestListener listener) {
        WeiboParameters params = buildHotParams(count, base_app);
        requestAsync(SERVER_URL_PRIX + "/hot/repost_weekly.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鎸夊ぉ杩斿洖鐑棬寰崥璇勮姒滅殑寰崥鍒楄〃銆�     * 
     * @param count     杩斿洖鐨勮褰曟潯鏁帮紝鏈�ぇ涓嶈秴杩�0锛岄粯璁や负20
     * @param base_app  鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void hotCommentsDaily(int count, boolean base_app, RequestListener listener) {
        WeiboParameters params = buildHotParams(count, base_app);
        requestAsync(SERVER_URL_PRIX + "/hot/comments_daily.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鎸夊懆杩斿洖鐑棬寰崥璇勮姒滅殑寰崥鍒楄〃銆�     * 
     * @param count     杩斿洖鐨勮褰曟潯鏁帮紝鏈�ぇ涓嶈秴杩�0锛岄粯璁や负20
     * @param base_app  鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void hotCommentsWeekly(int count, boolean base_app, RequestListener listener) {
        WeiboParameters params = buildHotParams(count, base_app);
        requestAsync(SERVER_URL_PRIX + "/hot/comments_weekly.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鎵归噺鑾峰彇鎸囧畾寰崥鐨勮浆鍙戞暟璇勮鏁般�
     * 
     * @param ids       闇�鑾峰彇鏁版嵁鐨勫井鍗欼D锛屾渶澶氫笉瓒呰繃100涓�     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void count(String[] ids, RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        StringBuilder strb = new StringBuilder();
        for (String id : ids) {
            strb.append(id).append(",");
        }
        strb.deleteCharAt(strb.length() - 1);
        params.put("ids", strb.toString());
        requestAsync(SERVER_URL_PRIX + "/count.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 杞彂涓�潯寰崥銆�     * 
     * @param id            瑕佽浆鍙戠殑寰崥ID
     * @param status        娣诲姞鐨勮浆鍙戞枃鏈紝鍐呭涓嶈秴杩�40涓眽瀛楋紝涓嶅～鍒欓粯璁や负鈥滆浆鍙戝井鍗氣�
     * @param commentType   鏄惁鍦ㄨ浆鍙戠殑鍚屾椂鍙戣〃璇勮锛�锛氬惁銆�锛氳瘎璁虹粰褰撳墠寰崥銆�锛氳瘎璁虹粰鍘熷井鍗氥�3锛氶兘璇勮锛岄粯璁や负0
     *                      <li> {@link #COMMENTS_NONE}
     *                      <li> {@link #COMMENTS_CUR_STATUSES}
     *                      <li> {@link #COMMENTS_RIGAL_STATUSES}
     *                      <li> {@link #COMMENTS_BOTH}
     * @param listener      寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void repost(long id, String status, int commentType, RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        params.put("id", id);
        params.put("status", status);
        params.put("is_comment", commentType);
        requestAsync(SERVER_URL_PRIX + "/repost.json", params, HTTPMETHOD_POST, listener);
    }

    /**
     * 鏍规嵁寰崥ID鍒犻櫎鎸囧畾寰崥銆�     * 
     * @param id        闇�鍒犻櫎鐨勫井鍗欼D
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void destroy(long id, RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        params.put("id", id);
        requestAsync(SERVER_URL_PRIX + "/destroy.json", params, HTTPMETHOD_POST, listener);
    }

    /**
     * 鍙戝竷涓�潯鏂板井鍗�杩炵画涓ゆ鍙戝竷鐨勫井鍗氫笉鍙互閲嶅)銆�     * 
     * @param content   瑕佸彂甯冪殑寰崥鏂囨湰鍐呭锛屽唴瀹逛笉瓒呰繃140涓眽瀛�     * @param lat       绾害锛屾湁鏁堣寖鍥达細-90.0鍒�90.0锛�琛ㄧず鍖楃含锛岄粯璁や负0.0
     * @param lon       缁忓害锛屾湁鏁堣寖鍥达細-180.0鍒�180.0锛�琛ㄧず涓滅粡锛岄粯璁や负0.0
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void update(String content, String lat, String lon, RequestListener listener) {
        WeiboParameters params = buildUpdateParams(content, lat, lon);
        requestAsync(SERVER_URL_PRIX + "/update.json", params, HTTPMETHOD_POST, listener);
    }

    /**
     * 涓婁紶鍥剧墖骞跺彂甯冧竴鏉℃柊寰崥锛屾鏂规硶浼氬鐞唘rlencode銆�     * 
     * @param content   瑕佸彂甯冪殑寰崥鏂囨湰鍐呭锛屽唴瀹逛笉瓒呰繃140涓眽瀛�     * @param bitmap    瑕佷笂浼犵殑鍥剧墖锛屼粎鏀寔JPEG銆丟IF銆丳NG鏍煎紡锛屽浘鐗囧ぇ灏忓皬浜�M
     * @param lat       绾害锛屾湁鏁堣寖鍥达細-90.0鍒�90.0锛�琛ㄧず鍖楃含锛岄粯璁や负0.0
     * @param lon       缁忓害锛屾湁鏁堣寖鍥达細-180.0鍒�180.0锛�琛ㄧず涓滅粡锛岄粯璁や负0.0
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     
     */
    public void upload(String content, Bitmap bitmap, String lat, String lon, RequestListener listener) {
        WeiboParameters params = buildUpdateParams(content, lat, lon);
        params.put("pic", bitmap);
        requestAsync(SERVER_URL_PRIX + "/upload.json", params, HTTPMETHOD_POST, listener);
    }

    /**
     * 鎸囧畾涓�釜鍥剧墖URL鍦板潃鎶撳彇鍚庝笂浼犲苟鍚屾椂鍙戝竷涓�潯鏂板井鍗氾紝姝ゆ柟娉曚細澶勭悊URLencode銆�     * 
     * @param status    瑕佸彂甯冪殑寰崥鏂囨湰鍐呭锛屽唴瀹逛笉瓒呰繃140涓眽瀛�     * @param imageUrl  鍥剧墖鐨刄RL鍦板潃锛屽繀椤讳互http寮�ご
     * @param pic_id    宸茬粡涓婁紶鐨勫浘鐗噋id锛屽涓椂浣跨敤鑻辨枃鍗婅閫楀彿绗﹀垎闅旓紝鏈�涓嶈秴杩囦節寮犮� imageUrl 鍜�pic_id蹇呴�涓�釜锛屼袱涓弬鏁伴兘瀛樺湪鏃讹紝鍙杙icid鍙傛暟鐨勫�涓哄噯
     * @param lat       绾害锛屾湁鏁堣寖鍥达細-90.0鍒�90.0锛�琛ㄧず鍖楃含锛岄粯璁や负0.0
     * @param lon       缁忓害锛屾湁鏁堣寖鍥达細-180.0鍒�180.0锛�琛ㄧず涓滅粡锛岄粯璁や负0.0
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void uploadUrlText(String status, String imageUrl, String pic_id, String lat, String lon,
            RequestListener listener) {
        WeiboParameters params = buildUpdateParams(status, lat, lon);
        params.put("url", imageUrl);
        params.put("pic_id", pic_id);
        requestAsync(SERVER_URL_PRIX + "/upload_url_text.json", params, HTTPMETHOD_POST, listener);
    }

    /**
     * 鑾峰彇寰崥瀹樻柟琛ㄦ儏鐨勮缁嗕俊鎭�
     * 
     * @param type      琛ㄦ儏绫诲埆锛岃〃鎯呯被鍒紝face锛氭櫘閫氳〃鎯呫�ani锛氶瓟娉曡〃鎯呫�cartoon锛氬姩婕〃鎯咃紝榛樿涓篺ace銆傚彲涓轰互涓嬪嚑绉嶏細 
     *                  <li> {@link #EMOTION_TYPE_FACE}
     *                  <li> {@link #EMOTION_TYPE_ANI}
     *                  <li> {@link #EMOTION_TYPE_CARTOON}
     * @param language  璇█绫诲埆锛宑nname锛氥�twname锛氾紝榛樿涓篶nname銆�
     *                  <li> {@link #LANGUAGE_CNNAME}
     *                  <li> {@link #LANGUAGE_TWNAME}
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void emotions(String type, String language, RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        params.put("type", type);
        params.put("language", language);
        requestAsync(API_SERVER + "/emotions.json", params, HTTPMETHOD_GET, listener);
    }

    // 缁勮TimeLines鐨勫弬鏁�   
    private WeiboParameters buildTimeLineBase(long since_id, long max_id, int count, int page) {
        WeiboParameters params = new WeiboParameters();
        params.put("since_id", since_id);
        params.put("max_id", max_id);
        params.put("count", count);
        params.put("page", page);
        return params;
    }

    private WeiboParameters buildTimeLineWithApp(long since_id, long max_id, int count, int page, boolean base_app,
            int featureType) {
        WeiboParameters params = buildTimeLineBase(since_id, max_id, count, page);
        params.put("feature", featureType);
        params.put("base_app", base_app ? 1 : 0);
        return params;
    }

    private WeiboParameters buildTimeLineWithAppTrim(long since_id, long max_id, int count, int page, boolean base_app,
            boolean trim_user, int featureType) {
        WeiboParameters params = buildTimeLineWithApp(since_id, max_id, count, page, base_app, featureType);
        params.put("trim_user", trim_user ? 1 : 0);
        return params;
    }

    private WeiboParameters buildHotParams(int count, boolean base_app) {
        WeiboParameters params = new WeiboParameters();
        params.put("count", count);
        params.put("base_app", base_app ? 1 : 0);
        return params;
    }

    // 缁勮寰崥璇锋眰鍙傛暟
    private WeiboParameters buildUpdateParams(String content, String lat, String lon) {
        WeiboParameters params = new WeiboParameters();
        params.put("status", content);
        if (!TextUtils.isEmpty(lon)) {
            params.put("long", lon);
        }
        if (!TextUtils.isEmpty(lat)) {
            params.put("lat", lat);
        }
        return params;
    }

}
