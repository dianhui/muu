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

package com.sina.weibo.sdk.openapi;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.SparseArray;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;

/**
 * 璇ョ被灏佽浜嗗井鍗氭帴鍙ｃ�
 * 璇︽儏璇峰弬鑰�a href="http://t.cn/8F3e7SE">寰崥鎺ュ彛</a>
 * 
 * @author SINA
 * @since 2014-03-03
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
    
    /** 鍘熷垱绛涢�绫诲瀷锛�锛氬叏閮ㄥ井鍗氥�1锛氬師鍒涚殑寰崥銆� */
    public static final int TYPE_FILTER_ALL     = 0;
    public static final int TYPE_FILTER_ORIGAL  = 1;    

    /** API URL */
    private static final String API_BASE_URL = API_SERVER + "/statuses";

    /**
     * API 绫诲瀷銆�     * 鍛藉悕瑙勫垯锛�     *      <li>璇诲彇鎺ュ彛锛歊EAD_API_XXX
     *      <li>鍐欏叆鎺ュ彛锛歐RITE_API_XXX
     * 璇锋敞鎰忥細璇ョ被涓殑鎺ュ彛浠呭仛涓烘紨绀轰娇鐢紝骞舵病鏈夊寘鍚墍鏈夊叧浜庡井鍗氱殑鎺ュ彛锛岀涓夋柟寮�彂鑰呭彲浠�     * 鏍规嵁闇�鏉ュ～鍏呰绫伙紝鍙弬鑰僱egacy鍖呬笅 {@link com.sina.weibo.sdk.openapi.legacy.StatusesAPI}
     */
    private static final int READ_API_FRIENDS_TIMELINE = 0;
    private static final int READ_API_MENTIONS         = 1;    
    private static final int WRITE_API_UPDATE          = 2;
    private static final int WRITE_API_REPOST          = 3;
    private static final int WRITE_API_UPLOAD          = 4;
    private static final int WRITE_API_UPLOAD_URL_TEXT = 5;

    private static final SparseArray<String> sAPIList = new SparseArray<String>();
    static {
        sAPIList.put(READ_API_FRIENDS_TIMELINE, API_BASE_URL + "/friends_timeline.json");
        sAPIList.put(READ_API_MENTIONS,         API_BASE_URL + "/mentions.json");
        sAPIList.put(WRITE_API_REPOST,          API_BASE_URL + "/repost.json");
        sAPIList.put(WRITE_API_UPDATE,          API_BASE_URL + "/update.json");
        sAPIList.put(WRITE_API_UPLOAD,          API_BASE_URL + "/upload.json");
        sAPIList.put(WRITE_API_UPLOAD_URL_TEXT, API_BASE_URL + "/upload_url_text.json");
    }

    /**
     * 鏋勯�鍑芥暟锛屼娇鐢ㄥ悇涓�API 鎺ュ彛鎻愪緵鐨勬湇鍔″墠蹇呴』鍏堣幏鍙�Token銆�     * 
     * @param accesssToken 璁块棶浠ょ墝
     */
    public StatusesAPI(Oauth2AccessToken accessToken) {
        super(accessToken);
    }
    
    /**
     * 鑾峰彇褰撳墠鐧诲綍鐢ㄦ埛鍙婂叾鎵�叧娉ㄧ敤鎴风殑鏈�柊寰崥銆�     * 
     * @param since_id    鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID姣攕ince_id澶х殑寰崥锛堝嵆姣攕ince_id鏃堕棿鏅氱殑寰崥锛夛紝榛樿涓�
     * @param max_id      鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID灏忎簬鎴栫瓑浜巑ax_id鐨勫井鍗氾紝榛樿涓�銆�     * @param count       鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0銆�     * @param page        杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�銆�     * @param base_app    鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse銆�     * @param featureType 杩囨护绫诲瀷ID锛�锛氬叏閮ㄣ�1锛氬師鍒涖�2锛氬浘鐗囥�3锛氳棰戙�4锛氶煶涔愶紝榛樿涓�銆�     *                    <li>{@link #FEATURE_ALL}
     *                    <li>{@link #FEATURE_ORIGINAL}
     *                    <li>{@link #FEATURE_PICTURE}
     *                    <li>{@link #FEATURE_VIDEO}
     *                    <li>{@link #FEATURE_MUSICE}
     * @param trim_user   杩斿洖鍊间腑user瀛楁寮�叧锛宖alse锛氳繑鍥炲畬鏁磚ser瀛楁銆乼rue锛歶ser瀛楁浠呰繑鍥瀠ser_id锛岄粯璁や负false銆�     * @param listener    寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void friendsTimeline(long since_id, long max_id, int count, int page, boolean base_app,
            int featureType, boolean trim_user, RequestListener listener) {
        WeiboParameters params = 
                buildTimeLineParamsBase(since_id, max_id, count, page, base_app, trim_user, featureType);
        requestAsync(sAPIList.get(READ_API_FRIENDS_TIMELINE), params, HTTPMETHOD_GET, listener);
    }    
    
    /**
     * 鑾峰彇鏈�柊鐨勬彁鍒扮櫥褰曠敤鎴风殑寰崥鍒楄〃锛屽嵆@鎴戠殑寰崥銆�     * 
     * @param since_id      鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID姣攕ince_id澶х殑寰崥锛堝嵆姣攕ince_id鏃堕棿鏅氱殑寰崥锛夛紝榛樿涓�銆�     * @param max_id        鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID灏忎簬鎴栫瓑浜巑ax_id鐨勫井鍗氾紝榛樿涓�銆�     * @param count         鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0銆�     * @param page          杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�銆�     * @param authorType    浣滆�绛涢�绫诲瀷锛�锛氬叏閮ㄣ�1锛氭垜鍏虫敞鐨勪汉銆�锛氶檶鐢熶汉 ,榛樿涓�銆傚彲涓轰互涓嬪嚑绉�:
     *                      <li>{@link #AUTHOR_FILTER_ALL}
     *                      <li>{@link #AUTHOR_FILTER_ATTENTIONS}
     *                      <li>{@link #AUTHOR_FILTER_STRANGER}
     * @param sourceType    鏉ユ簮绛涢�绫诲瀷锛�锛氬叏閮ㄣ�1锛氭潵鑷井鍗氱殑璇勮銆�锛氭潵鑷井缇ょ殑璇勮锛岄粯璁や负0銆傚彲涓轰互涓嬪嚑绉�:
     *                      <li>{@link #SRC_FILTER_ALL}
     *                      <li>{@link #SRC_FILTER_WEIBO}
     *                      <li>{@link #SRC_FILTER_WEIQUN}
     * @param filterType    鍘熷垱绛涢�绫诲瀷锛�锛氬叏閮ㄥ井鍗氥�1锛氬師鍒涚殑寰崥锛岄粯璁や负0銆�鍙负浠ヤ笅鍑犵 :
     *                      <li>{@link #TYPE_FILTER_ALL}
     *                      <li>{@link #TYPE_FILTER_ORIGAL}
     * @param trim_user     杩斿洖鍊间腑user瀛楁寮�叧锛宖alse锛氳繑鍥炲畬鏁磚ser瀛楁銆乼rue锛歶ser瀛楁浠呰繑鍥瀠ser_id锛岄粯璁や负false
     * @param listener      寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void mentions(long since_id, long max_id, int count, int page, int authorType, int sourceType,
            int filterType, boolean trim_user, RequestListener listener) {
        WeiboParameters params = buildMentionsParams(since_id, max_id, count, page, authorType, sourceType, filterType, trim_user);
        requestAsync(sAPIList.get(READ_API_MENTIONS), params, HTTPMETHOD_GET, listener);
    }
    
    /**
     * 鍙戝竷涓�潯鏂板井鍗氾紙杩炵画涓ゆ鍙戝竷鐨勫井鍗氫笉鍙互閲嶅锛夈�
     * 
     * @param content  瑕佸彂甯冪殑寰崥鏂囨湰鍐呭锛屽唴瀹逛笉瓒呰繃140涓眽瀛椼�
     * @param lat      绾害锛屾湁鏁堣寖鍥达細-90.0鍒�90.0锛�琛ㄧず鍖楃含锛岄粯璁や负0.0銆�     * @param lon      缁忓害锛屾湁鏁堣寖鍥达細-180.0鍒�180.0锛�琛ㄧず涓滅粡锛岄粯璁や负0.0銆�     * @param listener 寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void update(String content, String lat, String lon, RequestListener listener) {
        WeiboParameters params = buildUpdateParams(content, lat, lon);
        requestAsync(sAPIList.get(WRITE_API_UPDATE), params, HTTPMETHOD_POST, listener);
    }
    
    /**
     * 涓婁紶鍥剧墖骞跺彂甯冧竴鏉℃柊寰崥銆�     * 
     * @param content  瑕佸彂甯冪殑寰崥鏂囨湰鍐呭锛屽唴瀹逛笉瓒呰繃140涓眽瀛�     * @param bitmap   瑕佷笂浼犵殑鍥剧墖锛屼粎鏀寔JPEG銆丟IF銆丳NG鏍煎紡锛屽浘鐗囧ぇ灏忓皬浜�M
     * @param lat      绾害锛屾湁鏁堣寖鍥达細-90.0鍒�90.0锛�琛ㄧず鍖楃含锛岄粯璁や负0.0銆�     * @param lon      缁忓害锛屾湁鏁堣寖鍥达細-180.0鍒�180.0锛�琛ㄧず涓滅粡锛岄粯璁や负0.0銆�     * @param listener 寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void upload(String content, Bitmap bitmap, String lat, String lon, RequestListener listener) {
        WeiboParameters params = buildUpdateParams(content, lat, lon);
        params.put("pic", bitmap);
        requestAsync(sAPIList.get(WRITE_API_UPLOAD), params, HTTPMETHOD_POST, listener);
    }
    
    /**
     * 鎸囧畾涓�釜鍥剧墖URL鍦板潃鎶撳彇鍚庝笂浼犲苟鍚屾椂鍙戝竷涓�潯鏂板井鍗氾紝姝ゆ柟娉曚細澶勭悊URLencod銆�     * 
     * @param status   瑕佸彂甯冪殑寰崥鏂囨湰鍐呭锛屽唴瀹逛笉瓒呰繃140涓眽瀛椼�
     * @param imageUrl 鍥剧墖鐨刄RL鍦板潃锛屽繀椤讳互http寮�ご銆�     * @param pic_id   宸茬粡涓婁紶鐨勫浘鐗噋id锛屽涓椂浣跨敤鑻辨枃鍗婅閫楀彿绗﹀垎闅旓紝鏈�涓嶈秴杩囦節寮犮� 
     *                 imageUrl 鍜�pic_id蹇呴�涓�釜锛屼袱涓弬鏁伴兘瀛樺湪鏃讹紝鍙杙icid鍙傛暟鐨勫�涓哄噯銆�     *                 <b>娉細鐩墠璇ュ弬鏁颁笉鍙敤锛岀幇鍦ㄨ繕鍙兘閫氳繃BD鍚堜綔鎺ュ叆锛屼笉瀵逛釜浜虹敵璇�/b>
     * @param lat      绾害锛屾湁鏁堣寖鍥达細-90.0鍒�90.0锛�琛ㄧず鍖楃含锛岄粯璁や负0.0銆�     * @param lon      缁忓害锛屾湁鏁堣寖鍥达細-180.0鍒�180.0锛�琛ㄧず涓滅粡锛岄粯璁や负0.0銆�     * @param listener 寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void uploadUrlText(String status, String imageUrl, String pic_id, String lat, String lon,
            RequestListener listener) {
        WeiboParameters params = buildUpdateParams(status, lat, lon);
        params.put("url", imageUrl);
        params.put("pic_id", pic_id);
        requestAsync(sAPIList.get(WRITE_API_UPLOAD_URL_TEXT), params, HTTPMETHOD_POST, listener);
    }
    
    /**
     * @see #friendsTimeline(long, long, int, int, boolean, int, boolean, RequestListener)
     */
    public String friendsTimelineSync(long since_id, long max_id, int count, int page, boolean base_app, int featureType,
            boolean trim_user) {
        WeiboParameters params = buildTimeLineParamsBase(since_id, max_id, count, page, base_app,
                trim_user, featureType);
        return requestSync(sAPIList.get(READ_API_FRIENDS_TIMELINE), params, HTTPMETHOD_GET);
    }

    /**
     * -----------------------------------------------------------------------
     * 璇锋敞鎰忥細浠ヤ笅鏂规硶鍖�潎鍚屾鏂规硶銆傚鏋滃紑鍙戣�鏈夎嚜宸辩殑寮傛璇锋眰鏈哄埗锛岃浣跨敤璇ュ嚱鏁般�
     * -----------------------------------------------------------------------
     */
    
    /**
     * @see #mentions(long, long, int, int, int, int, int, boolean, RequestListener)
     */
    public String mentionsSync(long since_id, long max_id, int count, int page,
            int authorType, int sourceType, int filterType, boolean trim_user) {
        WeiboParameters params = buildMentionsParams(since_id, max_id, count, page, authorType, sourceType, filterType, trim_user);
        return requestSync(sAPIList.get(READ_API_MENTIONS), params, HTTPMETHOD_GET);
    }

    /**
     * @see #update(String, String, String, RequestListener)
     */
    public String updateSync(String content, String lat, String lon) {
        WeiboParameters params = buildUpdateParams(content, lat, lon);
        return requestSync(sAPIList.get(WRITE_API_UPDATE), params, HTTPMETHOD_POST);
    }

    /**
     * @see #upload(String, Bitmap, String, String, RequestListener)
     */
    public String uploadSync(String content, Bitmap bitmap, String lat, String lon) {
        WeiboParameters params = buildUpdateParams(content, lat, lon);
        params.put("pic", bitmap);
        return requestSync(sAPIList.get(WRITE_API_UPLOAD), params, HTTPMETHOD_POST);
    }

    /**
     * @see #uploadUrlText(String, String, String, String, String, RequestListener)
     */
    public String uploadUrlTextSync(String status, String imageUrl, String pic_id, String lat, String lon) {
        WeiboParameters params = buildUpdateParams(status, lat, lon);
        params.put("url", imageUrl);
        params.put("pic_id", pic_id);
        return requestSync(sAPIList.get(WRITE_API_UPLOAD_URL_TEXT), params, HTTPMETHOD_POST);
    }

    // 缁勮TimeLines鐨勫弬鏁�    
    private WeiboParameters buildTimeLineParamsBase(long since_id, long max_id, int count, int page,
            boolean base_app, boolean trim_user, int featureType) {
        WeiboParameters params = new WeiboParameters();
        params.put("since_id", since_id);
        params.put("max_id", max_id);
        params.put("count", count);
        params.put("page", page);
        params.put("base_app", base_app ? 1 : 0);
        params.put("trim_user", trim_user ? 1 : 0);
        params.put("feature", featureType);
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
    
    private WeiboParameters buildMentionsParams(long since_id, long max_id, int count, int page,
            int authorType, int sourceType, int filterType, boolean trim_user) {
        WeiboParameters params = new WeiboParameters();
        params.put("since_id", since_id);
        params.put("max_id", max_id);
        params.put("count", count);
        params.put("page", page);
        params.put("filter_by_author", authorType);
        params.put("filter_by_source", sourceType);
        params.put("filter_by_type", filterType);
        params.put("trim_user", trim_user ? 1 : 0);
        
        return params;
    } 
}
