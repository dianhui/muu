/*
 * 鏂囦欢鍚嶏紙鍙�锛夛紝濡�CodingRuler.java
 * 
 * 鐗堟湰淇℃伅锛堝彲閫夛級锛屽锛欯version 1.0.0
 * 
 * 鐗堟潈鐢虫槑锛堝紑婧愪唬鐮佷竴鑸兘闇�娣诲姞锛夛紝濡傦細Copyright (C) 2010-2013 SINA Corporation.
 */

package com.sina.weibo.sdk.codestyle;

/**
 * 绫荤殑澶т綋鎻忚堪鏀惧湪杩欓噷銆� * 
 * <p>
 * <b>NOTE锛氫互涓嬮儴鍒嗕负涓�釜绠�鐨勭紪鐮佽鑼冿紝鏇村瑙勮寖璇峰弬鑰�ORACLE 瀹樻柟鏂囨。銆�/b><br>
 * 鍦板潃锛歨ttp://www.oracle.com/technetwork/java/codeconventions-150003.pdf<br>
 * 鍙﹀锛岃浣跨敤 UTF-8 鏍煎紡鏉ユ煡鐪嬩唬鐮侊紝閬垮厤鍑虹幇涓枃涔辩爜銆�br>
 * <b>鑷充簬娉ㄩ噴搴旇浣跨敤涓枃杩樻槸鑻辨枃锛岃鑷繁琛屽喅瀹氾紝鏍规嵁鍏徃鎴栭」鐩殑瑕佹眰鑰屽畾锛屾帹鑽愪娇鐢ㄨ嫳鏂囥�</b><br>
 * </p>
 * <h3>1. 鏁寸悊浠ｇ爜</h3>
 * <ul>
 *    <li>1.1. Java 浠ｇ爜涓笉鍏佽鍑虹幇鍦ㄨ鍛婏紝鏃犳硶娑堥櫎鐨勮鍛婅鐢�@SuppressWarnings銆� *    <li>1.2. 鍘绘帀鏃犵敤鐨勫寘銆佹柟娉曘�鍙橀噺绛夛紝鍑忓皯鍍靛案浠ｇ爜銆� *    <li>1.3. 浣跨敤 Lint 宸ュ叿鏉ユ煡鐪嬪苟娑堥櫎璀﹀憡鍜岄敊璇�
 *    <li>1.4. 浣跨敤 Ctrl+Shift+F 鏉ユ牸寮忓寲浠ｇ爜锛岀劧鍚庡啀杩涜璋冩暣銆� *    <li>1.5. 浣跨敤 Ctrl+Shift+O 鏉ユ牸寮忓寲 Import 鍖呫�
 * </ul>
 * 
 * <h3>2. 鍛藉悕瑙勫垯</h3>
 *    <h3>2.1. 鍩烘湰鍘熷垯</h3>
 *    <ul>
 *         <li>2.1.1. 鍙橀噺锛屾柟娉曪紝绫诲懡鍚嶈琛ㄤ箟锛屼弗鏍肩姝娇鐢�name1, name2 绛夊懡鍚嶃�
 *         <li>2.1.2. 鍛藉悕涓嶈兘澶暱锛岄�褰撲娇鐢ㄧ畝鍐欐垨缂╁啓銆傦紙鏈�ソ涓嶈瓒呰繃 25 涓瓧姣嶏級
 *         <li>2.1.3. 鏂规硶鍚嶄互灏忓啓瀛楁瘝寮�锛屼互鍚庢瘡涓崟璇嶉瀛楁瘝澶у啓銆� *         <li>2.1.4. 閬垮厤浣跨敤鐩镐技鎴栬�浠呭湪澶у皬鍐欎笂鏈夊尯鍒殑鍚嶅瓧銆� *         <li>2.1.5. 閬垮厤浣跨敤鏁板瓧锛屼絾鍙敤 2 浠ｆ浛 to锛岀敤 4 浠ｆ浛 for 绛夛紝濡�go2Clean銆� *     </ul>
 *    
 *    <h3>2.2. 绫汇�鎺ュ彛</h3>
 *    <ul>
 *         <li>2.2.1. 鎵�湁鍗曡瘝棣栧瓧姣嶉兘澶у啓銆備娇鐢ㄨ兘纭垏鍙嶅簲璇ョ被銆佹帴鍙ｅ惈涔夈�鍔熻兘绛夌殑璇嶃�涓�埇閲囩敤鍚嶈瘝銆� *         <li>2.2.2. 鎺ュ彛甯�I 鍓嶇紑锛屾垨able, ible, er绛夊悗缂��濡侷Seriable銆� *    </ul>
 *    
 *    <h3>2.3. 瀛楁銆佸父閲�/h3>
 *    <ul>
 *         <li>2.3.1. 鎴愬憳鍙橀噺浠�m 寮�ご锛岄潤鎬佸彉閲忎互 s 寮�ご锛屽 mUserName, sInstance銆� *         <li>2.3.2. 甯搁噺鍏ㄩ儴澶у啓锛屽湪璇嶄笌璇嶄箣鍓嶇敤涓嬪垝绾胯繛鎺ワ紝濡�MAX_NUMBER銆� *         <li>2.3.3. 浠ｇ爜涓姝娇鐢ㄧ‖缂栫爜锛屾妸涓�簺鏁板瓧鎴栧瓧绗︿覆瀹氫箟鎴愬父鐢ㄩ噺銆� *         <li>2.3.4. 瀵逛簬搴熷純涓嶇敤鐨勫嚱鏁帮紝涓轰簡淇濇寔鍏煎鎬э紝閫氬父娣诲姞 @Deprecated锛屽 {@link #doSomething()}
 *    </ul>
 *         
 * <h3>3. 娉ㄩ噴</h3>
 *    璇峰弬鑰�{@link #SampleCode} 绫荤殑娉ㄩ噴銆� *    <ul>
 *    <li>3.1. 甯搁噺娉ㄩ噴锛屽弬瑙�{@link #ACTION_MAIN} 
 *    <li>3.2. 鍙橀噺娉ㄩ噴锛屽弬瑙�{@link #mObject0} 
 *    <li>3.3. 鍑芥暟娉ㄩ噴锛屽弬瑙�{@link #doSomething(int, float, String)}
 *    </ul> 
 *    
 * <h3>4. Class 鍐呴儴椤哄簭鍜岄�杈�/h3>
 * <ul>
 *    <li>4.1. 姣忎釜 class 閮藉簲璇ユ寜鐓т竴瀹氱殑閫昏緫缁撴瀯鏉ユ帓鍒楀熀鎴愬憳鍙橀噺銆佹柟娉曘�鍐呴儴绫荤瓑锛� *             浠庤�杈惧埌鑹ソ鐨勫彲璇绘�銆� *    <li>4.2. 鎬讳綋涓婃潵璇达紝瑕佹寜鐓у厛 public, 鍚�protected, 鏈�悗 private, 鍑芥暟鐨勬帓甯� *             涔熷簲璇ユ湁涓�釜閫昏緫鐨勫厛鍚庨『搴忥紝鐢遍噸鍒拌交銆� *    <li>4.3. 浠ヤ笅椤哄簭鍙緵鍙傝�锛�br>
 *         瀹氫箟TAG锛屼竴鑸负 private锛堝彲閫夛級<br>
 *         瀹氫箟 public 甯搁噺<br>
 *         瀹氫箟 protected 甯搁噺銆佸唴閮ㄧ被<br>
 *         瀹氫箟 private 鍙橀噺<br>
 *         瀹氫箟 public 鏂规硶<br>
 *         瀹氫箟 protected 鏂规硶<br>
 *         瀹氫箟 private 鏂规硶<br>
 * </ul>        
 * 
 * <h3>5. 琛ㄨ揪寮忎笌璇彞</h3>
 *    <h3>5.1. 鍩烘湰鍘熷垯锛氶噰鐢ㄧ揣鍑戝瀷椋庢牸鏉ョ紪鍐欎唬鐮�/h3>
 *    <h3>5.2. 缁嗗垯</h3>
 *    <ul>
 *         <li>5.2.1. 鏉′欢琛ㄧず寮忥紝鍙傝 {@link #conditionFun(boolean)} 
 *         <li>5.2.2. switch 璇彞锛屽弬瑙�{@link #switchFun(int)}
 *         <li>5.2.3. 寰幆璇彞锛屽弬瑙�{@link #circulationFun(boolean)}
 *         <li>5.2.4. 閿欒涓庡紓甯革紝鍙傝 {@link #exceptionFun()}
 *         <li>5.2.5. 鏉傞」锛屽弬瑙�{@link #otherFun()}
 *         <li>5.2.6. 鎵规敞锛屽弬瑙�{@link #doSomething(int, float, String)}
 *     </ul>
 * 
 * @author 浣滆�鍚� * @since 2013-XX-XX
 */
@SuppressWarnings("unused")
public class CodingRuler {

    /** 鍏湁鐨勫父閲忔敞閲�*/
    public static final String ACTION_MAIN = "android.intent.action.MAIN";
    
    /** 绉佹湁鐨勫父閲忔敞閲婏紙鍚岀被鍨嬬殑甯搁噺鍙互鍒嗗潡骞剁揣鍑戝畾涔夛級 */
    private static final int MSG_AUTH_NONE    = 0;
    private static final int MSG_AUTH_SUCCESS = 1;
    private static final int MSG_AUTH_FAILED  = 2;
    
    /** 淇濇姢鐨勬垚鍛樺彉閲忔敞閲�*/
    protected Object mObject0;
    
    /** 绉佹湁鐨勬垚鍛樺彉閲�mObject1 娉ㄩ噴锛堝悓绫诲瀷鐨勬垚鍛樺彉閲忓彲浠ュ垎鍧楀苟绱у噾瀹氫箟锛�*/
    private Object mObject1;
    /** 绉佹湁鐨勬垚鍛樺彉閲�mObject2 娉ㄩ噴 */
    private Object mObject2;
    /** 绉佹湁鐨勬垚鍛樺彉閲�mObject3 娉ㄩ噴 */
    private Object mObject3;
    
    /**
     * 瀵逛簬娉ㄩ噴澶氫簬涓�鐨勶紝閲囩敤杩欑鏂瑰紡鏉�     * 瀹氫箟璇ュ彉閲�     */
    private Object mObject4;

    /**
     * 鍏湁鏂规硶鎻忚堪...
     * 
     * @param param1  鍙傛暟1鎻忚堪...
     * @param param2  鍙傛暟2鎻忚堪...
     * @param paramXX 鍙傛暟XX鎻忚堪... 锛堟敞鎰忥細璇峰皢鍙傛暟銆佹弿杩伴兘瀵归綈锛�     */
    public void doSomething(int param1, float param2, String paramXX) {
        // 浠ヤ笅娉ㄩ噴鏍囩鍙互閫氳繃Eclipse鍐呯疆鐨凾ask鎻掍欢鐪嬪埌
        // TODO  浣跨敤TODO鏉ユ爣璁颁唬鐮侊紝璇存槑鏍囪瘑澶勬湁鍔熻兘浠ｇ爜寰呯紪鍐�        // FIXME 浣跨敤FIXME鏉ユ爣璁颁唬鐮侊紝璇存槑鏍囪瘑澶勪唬鐮侀渶瑕佷慨姝ｏ紝鐢氳嚦浠ｇ爜鏄�        //       閿欒鐨勶紝涓嶈兘宸ヤ綔锛岄渶瑕佷慨澶�        // XXX   浣跨敤XXX鏉ユ爣璁颁唬鐮侊紝璇存槑鏍囪瘑澶勪唬鐮佽櫧鐒跺疄鐜颁簡鍔熻兘锛屼絾鏄疄鐜�        //       鐨勬柟娉曟湁寰呭晢姒凤紝甯屾湜灏嗘潵鑳芥敼杩�    }
    }
    /**
     * 淇濇姢鏂规硶鎻忚堪...
     */
    @Deprecated
    protected void doSomething() {
        // ...implementation
    }
    
    /**
     * 绉佹湁鏂规硶鎻忚堪...
     * 
     * @param param1  鍙傛暟1鎻忚堪...
     * @param param2  鍙傛暟2鎻忚堪...
     */
    private void doSomethingInternal(int param1, float param2) {
        // ...implementation        
    }
    
    /**
     * 鏉′欢琛ㄨ揪寮忓師鍒欍�
     */
    private void conditionFun() {
        boolean condition1 = true;
        boolean condition2 = false;
        boolean condition3 = false;
        boolean condition4 = false;
        boolean condition5 = false;
        boolean condition6 = false;
        
        // 鍘熷垯锛�1. 鎵�湁 if 璇彞蹇呴』鐢�{} 鍖呮嫭璧锋潵锛屽嵆渚垮彧鏈変竴鍙ワ紝绂佹浣跨敤涓嶅甫{}鐨勮鍙�        //       2. 鍦ㄥ惈鏈夊绉嶈繍绠楃鐨勮〃杈惧紡涓紝浣跨敤鍦嗘嫭鍙锋潵閬垮厤杩愮畻绗︿紭鍏堢骇闂
        //       3. 鍒ゆ柇鏉′欢寰堝鏃讹紝璇峰皢鍏跺畠鏉′欢鎹㈣
        if (condition1) {
            // ...implementation
        }
        
        if (condition1) {
            // ...implementation
        } else {
            // ...implementation
        }
        
        if (condition1)          /* 绂佹浣跨敤涓嶅甫{}鐨勮鍙�*/
            condition3 = true;
        
        if ((condition1 == condition2) 
                || (condition3 == condition4)
                || (condition5 == condition6)) {
            
        }
    }
    
    /**
     * Switch璇彞鍘熷垯銆�     */
    private void switchFun() {
        
        // 鍘熷垯锛�1. switch 璇彞涓紝break 涓庝笅涓�潯 case 涔嬮棿锛岀┖涓�
        //       2. 瀵逛簬涓嶉渶瑕�break 璇彞鐨勶紝璇蜂娇鐢�/* Falls through */鏉ユ爣娉�        //       3. 璇烽粯璁ゅ啓涓�default 璇彞锛屼繚鎸佸畬鏁存�
        int code = MSG_AUTH_SUCCESS;
        switch (code) {
        case MSG_AUTH_SUCCESS:
            break;
            
        case MSG_AUTH_FAILED:
            break;
            
        case MSG_AUTH_NONE:
            /* Falls through */
        default:
            break;
        }
    }
    
    /**
     * 寰幆琛ㄨ揪寮忋�
     */
    private void circulationFun() {
        
        // 鍘熷垯锛�1. 灏介噺浣跨敤for each璇彞浠ｆ浛鍘熷鐨刦or璇彞
        //       2. 寰幆涓繀椤绘湁缁堟寰幆鐨勬潯浠舵垨璇彞锛岄伩鍏嶆寰幆
        //       3. 寰幆瑕佸敖鍙兘鐨勭煭, 鎶婇暱寰幆鐨勫唴瀹规娊鍙栧埌鏂规硶涓幓
        //       4. 宓屽灞傛暟涓嶅簲瓒呰繃3灞� 瑕佽寰幆娓呮櫚鍙
        
        int array[] = { 1, 2, 3, 4, 5 };
        for (int data : array) {
            // ...implementation
        }
        
        int length = array.length;
        for (int ix = 0; ix < length; ix++) {
            // ...implementation
        }
        
        boolean condition = true;
        while (condition) {
            // ...implementation
        }
        
        do {
            // ...implementation
        } while (condition);
    }
    
    /**
     * 寮傚父鎹曡幏鍘熷垯銆�     */
    private void exceptionFun() {
        
        // 鍘熷垯锛�1. 鎹曟崏寮傚父鏄负浜嗗鐞嗗畠锛岄�甯稿湪寮傚父catch鍧椾腑杈撳嚭寮傚父淇℃伅銆�        //       2. 璧勬簮閲婃斁鐨勫伐浣滐紝鍙互鏀惧埌 finally 鍧楅儴鍒嗗幓鍋氥�濡傚叧闂�Cursor 绛夈�
        try {
            // ...implementation
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            
        }
    }
    
    /**
     * 鍏跺畠鍘熷垯锛堟暣鐞嗕腑...锛夈�
     */
    private void otherFun() {
        // TODO
    }    
}
