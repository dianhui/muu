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

package com.sina.weibo.sdk.widget;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sina.sso.RemoteSSO;
import com.sina.weibo.sdk.R;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.utils.LogUtil;
import com.sina.weibo.sdk.utils.SecurityHelper;

/**
 * 该控件提供了以下功能：
 * 1. 手动获取当前登陆用户昵称；
 * 2. 点击昵称或登陆按钮，进行 SSO 登陆。
 *  
 * @author SINA
 * @since 2013-09-25
 */
public class UserStateLoginView extends FrameLayout implements OnClickListener {

    private static final String TAG = "UserStateLoginView";
    
    /** 远程 Service 名 */
    private static final String WEIBO_REMOTESSOSERVICE_NAME = "com.sina.weibo.remotessoservice";
    /** 启动 SSOActivity，存放在 Intent 中的 Extra Data中的请求码 */
    private static final String EXTRA_REQUEST_CODE = "com.sina.weibo.intent.extra.REQUEST_CODE";
    /** 启动 SSOActivity，获取昵称请求码 */
    private static final int REQUEST_CODE_GET_NICK_NAME = 32974;
    /** SSOActivity 返回后，存放在 Intent 中的 Extra Data */
    private static final String EXTRA_NICK_NAME = "com.sina.weibo.intent.extra.NICK_NAME";

    /** 用户界面状态：加载昵称中、获取昵称成功、获取昵称失败 */
    private static final int UI_STATE_LOADING        = 1;
    private static final int UI_STATE_FETCHE_SUCCESS = 2;
    private static final int UI_STATE_FETCHE_FAILED  = 3;
    
    /** 默认的页边距 */
    private static final int DEFAULT_MARGIN          = 5;
    /** 默认文本字体显示大小 */
    private static final int DEFAULT_TEXT_SIZE       = 16;
    /** 默认登陆按钮宽度为 100*density 个像素 */
    private static final int DEFAULT_BUTTON_WIDTH    = 100;
    
    /** 用户界面元素：进度条、显示昵称的View、登陆按钮 */
    private Context     mContext;
    private ProgressBar mProgressBar;
    private TextView    mNickNameView;
    private Button      mLoginButton;
    
    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */    
    private SsoHandler mSsoHandler;
	private AuthInfo mAuthInfo;
    
    /** 微博授权的回调 Listener */
    private WeiboAuthListener mAuthListener;
    /** 获取当前登陆用户昵称的回调 Listener */
    private IUserInfoListener mUserInfoListener;

    /** 远程Service对象接口：用于异步获取用户信息（如昵称） */
    private RemoteSSO mIRemoteUserState = null;
    /** 远程Service连接对象 */
    private ServiceConnection mConnection = new ServiceConnection() {
        
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIRemoteUserState = RemoteSSO.Stub.asInterface(service);
            LogUtil.i(TAG, "Service has connected");
            
            try {
                // 启动SSOActivity获取昵称
                String packageName = mIRemoteUserState.getPackageName();
                String className = mIRemoteUserState.getActivityName();
                LogUtil.d(TAG, "packageName: " + packageName + ", className: "+ className);
                startSSOActivity(packageName, className);
                
                // Unbind Service
                doUnbindService();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIRemoteUserState = null;
            LogUtil.i(TAG, "Service has unexpectedly disconnected");
        }
    }; 
    
    /**
     * 当该控件自动获取当前登陆用户的昵称后，会通过该Interface告知第三方。
     */
    public interface IUserInfoListener {
        /**
         * 当该控件自动获取当前登陆用户的昵称后，会通过该函数告知第三方。
         * 
         * @param nickName 获取到的用户昵称
         */
        void onUserNickNameRetrieved(String nickName);
    }
    
    /**
     * 构造函数。
     * 
     * @param context  应用程序上下文
     */
    public UserStateLoginView(Context context) {
        this(context, null);
    }
    
    /**
     * 构造函数。
     * 
     * @param context  应用程序上下文
     * @param attrs    该View对应的XML Tag属性
     */
    public UserStateLoginView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    /**
     * 构造函数。
     * 
     * @param context  应用程序上下文
     * @param attrs    该View对应的XML Tag属性
     * @param defStyle 该View对应的Style
     */
    public UserStateLoginView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        initialize(context, attrs);
    }

    /**
     * 设置微博授权所需信息。
     * 
     * @param appKey      应用程序Key
     * @param redirectUrl 授权回调页
     * @param scope       应用程序申请权限
     */
    public void setWeiboAuthInfo(String appKey, String redirectUrl, String scope) {        
        mAuthInfo = new AuthInfo(mContext, appKey, redirectUrl, scope);
        mSsoHandler = new SsoHandler((Activity)mContext, mAuthInfo);
    }
    
    /**
     * 设置微博授权的回调Listener。
     * 
     * @param authListener 微博授权的回调Listener
     */
    public void setAuthListener(WeiboAuthListener authListener) {
        mAuthListener = authListener;
    }
    
    /**
     * 获取当前登陆用户昵称的回调Listener。
     * 
     * @param userInfoListener 当前登陆用户昵称的回调Listener
     */
    public void setUserInfoListener(IUserInfoListener userInfoListener) {
        mUserInfoListener = userInfoListener;
    }
    
    /*public void setNickViewLayout(FrameLayout.LayoutParams params) {
        if (params != null) {
            mNickNameView.setLayoutParams(params);
        }
    }*/
    
    /**
     * 设置昵称View的宽度和高度。默认情况下，宽度和高度都是{@link LayoutParams#WRAP_CONTENT}。
     * 
     * @param width  昵称View的宽度，可以是实际像素值、
     *               {@link LayoutParams#WRAP_CONTENT}或{@link LayoutParams#MATCH_PARENT}
     * @param height 昵称View的高度，可以是实际像素值、
     *               {@link LayoutParams#WRAP_CONTENT}或{@link LayoutParams#MATCH_PARENT}
     */
    public void setNickViewLayout(int width, int height) {
        android.widget.FrameLayout.LayoutParams params = 
                (android.widget.FrameLayout.LayoutParams) mNickNameView.getLayoutParams();
        if (params != null) {
            params.width = width;
            params.height = height;
            mNickNameView.setLayoutParams(params);
        }
    }
    
    /*public void setLoginButtonLayout(FrameLayout.LayoutParams params) {
        if (params != null) {
            mLoginButton.setLayoutParams(params);
        }
    }*/
    
    /**
     * 设置登陆按钮的宽度和高度。默认情况下，宽度 100像素，高度是{@link LayoutParams#WRAP_CONTENT}。
     * 
     * @param width  登陆按钮的宽度，可以是实际像素值、
     *               {@link LayoutParams#WRAP_CONTENT}或{@link LayoutParams#MATCH_PARENT}
     * @param height 登陆按钮的高度，可以是实际像素值、
     *               {@link LayoutParams#WRAP_CONTENT}或{@link LayoutParams#MATCH_PARENT}
     */
    public void setLoginButtonLayout(int width, int height) {
        android.widget.FrameLayout.LayoutParams params = 
                (android.widget.FrameLayout.LayoutParams) mLoginButton.getLayoutParams();
        if (params != null) {
            params.width = width;
            params.height = height;
            mLoginButton.setLayoutParams(params);
        }
    }
    
    /*public void setProgressBayStyle() {
        // TODO: TBD
    }*/
    
    /**
     * 手动设置昵称View的样式。
     * NOTE：目前只支持字体的颜色和大小，其它样式正在准备中。
     * 
     * @param textColor 字体颜色
     * @param textSize  字体大小
     */
    public void setNickViewStyle(int textColor, int textSize) {
        mNickNameView.setTextColor(textColor);
        mNickNameView.setTextSize(textSize);
    }

    /**
     * 手动设置登陆按钮的样式。
     * NOTE：目前只支持设置按钮的背景和Selector，其它样式正在准备中。
     * 
     * @param textColor  字体颜色
     * @param textSize   字体大小
     * @param background 按钮的背景或Selector
     */
    //@SuppressWarnings("deprecation")
    public void setLoginButtonStyle(int textColor, int textSize, Drawable background) {
        //mLoginButton.setBackground(background);
        mLoginButton.setTextColor(textColor);
        mLoginButton.setTextSize(textSize);
        mLoginButton.setBackgroundDrawable(background);
    }
    
    /**
     * 获取当前登陆用户的昵称。
     */
    public void fetchLoginUserNick() {
        doBindService();
    }
    
    /**
     * 使用该控件进行SSO授权时，需要手动调用该函数。
     * <p>
     * 重要：使用该控件的Activity必须重写{@link Activity#onActivityResult(int, int, Intent)}，
     *       并在内部调用该函数，否则无法授权成功。</p>
     * <p>Sample Code：</p>
     * <pre class="prettyprint">
     * protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     *     super.onActivityResult(requestCode, resultCode, data);
     *     
     *     // 在此处调用
     *     mUserStateView.onActivityResult(requestCode, resultCode, data);
     * }
     * </pre>
     * @param requestCode 请查看 {@link Activity#onActivityResult(int, int, Intent)}
     * @param resultCode  请查看 {@link Activity#onActivityResult(int, int, Intent)}
     * @param data        请查看 {@link Activity#onActivityResult(int, int, Intent)}
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        
        LogUtil.d(TAG, "requestCode: " + requestCode + ", resultCode: " + resultCode + ", data: " + data);
        
        switch (requestCode) {
        case REQUEST_CODE_GET_NICK_NAME:
            String nickName = (data != null) ? data.getStringExtra(EXTRA_NICK_NAME) : "";
            LogUtil.i(TAG, "Get the nick name from SSOActivity: " + nickName);
            onFetchCompleted(nickName);
            break;

        default:
            if (mSsoHandler != null) {
                mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
            break;
        }
    }
    
    /**
     * 用户点击登陆按钮和昵称时，进行SSO登陆。
     */
    @Override
    public void onClick(View v) {
        if (mSsoHandler != null) {
            mSsoHandler.authorize(mAuthListener);
        } else {
            LogUtil.e(TAG, "Please setWeiboAuthInfo(...) for first");
        }
    }
 
    /**
     * 初始化控件布局及样式，并启动Service，获取当前登陆用户的昵称。
     * 
     * @param context 应用程序上下文
     * @param attrs   该View对应的XML Tag属性
     */
    private void initialize(Context context, AttributeSet attrs) {
        mContext = context;
        
        // 初始化控件
        initViews(context);
        
        // 解析XML Tag属性，设置控件的样式
        parseAttributes(attrs);
        
        // 启动Service，获取当前登陆用户的昵称
        //doBindService();
    }

    /**
     * 初始化控件布局。
     * 
     * @param context 应用程序上下文
     */
    private void initViews(Context context) {
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        float density = dm.density;
        int defMargin = (int) (DEFAULT_MARGIN * density);
        int defButtonWith = (int) (DEFAULT_BUTTON_WIDTH * density);
        
        // 1. Create ProgressBar and add it into container
        FrameLayout.LayoutParams progressBarlp = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        progressBarlp.gravity = Gravity.CENTER;
        mProgressBar = new ProgressBar(mContext);
        //mProgressBar = new ProgressBar(mContext, null, android.R.attr.progressBarStyleSmall);
        addView(mProgressBar, progressBarlp);
        
        // 2. Create TextView and add it into container
        FrameLayout.LayoutParams nickNamelp = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        nickNamelp.gravity = Gravity.CENTER;
        nickNamelp.setMargins(defMargin, defMargin, defMargin, defMargin);
        mNickNameView = new TextView(mContext);
        mNickNameView.setTextSize(DEFAULT_TEXT_SIZE);
        mNickNameView.setSingleLine(true);
        mNickNameView.setEllipsize(TruncateAt.MIDDLE);
        mNickNameView.setOnClickListener(this);
        mNickNameView.setGravity(Gravity.CENTER);
        mNickNameView.setPadding(defMargin, defMargin, defMargin, defMargin);
        addView(mNickNameView, nickNamelp);
        
        // 3. Create Login Button and add it into container
        FrameLayout.LayoutParams loginButtonlp = new FrameLayout.LayoutParams(
                /*LayoutParams.WRAP_CONTENT*/defButtonWith, LayoutParams.WRAP_CONTENT);
        loginButtonlp.gravity = Gravity.CENTER;
        //loginButtonlp.setMargins(DEFAULT_MARGIN, DEFAULT_MARGIN, DEFAULT_MARGIN, DEFAULT_MARGIN);
        mLoginButton = new Button(context);
        mLoginButton.setTextSize(DEFAULT_TEXT_SIZE);
        mLoginButton.setText(R.string.com_sina_weibo_sdk_login_with_weibo_account);
        mLoginButton.setOnClickListener(this);
        addView(mLoginButton, loginButtonlp);
        
        resetUIState(UI_STATE_LOADING);
    }
    
    /**
     * 根据当前状态设置用户界面。
     * 
     * @param state 当前状态为以下状态中的任意一种。
     *              加载昵称中：   {@link #UI_STATE_LOADING}
     *              获取昵称成功：{@link #UI_STATE_FETCHE_SUCCESS}
     *              获取昵称失败：{@link #UI_STATE_FETCHE_FAILED}
     */
    private void resetUIState(int state) {
        switch (state) {          
        case UI_STATE_FETCHE_SUCCESS:
            mProgressBar.setVisibility(INVISIBLE);
            mNickNameView.setVisibility(VISIBLE);
            mLoginButton.setVisibility(GONE);
            break;

        case UI_STATE_FETCHE_FAILED:
            mProgressBar.setVisibility(INVISIBLE);
            mNickNameView.setVisibility(GONE);
            mLoginButton.setVisibility(VISIBLE);
            break;
            
        case UI_STATE_LOADING:            
        default:
            mProgressBar.setVisibility(VISIBLE);
            mNickNameView.setVisibility(GONE);
            mLoginButton.setVisibility(GONE);
            break;
        }
    }
    
    /**
     * 解析XML Tag属性，设置控件的样式
     * TODO: TBD
     * 
     * @param attrs 该View对应的XML Tag属性
     */
    private void parseAttributes(AttributeSet attrs) {
        // TODO: TBD
        /*
        TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.com_sina_weibo_user_state_login);
        int textColor = a.getColor(R.styleable.com_sina_weibo_user_state_login_text_color, Color.BLACK);
        float textDimen = a.getDimension(R.styleable.com_sina_weibo_user_state_login_text_size, 16);
        a.recycle();
        
        try {
            mNickNameView.setTextColor(textColor);
            mNickNameView.setTextSize(textDimen);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }
    
    /**
     * 绑定Service。
     */
    private void doBindService() {
        Intent intent = new Intent(WEIBO_REMOTESSOSERVICE_NAME);
        intent.putExtras(mAuthInfo.getAuthBundle());        
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    
    /**
     * 解绑定Service。
     */
    private void doUnbindService() {
        if (mConnection != null) {
            mContext.unbindService(mConnection);
            mConnection = null;
        }
    }

    /**
     * 当获取昵称完毕后被调用。
     * 
     * @param nickName 获取到的用户昵称。如果为 null 或者空字符串，则表示获取失败。
     */
    private void onFetchCompleted(String nickName) {
        // Tell the listener
        if (mUserInfoListener != null) {
            mUserInfoListener.onUserNickNameRetrieved(nickName);
        }
        
        // Reset view
        mNickNameView.setText(nickName);
        int state = TextUtils.isEmpty(nickName) ? UI_STATE_FETCHE_FAILED : UI_STATE_FETCHE_SUCCESS;
        resetUIState(state);
        
        // Un-bind service
        doUnbindService();
    }
    
    /**
     * 启动SSOActivity，在{@link #onActivityResult(int, int, Intent)}中获取当前登陆用户昵称。
     * 
     * @param packageName SSOActivity的包名
     * @param className   SSOActivity的类名
     * 
     * @return 启动成功，则返回true；失败返回false。
     */
    private boolean startSSOActivity(String packageName, String className) {
        if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(className)) {
            return false;
        }
        
        boolean bSucceed = true;        
        Intent intent = new Intent();
        // 设置要启动的 Package 名和 Activity 名
        intent.setClassName(packageName, className);
        
        // 设置微博授权所需参数
        intent.putExtras(mAuthInfo.getAuthBundle());
        
        // 设置安全验证所需参数
        intent.putExtra(WBConstants.COMMAND_TYPE_KEY, WBConstants.COMMAND_SSO);
        intent.putExtra(WBConstants.TRAN, String.valueOf(System.currentTimeMillis()));
    
        // 判读是否存在有合法的微博客户端
        if (!SecurityHelper.validateAppSignatureForIntent(mContext, intent)) {
            return false;
        }

        try {
            intent.putExtra(EXTRA_REQUEST_CODE, REQUEST_CODE_GET_NICK_NAME);
            ((Activity)mContext).startActivityForResult(intent, REQUEST_CODE_GET_NICK_NAME);
        } catch (ActivityNotFoundException e) {
            bSucceed = false;
        }
        
        return bSucceed;
    }
}