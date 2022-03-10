package com.yunchao.tencentim.model;

import android.app.Activity;
import android.content.Intent;
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
import com.tencent.imsdk.v2.V2TIMGroupMemberInfo;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMSimpleMsgListener;
import com.tencent.imsdk.v2.V2TIMUserInfo;

import java.util.HashMap;
import java.util.Map;

public class TencentIMMonitor extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;
    private final String GROUP_MESSAGE_TEXT = "groupMessageText";

    public TencentIMMonitor(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.initEvent();

    }



    @Override
    public String getName() {
        return "TencentIMMonitor";
    }
    private void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
    }

/*    @ReactMethod
    public void initEvent(final int sdkAppId) {

    }*/

    /**
     * 初始化事件监听
     */
    private void initEvent(){
        V2TIMManager.getInstance().addSimpleMsgListener(new V2TIMSimpleMsgListener() {
            @Override
            public void onRecvC2CTextMessage(String msgID, V2TIMUserInfo sender, String text) {
                super.onRecvC2CTextMessage(msgID, sender, text);
            }

            @Override
            public void onRecvC2CCustomMessage(String msgID, V2TIMUserInfo sender, byte[] customData) {
                super.onRecvC2CCustomMessage(msgID, sender, customData);
            }

            @Override
            public void onRecvGroupTextMessage(String msgID, String groupID, V2TIMGroupMemberInfo sender, String text) {
                super.onRecvGroupTextMessage(msgID, groupID, sender, text);

                Log.i("TencentIMMonitor","消息id："+msgID+"群id：" + groupID + "消息: "+text);
                WritableMap params = Arguments.createMap();
                params.putString("msgID",msgID);
                params.putString("groupID",groupID);
                params.putString("userID",sender.getUserID());
                params.putString("text",text);

                sendEvent(reactContext,"groupMessageText",params);
            }

            @Override
            public void onRecvGroupCustomMessage(String msgID, String groupID, V2TIMGroupMemberInfo sender, byte[] customData) {
                super.onRecvGroupCustomMessage(msgID, groupID, sender, customData);
            }
        });
    }


}
