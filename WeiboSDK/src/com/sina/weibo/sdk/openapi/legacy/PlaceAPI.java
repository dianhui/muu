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

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.AbsOpenAPI;

/**
 * 璇ョ被灏佽浜嗗井鍗氱殑浣嶇疆鏈嶅姟鎺ュ彛銆� * 璇︽儏璇峰弬鑰�a href="http://t.cn/8F1s1DJ">寰崥浣嶇疆鏈嶅姟</a>
 * 
 * @author SINA
 * @date 2014-03-03
 */
public class PlaceAPI extends AbsOpenAPI {

    /** 鑾峰彇鍒板懆杈圭殑鎺掑簭鏂瑰紡锛�0锛氭寜鏃堕棿鎺掑簭 , 1锛氭寜涓庝腑蹇冪偣璺濈鎺掑簭銆�*/
    public static final int SORT_BY_TIME     = 0;
    public static final int SORT_BY_DISTENCE = 1;

    /** 鑾峰彇浣嶇疆鐨勬帓搴忔柟寮忥紝 0锛氭寜鏉冮噸锛�锛氭寜璺濈锛�锛氭寜绛惧埌浜烘暟銆�*/
    public static final int NEARBY_POIS_SORT_BY_WEIGHT          = 0;
    public static final int NEARBY_POIS_SORT_BY_DISTENCE        = 1;
    public static final int NEARBY_POIS_SORT_BY_CHECKIN_NUMBER  = 2;

    /** 鍦扮偣鐨勬帓搴忔柟寮忥紝0锛氭寜鏃堕棿銆�锛氭寜鐑棬锛岄粯璁や负0锛岀洰鍓嶅彧鏀寔鎸夋椂闂淬� */
    public static final int POIS_SORT_BY_TIME   = 0;
    public static final int POIS_SORT_BY_HOT    = 1;

    /** 鐢ㄦ埛鍏崇郴杩囨护锛�锛氬叏閮ㄣ�1锛氬彧杩斿洖闄岀敓浜恒�2锛氬彧杩斿洖鍏虫敞浜猴紝榛樿涓�銆�*/
    public static final int RELATIONSHIP_FILTER_ALL         = 0;
    public static final int RELATIONSHIP_FILTER_STRANGER    = 1;
    public static final int RELATIONSHIP_FILTER_FOLLOW      = 2;

    /** 鎬у埆杩囨护锛�锛氬叏閮ㄣ�1锛氱敺銆�锛氬コ锛岄粯璁や负0銆�*/
    public static final int GENDER_ALL   = 0;
    public static final int GENDER_MAN   = 1;
    public static final int GENDER_WOMAM = 2;

    /** 鐢ㄦ埛绾у埆杩囨护锛�锛氬叏閮ㄣ�1锛氭櫘閫氱敤鎴枫�2锛歏IP鐢ㄦ埛銆�锛氳揪浜猴紝榛樿涓�銆�*/
    public static final int USER_LEVEL_ALL    = 0;
    public static final int USER_LEVEL_NORMAL = 1;
    public static final int USER_LEVEL_VIP    = 2;
    public static final int USER_LEVEL_STAR   = 7;

    /** 鍛ㄨ竟鐢ㄦ埛鎺掑簭鏂瑰紡锛�锛氭寜鏃堕棿銆�锛氭寜璺濈銆�锛氭寜绀句細鍖栧叧绯伙紝榛樿涓�銆�*/
    public static final int NEARBY_USER_SORT_BY_TIME        = 0;
    public static final int NEARBY_USER_SORT_BY_DISTANCE    = 1;
    public static final int NEARBY_USER_SORT_BY_SOCIAL_SHIP = 2;

    private static final String SERVER_URL_PRIX = API_SERVER + "/place";

    public PlaceAPI(Oauth2AccessToken accessToken) {
        super(accessToken);
    }

    /**
     * 鑾峰彇鏈�柊20鏉″叕鍏辩殑浣嶇疆鍔ㄦ�銆�     * 
     * @param count     姣忔杩斿洖鐨勫姩鎬佹暟锛屾渶澶т负50锛岄粯璁や负20
     * @param base_app  鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆�涓哄惁锛堟墍鏈夋暟鎹級锛�涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓�
     */
    public void pulicTimeline(long count, boolean base_app, RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        params.put("count", count);
        params.put("base_app", base_app ? 1 : 0);
        requestAsync(SERVER_URL_PRIX + "/public_timelin.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇褰撳墠鐧诲綍鐢ㄦ埛涓庡叾濂藉弸鐨勪綅缃姩鎬併�
     * 
     * @param since_id  鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID姣攕ince_id澶х殑寰崥锛堝嵆姣攕ince_id鏃堕棿鏅氱殑寰崥锛夛紝榛樿涓�
     * @param max_id    鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID灏忎簬鎴栫瓑浜巑ax_id鐨勫井鍗氾紝榛樿涓�
     * @param count     鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝鏈�ぇ涓�0锛岄粯璁や负20
     * @param page      杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param only_attentions true锛氫粎杩斿洖鍏虫敞鐨勶紝false锛氳繑鍥炲ソ鍙嬬殑锛岄粯璁や负true
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void friendsTimeline(long since_id, long max_id, int count, int page, boolean only_attentions,
            RequestListener listener) {
        WeiboParameters params = buildTimeLineParamsBase(since_id, max_id, count, page);
        params.put("type", only_attentions ? 1 : 0);
        requestAsync(SERVER_URL_PRIX + "/friends_timeline.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇鏌愪釜鐢ㄦ埛鐨勪綅缃姩鎬併�
     * 
     * @param uid       闇�鏌ヨ鐨勭敤鎴稩D
     * @param since_id  鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID姣攕ince_id澶х殑寰崥锛堝嵆姣攕ince_id鏃堕棿鏅氱殑寰崥锛夛紝榛樿涓�
     * @param max_id    鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID灏忎簬鎴栫瓑浜巑ax_id鐨勫井鍗氾紝榛樿涓�
     * @param count     鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝鏈�ぇ涓�0锛岄粯璁や负20
     * @param page      杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param base_app  鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void userTimeline(long uid, long since_id, long max_id, int count, int page, boolean base_app,
            RequestListener listener) {
        WeiboParameters params = buildTimeLineParamsBase(since_id, max_id, count, page);
        params.put("uid", uid);
        params.put("base_app", base_app ? 1 : 0);
        requestAsync(SERVER_URL_PRIX + "/user_timeline.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇鏌愪釜浣嶇疆鍦扮偣鐨勫姩鎬併�
     * 
     * @param poiid     闇�鏌ヨ鐨凱OI鐐笽D
     * @param since_id  鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID姣攕ince_id澶х殑寰崥锛堝嵆姣攕ince_id鏃堕棿鏅氱殑寰崥锛夛紝榛樿涓�
     * @param max_id    鑻ユ寚瀹氭鍙傛暟锛屽垯杩斿洖ID灏忎簬鎴栫瓑浜巑ax_id鐨勫井鍗氾紝榛樿涓�
     * @param count     鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝鏈�ぇ涓�0锛岄粯璁や负20
     * @param page      杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param base_app  鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void poiTimeline(String poiid, long since_id, long max_id, int count, int page, boolean base_app,
            RequestListener listener) {
        WeiboParameters params = buildTimeLineParamsBase(since_id, max_id, count, page);
        params.put("poiid", poiid);
        params.put("base_app", base_app ? 1 : 0);
        requestAsync(SERVER_URL_PRIX + "/poi_timeline.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇鏌愪釜浣嶇疆鍛ㄨ竟鐨勫姩鎬併�
     * 
     * @param lat       绾害銆傛湁鏁堣寖鍥达細-90.0鍒�90.0锛�琛ㄧず鍖楃含
     * @param lon       缁忓害銆傛湁鏁堣寖鍥达細-180.0鍒�180.0锛�琛ㄧず涓滅粡
     * @param range     鎼滅储鑼冨洿锛屽崟浣嶇背锛岄粯璁�000绫筹紝鏈�ぇ11132绫�     * @param starttime 寮�鏃堕棿锛孶nix鏃堕棿鎴�     * @param endtime   缁撴潫鏃堕棿锛孶nix鏃堕棿鎴�     * @param sortType  鎺掑簭鏂瑰紡銆�锛氭寜鏃堕棿鎺掑簭锛�1锛氭寜涓庝腑蹇冪偣璺濈杩涜鎺掑簭
     *                  <li>{@link #SORT_BY_TIME}
     *                  <li>{@link #SORT_BY_DISTENCE}
     * @param count     鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝鏈�ぇ涓�0锛岄粯璁や负20
     * @param page      杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param base_app  鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param offset    浼犲叆鐨勭粡绾害鏄惁鏄籂鍋忚繃锛宖alse锛氭病绾犲亸銆乼rue锛氱籂鍋忚繃锛岄粯璁や负false
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void nearbyTimeline(String lat, String lon, int range, long starttime, long endtime, int sortType,
            int count, int page, boolean base_app, boolean offset, RequestListener listener) {
        WeiboParameters params = buildNearbyParams(lat, lon, range, count, page, sortType, offset);
        params.put("starttime", starttime);
        params.put("endtime", endtime);
        params.put("base_app", base_app ? 1 : 0);
        requestAsync(SERVER_URL_PRIX + "/nearby_timeline.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鏍规嵁ID鑾峰彇鍔ㄦ�鐨勮鎯呫�
     * 
     * @param id        闇�鑾峰彇鐨勫姩鎬両D
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void statusesShow(long id, RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        params.put("id", id);
        requestAsync(SERVER_URL_PRIX + "/statuses/show.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇LBS浣嶇疆鏈嶅姟鍐呯殑鐢ㄦ埛淇℃伅銆�     * 
     * @param uid       闇�鏌ヨ鐨勭敤鎴稩D
     * @param base_app  鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void usersShow(long uid, boolean base_app, RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        params.put("uid", uid);
        params.put("base_app", base_app ? 1 : 0);
        requestAsync(SERVER_URL_PRIX + "/users/show.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇鐢ㄦ埛绛惧埌杩囩殑鍦扮偣鍒楄〃銆�     * 
     * @param uid       闇�鏌ヨ鐨勭敤鎴稩D
     * @param count     鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0锛屾渶澶т负50
     * @param page      杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param base_app  鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void usersCheckins(long uid, int count, int page, boolean base_app, RequestListener listener) {
        WeiboParameters params = buildUserParams(uid, count, page, base_app);
        requestAsync(SERVER_URL_PRIX + "/users/checkins.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇鐢ㄦ埛鐨勭収鐗囧垪琛ㄣ�
     * 
     * @param uid       闇�鏌ヨ鐨勭敤鎴稩D
     * @param count     鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0锛屾渶澶т负50
     * @param page      杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param base_app  鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void usersPhotos(long uid, int count, int page, boolean base_app, RequestListener listener) {
        WeiboParameters params = buildUserParams(uid, count, page, base_app);
        requestAsync(SERVER_URL_PRIX + "/users/photos.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇鐢ㄦ埛鐨勭偣璇勫垪琛ㄣ�
     * 
     * @param uid       闇�鏌ヨ鐨勭敤鎴稩D
     * @param count     鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0锛屾渶澶т负50
     * @param page      杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param base_app  鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void usersTips(long uid, int count, int page, boolean base_app, RequestListener listener) {
        WeiboParameters params = buildUserParams(uid, count, page, base_app);
        requestAsync(SERVER_URL_PRIX + "/users/tips.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇鐢ㄦ埛鐨則odo鍒楄〃銆�     * 
     * @param uid       闇�鏌ヨ鐨勭敤鎴稩D
     * @param count     鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0锛屾渶澶т负50
     * @param page      杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param base_app  鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void usersTodo(long uid, int count, int page, boolean base_app, RequestListener listener) {
        WeiboParameters params = buildUserParams(uid, count, page, base_app);
        requestAsync(SERVER_URL_PRIX + "/users/todos.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇鍦扮偣璇︽儏銆�     * 
     * @param poiid     闇�鏌ヨ鐨凱OI鍦扮偣ID
     * @param base_app  鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void poisShow(String poiid, boolean base_app, RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        params.put("poiid", poiid);
        params.put("base_app", base_app ? 1 : 0);
        requestAsync(SERVER_URL_PRIX + "/pois/show.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇鍦ㄦ煇涓湴鐐圭鍒扮殑浜虹殑鍒楄〃銆�     * 
     * @param poiid     闇�鏌ヨ鐨凱OI鍦扮偣ID
     * @param count     鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0锛屾渶澶т负50
     * @param page      杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param base_app  鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void poisUsers(String poiid, int count, int page, boolean base_app, RequestListener listener) {
        WeiboParameters params = buildPoisParams(poiid, count, page, base_app);
        requestAsync(SERVER_URL_PRIX + "/pois/users.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇鍦扮偣鐓х墖鍒楄〃銆�     * 
     * @param poiid     闇�鏌ヨ鐨凱OI鍦扮偣ID
     * @param count     鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0锛屾渶澶т负50
     * @param page      杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param sortType  鎺掑簭鏂瑰紡锛�锛氭寜鏃堕棿銆�锛氭寜鐑棬锛岄粯璁や负0锛岀洰鍓嶅彧鏀寔鎸夋椂闂淬�
     *                  <li>{@link #POIS_SORT_BY_TIME}
     *                  <li>{@link #POIS_SORT_BY_HOT}
     * @param base_app  鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void poisPhotos(String poiid, int count, int page, int sortType, boolean base_app, RequestListener listener) {
        WeiboParameters params = buildPoisParams(poiid, count, page, base_app);
        params.put("sort", sortType);
        requestAsync(SERVER_URL_PRIX + "/pois/photos.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇鍦扮偣鐐硅瘎鍒楄〃銆�     * 
     * @param poiid     闇�鏌ヨ鐨凱OI鍦扮偣ID
     * @param count     鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0锛屾渶澶т负50
     * @param page      杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param sortType  鎺掑簭鏂瑰紡锛�锛氭寜鏃堕棿銆�锛氭寜鐑棬锛岄粯璁や负0锛岀洰鍓嶅彧鏀寔鎸夋椂闂淬�
     *                  <li>{@link #POIS_SORT_BY_TIME}
     *                  <li>{@link #POIS_SORT_BY_HOT}
     * @param base_app  鏄惁鍙幏鍙栧綋鍓嶅簲鐢ㄧ殑鏁版嵁銆俧alse涓哄惁锛堟墍鏈夋暟鎹級锛宼rue涓烘槸锛堜粎褰撳墠搴旂敤锛夛紝榛樿涓篺alse
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void poisTips(String poiid, int count, int page, int sortType, boolean base_app, RequestListener listener) {
        WeiboParameters params = buildPoisParams(poiid, count, page, base_app);
        params.put("sort", sortType);
        requestAsync(SERVER_URL_PRIX + "/pois/tips.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鎸夌渷甯傛煡璇㈠湴鐐广�
     * 
     * @param keyword   鏌ヨ鐨勫叧閿瘝
     * @param city      鍩庡競浠ｇ爜锛岄粯璁や负鍏ㄥ浗鎼滅储
     * @param category  鏌ヨ鐨勫垎绫讳唬鐮侊紝鍙栧�鑼冨洿瑙侊細鍒嗙被浠ｇ爜瀵瑰簲琛�     * @param count     鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0锛屾渶澶т负50
     * @param page      杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�銆�     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void poisSearch(String keyword, String city, String category, int count, int page, RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        params.put("keyword", keyword);
        params.put("city", city);
        params.put("category", category);
        params.put("count", count);
        params.put("page", page);
        requestAsync(SERVER_URL_PRIX + "/pois/search.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇鍦扮偣鍒嗙被銆�     * 
     * @param pid       鐖跺垎绫籌D锛岄粯璁や负0
     * @param returnALL 鏄惁杩斿洖鍏ㄩ儴鍒嗙被锛宖alse锛氬彧杩斿洖鏈骇涓嬬殑鍒嗙被锛宼rue锛氳繑鍥炲叏閮ㄥ垎绫伙紝榛樿涓篺alse
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void poisCategory(int pid, boolean returnALL, RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        params.put("pid", pid);
        params.put("flag", returnALL ? 1 : 0);
        requestAsync(SERVER_URL_PRIX + "/pois/category.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇闄勮繎鍦扮偣銆�     * 
     * @param lat       绾害锛屾湁鏁堣寖鍥达細-90.0鍒�90.0锛�琛ㄧず鍖楃含
     * @param lon       缁忓害锛屾湁鏁堣寖鍥达細-180.0鍒�180.0锛�琛ㄧず涓滅粡
     * @param range     鏌ヨ鑼冨洿鍗婂緞锛岄粯璁や负2000锛屾渶澶т负10000锛屽崟浣嶇背
     * @param q         鏌ヨ鐨勫叧閿瘝
     * @param category  鏌ヨ鐨勫垎绫讳唬鐮侊紝鍙栧�鑼冨洿瑙侊細鍒嗙被浠ｇ爜瀵瑰簲琛�     * @param count     鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0锛屾渶澶т负50
     * @param page      杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param sortType  鎺掑簭鏂瑰紡锛�锛氭寜鏉冮噸锛�锛氭寜璺濈锛�锛氭寜绛惧埌浜烘暟銆傞粯璁や负0
     *                  <li>{@link #NEARBY_POIS_SORT_BY_WEIGHT}
     *                  <li>{@link #NEARBY_POIS_SORT_BY_DISTENCE}
     *                  <li>{@link #NEARBY_POIS_SORT_BY_CHECKIN_NUMBER}
     * @param offset    浼犲叆鐨勭粡绾害鏄惁鏄籂鍋忚繃锛宖alse锛氭病绾犲亸銆乼rue锛氱籂鍋忚繃锛岄粯璁や负false
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void nearbyPois(String lat, String lon, int range, String q, String category, int count, int page,
            int sortType, boolean offset, RequestListener listener) {
        WeiboParameters params = buildNearbyParams(lat, lon, range, count, page, sortType, offset);
        params.put("q", q);
        params.put("category", category);
        requestAsync(SERVER_URL_PRIX + "/nearby/pois.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇闄勮繎鍙戜綅缃井鍗氱殑浜恒�
     * 
     * @param lat       绾害锛屾湁鏁堣寖鍥达細-90.0鍒�90.0锛�琛ㄧず鍖楃含
     * @param lon       缁忓害锛屾湁鏁堣寖鍥达細-180.0鍒�180.0锛�琛ㄧず涓滅粡
     * @param range     鏌ヨ鑼冨洿鍗婂緞锛岄粯璁や负2000锛屾渶澶т负11132锛屽崟浣嶇背
     * @param starttime 寮�鏃堕棿锛孶nix鏃堕棿鎴�     * @param endtime   缁撴潫鏃堕棿锛孶nix鏃堕棿鎴�     * @param sortType  鎺掑簭鏂瑰紡锛�锛氭寜鏃堕棿銆�锛氭寜璺濈锛岄粯璁や负0
     *                  <li>{@link #SORT_BY_TIME}
     *                  <li>{@link #SORT_BY_DISTENCE}
     * @param count     鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0锛屾渶澶т负50
     * @param page      杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param offset    浼犲叆鐨勭粡绾害鏄惁鏄籂鍋忚繃锛宖alse锛氭病绾犲亸銆乼rue锛氱籂鍋忚繃锛岄粯璁や负false
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void nearbyUsers(String lat, String lon, int range, long starttime, long endtime, int sortType, int count,
            int page, boolean offset, RequestListener listener) {
        WeiboParameters params = buildNearbyParams(lat, lon, range, count, page, sortType, offset);
        params.put("starttime", starttime);
        params.put("endtime", endtime);
        requestAsync(SERVER_URL_PRIX + "/nearby/users.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇闄勮繎鐓х墖銆�     * 
     * @param lat       绾害锛屾湁鏁堣寖鍥达細-90.0鍒�90.0锛�琛ㄧず鍖楃含
     * @param lon       缁忓害锛屾湁鏁堣寖鍥达細-180.0鍒�180.0锛�琛ㄧず涓滅粡
     * @param range     鏌ヨ鑼冨洿鍗婂緞锛岄粯璁や负2000锛屾渶澶т负11132锛屽崟浣嶇背
     * @param starttime 寮�鏃堕棿锛孶nix鏃堕棿鎴�     * @param endtime   缁撴潫鏃堕棿锛孶nix鏃堕棿鎴�     * @param sortType  鎺掑簭鏂瑰紡锛�锛氭寜鏃堕棿銆�锛氭寜璺濈锛岄粯璁や负0
     *                  <li>{@link #SORT_BY_TIME}
     *                  <li>{@link #SORT_BY_DISTENCE}
     * @param count     鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0锛屾渶澶т负50
     * @param page      杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param offset    浼犲叆鐨勭粡绾害鏄惁鏄籂鍋忚繃锛宖alse锛氭病绾犲亸銆乼rue锛氱籂鍋忚繃锛岄粯璁や负false
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void nearbyPhotos(String lat, String lon, int range, long starttime, long endtime, int sortType, int count,
            int page, boolean offset, RequestListener listener) {
        WeiboParameters params = buildNearbyParams(lat, lon, range, count, page, sortType, offset);
        params.put("starttime", starttime);
        params.put("endtime", endtime);
        requestAsync(SERVER_URL_PRIX + "/nearby/photos.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 鑾峰彇闄勮繎鐨勪汉
     * 
     * @param lat       绾害锛屾湁鏁堣寖鍥达細-90.0鍒�90.0锛�琛ㄧず鍖楃含
     * @param lon       缁忓害锛屾湁鏁堣寖鍥达細-180.0鍒�180.0锛�琛ㄧず涓滅粡
     * @param count     鍗曢〉杩斿洖鐨勮褰曟潯鏁帮紝榛樿涓�0锛屾渶澶т负50
     * @param page      杩斿洖缁撴灉鐨勯〉鐮侊紝榛樿涓�
     * @param range     鏌ヨ鑼冨洿鍗婂緞锛岄粯璁や负2000锛屾渶澶т负11132
     * @param sortType  鎺掑簭鏂瑰紡锛�锛氭寜鏃堕棿銆�锛氭寜璺濈銆�锛氭寜绀句細鍖栧叧绯伙紝榛樿涓�銆�     *                  <li>{@link #NEARBY_USER_SORT_BY_TIME}
     *                  <li>{@link #NEARBY_POIS_SORT_BY_DISTENCE}
     *                  <li>{@link #NEARBY_USER_SORT_BY_SOCIAL_SHIP}
     * @param filterType锛堟殏鏈惎鐢級鐢ㄦ埛鍏崇郴杩囨护锛�锛氬叏閮ㄣ�1锛氬彧杩斿洖闄岀敓浜恒�2锛氬彧杩斿洖鍏虫敞浜猴紝榛樿涓�
     *                  <li>{@link #RELATIONSHIP_FILTER_ALL}
     *                  <li>{@link #RELATIONSHIP_FILTER_STRANGER}
     *                  <li>{@link #RELATIONSHIP_FILTER_FOLLOW}
     * @param genderType鎬у埆杩囨护锛�锛氬叏閮ㄣ�1锛氱敺銆�锛氬コ锛岄粯璁や负0
     *                  <li>{@link #GENDER_ALL}
     *                  <li>{@link #GENDER_MAN}
     *                  <li>{@link #GENDER_WOMAM}
     * @param levelType 鐢ㄦ埛绾у埆杩囨护锛�锛氬叏閮ㄣ�1锛氭櫘閫氱敤鎴枫�2锛歏IP鐢ㄦ埛銆�锛氳揪浜猴紝榛樿涓�
     *                  <li>{@link #USER_LEVEL_ALL}
     *                  <li>{@link #USER_LEVEL_NORMAL}
     *                  <li>{@link #USER_LEVEL_VIP}
     *                  <li>{@link #USER_LEVEL_STAR}
     * @param start_birth 涓庡弬鏁癳ndbirth涓�捣瀹氫箟杩囨护骞撮緞娈碉紝鏁板�涓哄勾榫勫ぇ灏忥紝榛樿涓虹┖
     * @param end_birth 涓庡弬鏁皊tartbirth涓�捣瀹氫箟杩囨护骞撮緞娈碉紝鏁板�涓哄勾榫勫ぇ灏忥紝榛樿涓虹┖
     * @param offset    浼犲叆鐨勭粡绾害鏄惁鏄籂鍋忚繃锛�锛氭病绾犲亸銆�锛氱籂鍋忚繃锛岄粯璁や负0
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void nearbyUserList(String lat, String lon, int count, int page, int range, int sortType, int filterType,
            int genderType, int levelType, int start_birth, int end_birth, boolean offset, RequestListener listener) {
        WeiboParameters params = buildNearbyParams(lat, lon, range, count, page, sortType, offset);
        params.put("filter", filterType);
        params.put("gender", genderType);
        params.put("level", levelType);
        params.put("startbirth", start_birth);
        params.put("endbirth", end_birth);
        requestAsync(SERVER_URL_PRIX + "/nearby_users/list.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 娣诲姞鍦扮偣
     * 
     * @param title     POI鐐圭殑鍚嶇О锛屼笉瓒呰繃30涓瓧绗︼紝蹇呴』杩涜URLencode
     * @param address   POI鐐圭殑鍦板潃锛屼笉瓒呰繃60涓瓧绗︼紝蹇呴』杩涜URLencode
     * @param category  POI鐨勭被鍨嬪垎绫讳唬鐮侊紝鍙栧�鑼冨洿瑙侊細鍒嗙被浠ｇ爜瀵瑰簲琛紝榛樿涓�00
     * @param lat       绾害锛屾湁鏁堣寖鍥达細-90.0鍒�90.0锛�琛ㄧず鍖楃含
     * @param lon       缁忓害锛屾湁鏁堣寖鍥达細-180.0鍒�180.0锛�琛ㄧず涓滅粡
     * @param city      鍩庡競浠ｇ爜
     * @param province  鐪佷唤浠ｇ爜
     * @param country   鍥藉浠ｇ爜
     * @param phone     POI鐐圭殑鐢佃瘽锛屼笉瓒呰繃14涓瓧绗�     * @param postCode  POI鐐圭殑閭紪
     * @param extra     鍏朵粬锛屽繀椤昏繘琛孶RLencode
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void poisCreate(String title, String address, String category, String lat, String lon, String city,
            String province, String country, String phone, String postCode, String extra, RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        params.put("title", title);
        params.put("address", address);
        params.put("category", category);
        params.put("lat", lat);
        params.put("long", lon);
        params.put("city", city);
        params.put("province", province);
        params.put("country", country);
        params.put("phone", phone);
        params.put("postcode", postCode);
        params.put("extra", extra);
        requestAsync(SERVER_URL_PRIX + "/pois/create.json", params, HTTPMETHOD_POST, listener);
    }

    /**
     * 绛惧埌鍚屾椂鍙互涓婁紶涓�紶鍥剧墖銆�     * 
     * @param poiid     闇�绛惧埌鐨凱OI鍦扮偣ID
     * @param status    绛惧埌鏃跺彂甯冪殑鍔ㄦ�鍐呭锛屽唴瀹逛笉瓒呰繃140涓眽瀛�     * @param pic       闇�涓婁紶鐨勫浘鐗囪矾寰勶紝浠呮敮鎸丣PEG銆丟IF銆丳NG鏍煎紡锛屽浘鐗囧ぇ灏忓皬浜�M銆備緥濡傦細/sdcard/pic.jgp锛�娉ㄦ剰锛歱ic涓嶈兘涓虹綉缁滃浘鐗�     * @param isPublic  鏄惁鍚屾鍒板井鍗氾紝榛樿涓轰笉鍚屾
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void poisAddCheckin(String poiid, String status, String pic, boolean isPublic, RequestListener listener) {
        WeiboParameters params = buildPoiis(poiid, status, isPublic);
        params.put("pic", pic);
        requestAsync(SERVER_URL_PRIX + "/pois/add_checkin.json", params, HTTPMETHOD_POST, listener);
    }

    /**
     * 娣诲姞鐓х墖銆�     * 
     * @param poiid     闇�娣诲姞鐓х墖鐨凱OI鍦扮偣ID
     * @param status    绛惧埌鏃跺彂甯冪殑鍔ㄦ�鍐呭锛屽唴瀹逛笉瓒呰繃140涓眽瀛�     * @param pic       闇�涓婁紶鐨勫浘鐗囷紝浠呮敮鎸丣PEG銆丟IF銆丳NG鏍煎紡锛屽浘鐗囧ぇ灏忓皬浜�M銆備緥濡傦細/sdcard/pic.jgp锛�娉ㄦ剰锛�pic涓嶈兘涓虹綉缁滃浘鐗�     * @param isPublic  鏄惁鍚屾鍒板井鍗氾紝榛樿涓轰笉鍚屾
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void poisAddPhoto(String poiid, String status, String pic, boolean isPublic, RequestListener listener) {
        WeiboParameters params = buildPoiis(poiid, status, isPublic);
        params.put("pic", pic);
        requestAsync(SERVER_URL_PRIX + "/pois/add_photo.json", params, HTTPMETHOD_POST, listener);
    }

    /**
     * 娣诲姞鐐硅瘎銆�     * 
     * @param poiid     闇�鐐硅瘎鐨凱OI鍦扮偣ID
     * @param status    鐐硅瘎鏃跺彂甯冪殑鍔ㄦ�鍐呭锛屽唴瀹逛笉瓒呰繃140涓眽瀛�     * @param isPublic  鏄惁鍚屾鍒板井鍗氾紝榛樿涓轰笉鍚屾
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void poisAddTip(String poiid, String status, boolean isPublic, RequestListener listener) {
        WeiboParameters params = buildPoiis(poiid, status, isPublic);
        requestAsync(SERVER_URL_PRIX + "/pois/add_tip.json", params, HTTPMETHOD_POST, listener);
    }

    /**
     * 娣诲姞todo銆�     * 
     * @param poiid     闇�娣诲姞todo鐨凱OI鍦扮偣ID
     * @param status    娣诲姞todo鏃跺彂甯冪殑鍔ㄦ�鍐呭锛屽繀椤诲仛URLencode锛屽唴瀹逛笉瓒呰繃140涓眽瀛�     * @param isPublic  鏄惁鍚屾鍒板井鍗氾紝1锛氭槸銆�锛氬惁锛岄粯璁や负0
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void poisAddTodo(String poiid, String status, boolean isPublic, RequestListener listener) {
        WeiboParameters params = buildPoiis(poiid, status, isPublic);
        requestAsync(SERVER_URL_PRIX + "/pois/add_todo.json", params, HTTPMETHOD_POST, listener);
    }

    /**
     * 鐢ㄦ埛娣诲姞鑷繁鐨勪綅缃�
     * 
     * @param lat       绾害锛屾湁鏁堣寖鍥达細-90.0鍒�90.0锛�琛ㄧず鍖楃含
     * @param lon       缁忓害锛屾湁鏁堣寖鍥达細-180.0鍒�180.0锛�琛ㄧず涓滅粡
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void nearbyUsersCreate(String lat, String lon, RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        params.put("lat", lat);
        params.put("long", lon);
        requestAsync(SERVER_URL_PRIX + "/nearby_users/create.json", params, HTTPMETHOD_POST, listener);
    }

    /**
     * 鐢ㄦ埛鍒犻櫎鑷繁鐨勪綅缃�
     * 
     * @param listener  寮傛璇锋眰鍥炶皟鎺ュ彛
     */
    public void nearbyUsersDestroy(RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        requestAsync(SERVER_URL_PRIX + "/nearby_users/destory.json", params, HTTPMETHOD_POST, listener);
    }

    // 缁勮TimeLines鐨勫弬鏁�    
    private WeiboParameters buildTimeLineParamsBase(long since_id, long max_id, int count, int page) {
        WeiboParameters params = new WeiboParameters();
        params.put("since_id", since_id);
        params.put("max_id", max_id);
        params.put("count", count);
        params.put("page", page);
        return params;
    }

    // 缁勮UserParams鐨勫弬鏁� 
    private WeiboParameters buildUserParams(long uid, int count, int page, boolean base_app) {
        WeiboParameters params = new WeiboParameters();
        params.put("uid", uid);
        params.put("count", count);
        params.put("page", page);
        params.put("base_app", base_app ? 1 : 0);
        return params;
    }

    // 缁勮UserParams鐨勫弬鏁�    
    private WeiboParameters buildNearbyParams(String lat, String lon, int range, int count, int page,
            int sortType, boolean offset) {
        WeiboParameters params = new WeiboParameters();
        params.put("lat", lat);
        params.put("long", lon);
        params.put("range", range);
        params.put("count", count);
        params.put("page", page);
        params.put("sort", sortType);
        params.put("offset", offset ? 1 : 0);
        return params;
    }

    private WeiboParameters buildPoiis(String poiid, String status, boolean isPublic) {
        WeiboParameters params = new WeiboParameters();
        params.put("poiid", poiid);
        params.put("status", status);
        params.put("public", isPublic ? 1 : 0);
        return params;
    }

    private WeiboParameters buildPoisParams(String poiid, int count, int page, boolean base_app) {
        WeiboParameters params = new WeiboParameters();
        params.put("poiid", poiid);
        params.put("base_app", base_app ? 1 : 0);
        params.put("count", count);
        params.put("page", page);
        return params;
    }
}
