package com.yunchao.tencentim.model;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.imsdk.v2.V2TIMGroupMemberInfo;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMSimpleMsgListener;
import com.tencent.imsdk.v2.V2TIMUserInfo;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.base.IMEventListener;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.tencent.qcloud.tim.uikit.config.GeneralConfig;
import com.tencent.qcloud.tim.uikit.config.TUIKitConfigs;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.yunchao.tencentim.IMApplication;
import com.yunchao.tencentim.activity.ChatActivity;
import com.yunchao.tencentim.common.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogRecord;


public class TencentIMModel extends ReactContextBaseJavaModule {

    public TencentIMModel(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    private static ReactApplicationContext reactContext;

    @Override
    public String getName() {
        return "TencentIMModel";
    }

    public TUIKitConfigs getConfigs() {
        GeneralConfig config = new GeneralConfig();
        // 显示对方是否已读的view将会展示
        config.setShowRead(true);
        config.setAppCacheDir(IMApplication.getContext().getFilesDir().getPath());
        TUIKit.getConfigs().setGeneralConfig(config);
        return TUIKit.getConfigs();
    }

    private void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
    }

    @ReactMethod
    public void initSdk(final int sdkAppId) {
        final Activity activity = getCurrentActivity();
        if (null != activity) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TUIKit.init(IMApplication.getContext(), sdkAppId, getConfigs());
                }
            });
            reactContext = this.getReactApplicationContext();
        }
    }

    @ReactMethod
    public void logout(final Promise promise) {
        final String loginUser = TIMManager.getInstance().getLoginUser();
        TIMManager.getInstance().logout(new TIMCallBack() {
            @Override
            public void onError(int i, String s) {Map<String, Object> result = new HashMap<>(3);
                result.put("module", "onError");
                result.put("code", -9999);
                result.put("desc", "登出失败");
                promise.reject(result.toString(), new RuntimeException(s));

            }

            @Override
            public void onSuccess() {
                Map<String, Object> result = new HashMap<>(3);
                result.put("module", "onSuccess");
                result.put("code", 0);
                result.put("desc", "登出成功");
                promise.resolve(result);
            }
        });
    }


    @ReactMethod
    public void login(final String userId, String userSig, final Promise promise) {
        TUIKit.login(userId, userSig, new IUIKitCallBack() {
            @Override
            public void onError(String module, final int code, final String desc) {
                Map<String, Object> result = new HashMap<>(3);
                result.put("module", module);
                result.put("code", code);
                result.put("desc", desc);
                promise.reject(result.toString(), new RuntimeException(desc));
            }

            @Override
            public void onSuccess(Object data) {
                Map<String, Object> result = new HashMap<>(3);
                result.put("module", "success");
                result.put("code", 0);
                result.put("desc", "登录成功");
                promise.resolve(data);
            }
        });
    }

    @ReactMethod
    public void startChatView(String userId, String conTitle, int type) {
        final Activity activity = getCurrentActivity();
        if (activity != null) {

            ChatInfo chatInfo = new ChatInfo();
            if (type == 2) {
                chatInfo.setType(TIMConversationType.Group.value());
            } else {
                chatInfo.setType(TIMConversationType.C2C.value());
            }
            chatInfo.setId(userId);
            chatInfo.setChatName(conTitle);
            final Intent intent = new Intent(activity, ChatActivity.class);
            intent.putExtra(Constants.CHAT_INFO, chatInfo);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        }
    }

    @ReactMethod
    public void joinGroup(final String groupID,final Promise promise) {
        V2TIMManager.getInstance().joinGroup(groupID, "", new V2TIMCallback() {
            @Override
            public void onError(int code, String desc) {
                WritableMap params = Arguments.createMap();
                params.putInt("code",code);
                params.putString("desc",desc);
                promise.reject(params.toString(),new RuntimeException(desc));
            }

            @Override
            public void onSuccess() {
                WritableMap params = Arguments.createMap();
                params.putInt("code",0);
                params.putString("desc","加入群成功");
                promise.resolve(params);
            }
        });
    }


    /**
     *  发送群聊普通文本消息（最大支持 8KB）
     * @param text 内容
     * @param groupID 群聊id
     * @param priority 发送等级 0：云端按默认优先级传输，适用于在群里发送非重要消息，比如观众发送的弹幕消息等， 1：云端会优先传输，适用于在群里发送重要消息，比如主播发送的文本消息等。
     * @param promise 回调
     */
    @ReactMethod
    public void sendGroupTextMessage(final String text,final String groupID,int priority,final Promise promise) {
        priority = priority>=1? V2TIMMessage.V2TIM_PRIORITY_HIGH:V2TIMMessage.V2TIM_PRIORITY_NORMAL;

        V2TIMManager.getInstance().sendGroupTextMessage(text, groupID, priority, new V2TIMValueCallback<V2TIMMessage>() {
            @Override
            public void onError(int code, String desc) {
                WritableMap params = Arguments.createMap();
                params.putInt("code", code);
                params.putString("desc", desc);
                promise.reject(params.toString(), new RuntimeException(desc));
            }

            @Override
            public void onSuccess(V2TIMMessage v2TIMMessage) {
                WritableMap params = Arguments.createMap();
                params.putInt("code", 0);
                params.putString("desc", "发送成功");
                params.putString("infoCode", v2TIMMessage.getMsgID());
                promise.resolve(params);
            }
        });
    }


}
