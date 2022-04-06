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
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.tencent.imsdk.v2.V2TIMAdvancedMsgListener;
import com.tencent.imsdk.v2.V2TIMGroupMemberInfo;
import com.tencent.imsdk.v2.V2TIMImageElem;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMMessageManager;
import com.tencent.imsdk.v2.V2TIMMessageReceipt;
import com.tencent.imsdk.v2.V2TIMSimpleMsgListener;
import com.tencent.imsdk.v2.V2TIMTextElem;
import com.tencent.imsdk.v2.V2TIMUserInfo;
import com.yunchao.tencentim.utils.MessageT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TencentIMMonitor extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;
    private final String GROUP_MESSAGE = "groupMessage";

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
        V2TIMMessageManager v2TIMMessageManager = V2TIMManager.getMessageManager();
        V2TIMManager v2TIMManager = V2TIMManager.getInstance();

        v2TIMMessageManager.addAdvancedMsgListener(new V2TIMAdvancedMsgListener() {
            @Override
            public void onRecvNewMessage(V2TIMMessage msg) {
                sendEvent(reactContext,GROUP_MESSAGE, MessageT.MessageSort(msg));

                Log.i("TencentIMMonitor","消息id："+msg.getMsgID()+"群id：" + msg.getGroupID() + "消息类型: "+msg.getElemType());

                super.onRecvNewMessage(msg);
            }

            @Override
            public void onRecvC2CReadReceipt(List<V2TIMMessageReceipt> receiptList) {
                super.onRecvC2CReadReceipt(receiptList);
            }

            @Override
            public void onRecvMessageRevoked(String msgID) {
                super.onRecvMessageRevoked(msgID);
            }
        });
    }


}
