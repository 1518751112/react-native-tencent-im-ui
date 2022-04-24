package com.yunchao.tencentim.model;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.*;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.tencent.imsdk.v2.*;
import com.yunchao.tencentim.IMApplication;
import com.yunchao.tencentim.common.Constants;
import com.yunchao.tencentim.utils.MessageT;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.LogRecord;


public class TencentIMModel extends ReactContextBaseJavaModule {

    private V2TIMUserFullInfo userInfo;

    public TencentIMModel(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    private static ReactApplicationContext reactContext;

    @Override
    public String getName() {
        return "TencentIMModel";
    }

/*    public TUIKitConfigs getConfigs() {
        GeneralConfig config = new GeneralConfig();
        // 显示对方是否已读的view将会展示
        config.setShowRead(true);
        config.setAppCacheDir(IMApplication.getContext().getFilesDir().getPath());
        TUIKit.getConfigs().setGeneralConfig(config);
        return TUIKit.getConfigs();
    }*/

    private void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    @ReactMethod
    public void initSdk(final int sdkAppId,final Promise promise) {
        final Activity activity = getCurrentActivity();
        if (null != activity) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        V2TIMSDKConfig config = new V2TIMSDKConfig();
                        // 3. 指定 log 输出级别，详情请参考 SDKConfig。
                        config.setLogLevel(V2TIMSDKConfig.V2TIM_LOG_INFO);
                        V2TIMManager.getInstance().initSDK(IMApplication.getContext(), sdkAppId, config, new V2TIMSDKListener() {
                            @Override
                            public void onConnecting() {
                                super.onConnecting();
                            }

                            @Override
                            public void onConnectSuccess() {
                                WritableMap params = Arguments.createMap();
                                params.putInt("code", 0);
                                params.putString("desc", "已经成功连接到腾讯云服务器");
                                promise.resolve(params);

                                super.onConnectSuccess();
                            }

                            @Override
                            public void onConnectFailed(int code, String error) {
                                Map<String, Object> result = new HashMap<>(2);
                                result.put("code", -9999);
                                result.put("desc", "连接腾讯云服务器失败");
                                promise.reject(result.toString(),new RuntimeException(error));

                                super.onConnectFailed(code, error);
                            }

                            @Override
                            public void onKickedOffline() {
                                super.onKickedOffline();
                            }

                            @Override
                            public void onUserSigExpired() {
                                super.onUserSigExpired();
                            }

                            @Override
                            public void onSelfInfoUpdated(V2TIMUserFullInfo info) {
                                super.onSelfInfoUpdated(info);
                            }
                        });

                    }catch (Exception e){
                        Log.e("初始化失败",e.getMessage());
                    }
                }
            });
            reactContext = this.getReactApplicationContext();
        }
    }

    @ReactMethod
    public void logout(final Promise promise) {
        V2TIMManager.getInstance().logout(new V2TIMCallback() {
            @Override
            public void onError(int i, String s) {Map<String, Object> result = new HashMap<>(3);
                result.put("module", "onError");
                result.put("code", -9999);
                result.put("desc", "登出失败");
                promise.reject(result.toString(), new RuntimeException(s));

            }

            @Override
            public void onSuccess() {
                WritableMap params = Arguments.createMap();
                params.putString("module", "onSuccess");
                params.putInt("code", 0);
                params.putString("desc", "登出成功");
                promise.resolve(params);
            }
        });
    }


    @ReactMethod
    public void login(final String userId, String userSig, final Promise promise) {
        V2TIMManager.getInstance().login(userId, userSig, new V2TIMCallback() {
            @Override
            public void onError(int code, String desc) {
                Map<String, Object> result = new HashMap<>(2);
                result.put("code", code);
                result.put("desc", desc);
                promise.reject(result.toString(), new RuntimeException(desc));
            }

            @Override
            public void onSuccess() {
                List<String> users = Arrays.asList(userId);

                V2TIMManager.getInstance().getUsersInfo(users,new V2TIMValueCallback<List<V2TIMUserFullInfo>>(){
                    @Override
                    public void onError(int code, String desc) {
                        Map<String, Object> result = new HashMap<>(3);
                        result.put("code", code);
                        result.put("desc", desc);
                        promise.reject(result.toString(), new RuntimeException(desc));
                    }

                    @Override
                    public void onSuccess(List<V2TIMUserFullInfo> v2TIMUserFullInfos) {
                        V2TIMUserFullInfo info = v2TIMUserFullInfos.get(0);
                        //更新信息
                        userInfo = info;
                        WritableMap params = Arguments.createMap();

                        params.putString("module", "success");
                        params.putInt("code", 0);
                        params.putString("desc", "登录成功");
                        WritableMap userInfo = Arguments.createMap();

                        userInfo.putString("userID",info.getUserID());
                        userInfo.putString("nickName",info.getNickName());
                        userInfo.putString("avatarPic",info.getFaceUrl());
                        params.putMap("userInfo",userInfo);
                        promise.resolve(params);
                    }
                });
            }
        });
    }

/*    @ReactMethod
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
    }*/

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
                params.putString("msgID", v2TIMMessage.getMsgID());
//                params.putString("timestamp",v2TIMMessage.getTimestamp()+"");
//                params.putString("userID",v2TIMMessage.getSender());
//                params.putString("avatarPic",v2TIMMessage.getFaceUrl());
//                params.putString("nickName",v2TIMMessage.getNickName());
//                params.putString("sort",v2TIMMessage.getSeq()+"");
//                params.putInt("type",v2TIMMessage.getElemType());
                promise.resolve(params);
            }
        });
    }

    /**
     *  获取群组历史消息
     * @param groupID 群id
     * @param count 拉取消息的个数，不宜太多，会影响消息拉取的速度，这里建议一次拉取 20 个
     * @param msgID 消息id 获取消息的起始消息
     * @param promise 回调
     */
    @ReactMethod
    public void getGroupHistoryMessageList(final String groupID,final int count,final String msgID,final Promise promise){
        V2TIMMessageManager v2TIMMessageManager= V2TIMManager.getMessageManager();
        final V2TIMMessage[] lastMsg = {null};
        if (msgID!=null){
            //从本地获取msgID对应的记录
            v2TIMMessageManager.findMessages(Collections.singletonList(msgID),new V2TIMValueCallback<List<V2TIMMessage>>(){

                @Override
                public void onError(int code, String desc) {
                    Log.i("msgID对应的记录出错了",desc);
                    WritableMap params = Arguments.createMap();
                    params.putInt("code", code);
                    params.putString("desc", desc);
                    promise.reject(params.toString(), new RuntimeException(desc));
                }

                @Override
                public void onSuccess(List<V2TIMMessage> v2TIMMessages) {
                    if (v2TIMMessages.size()>0){
                        v2TIMMessageManager.getGroupHistoryMessageList(groupID, count, v2TIMMessages.get(0), new V2TIMValueCallback<List<V2TIMMessage>>() {
                            @Override
                            public void onError(int code, String desc) {
                                WritableMap params = Arguments.createMap();
                                params.putInt("code", code);
                                params.putString("desc", desc);
                                promise.reject(params.toString(), new RuntimeException(desc));
                            }

                            @Override
                            public void onSuccess(List<V2TIMMessage> v2TIMMessages) {
                                WritableArray arr = Arguments.createArray();

                                for (V2TIMMessage v2T : v2TIMMessages) {
                                    arr.pushMap(MessageT.MessageSort(v2T));
                                }
                                promise.resolve(arr);

                            }
                        });
                    }else{
                        //没有就返回空数组
                        WritableArray arr = Arguments.createArray();
                        promise.resolve(arr);
                    }


                }
            });
        }else{
            v2TIMMessageManager.getGroupHistoryMessageList(groupID, count, lastMsg[0], new V2TIMValueCallback<List<V2TIMMessage>>() {
                @Override
                public void onError(int code, String desc) {
                    WritableMap params = Arguments.createMap();
                    params.putInt("code", code);
                    params.putString("desc", desc);
                    promise.reject(params.toString(), new RuntimeException(desc));
                }

                @Override
                public void onSuccess(List<V2TIMMessage> v2TIMMessages) {
                    WritableArray arr = Arguments.createArray();

                    for (V2TIMMessage v2T : v2TIMMessages) {
                        arr.pushMap(MessageT.MessageSort(v2T));
                    }
                    promise.resolve(arr);

                }
            });
        }


    }

    /**
     * 发送群图片消息
     * @param imagePath 本地图片路径
     * @param receiver 消息接收者的 userID, 如果是发送 C2C 单聊消息，只需要指定 receiver 即可。
     * @param groupID 群聊id [二选一]
     * @param priority 发送等级 0：云端按默认优先级传输，适用于在群里发送非重要消息，比如观众发送的弹幕消息等， 1：云端会优先传输，适用于在群里发送重要消息，比如主播发送的文本消息等。
     * @param promise 回调
     */
    @ReactMethod
    public void sendGroupImageMessage(final String imagePath,final String receiver,final String groupID,int priority,final Promise promise){

        V2TIMMessage message = V2TIMManager.getMessageManager().createImageMessage(imagePath);
        this.sendGroupMessage(message,receiver,groupID,priority,promise);
    }


    /**
     *  发送高级消息（最大支持 8KB）
     * @param message 待发送的消息对象
     * @param receiver 消息接收者的 userID, 如果是发送 C2C 单聊消息，只需要指定 receiver 即可。
     * @param groupID 群聊id [二选一]
     * @param priority 发送等级 0：云端按默认优先级传输，适用于在群里发送非重要消息，比如观众发送的弹幕消息等， 1：云端会优先传输，适用于在群里发送重要消息，比如主播发送的文本消息等。
     * @param promise 回调
     */
    private void sendGroupMessage(final V2TIMMessage message,final String receiver,final String groupID,int priority,final Promise promise) {
        priority = priority>=1? V2TIMMessage.V2TIM_PRIORITY_HIGH:V2TIMMessage.V2TIM_PRIORITY_NORMAL;

        V2TIMManager.getMessageManager().sendMessage(message, receiver, groupID, priority, false, null, new V2TIMSendCallback<V2TIMMessage>() {
            @Override
            public void onProgress(int progress) {

            }

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

                params.putString("msgID", v2TIMMessage.getMsgID());
//                params.putString("timestamp",v2TIMMessage.getTimestamp()+"");
//                params.putString("userID",v2TIMMessage.getSender());
//                params.putString("avatarPic",v2TIMMessage.getFaceUrl());
//                params.putString("nickName",v2TIMMessage.getNickName());
//                params.putString("sort",v2TIMMessage.getSeq()+"");
//                params.putInt("type",v2TIMMessage.getElemType());
                promise.resolve(params);
            }
        });
    }

    /**
     * 获取直播群在线人数
     * @param groupID 群id
     * @param promise 回调
     */
    @ReactMethod
    public void getGroupOnlineMemberCount(final String groupID,final Promise promise){

        V2TIMGroupManager v2TIMGroupManager = V2TIMManager.getGroupManager();
        v2TIMGroupManager.getGroupOnlineMemberCount(groupID,new V2TIMValueCallback<Integer>(){

            @Override
            public void onError(int code, String desc) {
                WritableMap params = Arguments.createMap();
                params.putInt("code", code);
                params.putString("desc", desc);
                promise.reject(params.toString(), new RuntimeException(desc));
            }

            @Override
            public void onSuccess(Integer integer) {
                WritableMap params = Arguments.createMap();
                params.putInt("code", 0);
                params.putInt("number", integer);
                promise.resolve(params);
            }
        });
    }

    /**
     * 更新个人信息
     * @param info 信息
     * @param promise 回调
     */
    @ReactMethod
    public void setSelfInfo(final  @NotNull ReadableMap info, final Promise promise){
        V2TIMUserFullInfo userConfig = new V2TIMUserFullInfo();
        if (info.hasKey("nickName")){
            userConfig.setNickname(info.getString("nickName"));
        }
        if (info.hasKey("avatarPic")){
            userConfig.setFaceUrl(info.getString("avatarPic"));
        }
        V2TIMManager v2TIMManager = V2TIMManager.getInstance();
        v2TIMManager.setSelfInfo(userConfig, new V2TIMCallback() {
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
                params.putString("desc","修改成功");
                promise.resolve(params);
            }
        });
    }
}
