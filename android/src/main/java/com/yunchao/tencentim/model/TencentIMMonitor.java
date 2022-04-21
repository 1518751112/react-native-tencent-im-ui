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
import com.tencent.imsdk.v2.*;
import com.yunchao.tencentim.utils.MessageT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TencentIMMonitor extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;
    /**
     * 群消息事件
     */
    private final String GROUP_MESSAGE = "groupMessage";
    /**
     * 有用户加入群
     */
    private final String ON_MEMBER_ENTER = "onMemberEnter";
    /**
     * 有用户离开群
     */
    private final String ON_MEMBER_LEAVE = "onMemberLeave";
    /**
     * 有成员被踢出群
     */
    private final String ON_MEMBER_KICKED = "onMemberKicked";
    /**
     * 群被解散
     */
    private final String ON_GROUP_DISMISSED = "onGroupDismissed";
    /**
     * 指定管理员身份
     */
    private final String ON_GRANT_ADMINISTRATOR = "onGrantAdministrator";
    /**
     * 取消管理员身份
     */
    private final String ON_REVOKE_ADMINISTRATOR = "onRevokeAdministrator";

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
        //--------消息监听--------
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

        //--------群通知--------
        v2TIMManager.setGroupListener(new V2TIMGroupListener() {
            //有成员加入群
            @Override
            public void onMemberEnter(String groupID, List<V2TIMGroupMemberInfo> memberList) {
                Log.i("onMemberEnter",groupID);
                WritableMap params = Arguments.createMap();
                params.putString("groupID",groupID);

                WritableArray userList = Arguments.createArray();
                for (V2TIMGroupMemberInfo info : memberList) {
                    WritableMap temp = Arguments.createMap();
                    temp.putString("userID",info.getUserID());
                    temp.putString("nickName",info.getNickName());
                    temp.putString("avatarPic",info.getFaceUrl());

                    userList.pushMap(temp);
                }
                params.putArray("userList",userList);

                sendEvent(reactContext,ON_MEMBER_ENTER, params);
                super.onMemberEnter(groupID, memberList);
            }

            //有成员离开群
            @Override
            public void onMemberLeave(String groupID, V2TIMGroupMemberInfo member) {
                WritableMap params = Arguments.createMap();
                params.putString("groupID",groupID);
                params.putString("userID",member.getUserID());

                sendEvent(reactContext,ON_MEMBER_LEAVE, params);
                super.onMemberLeave(groupID, member);
            }

            @Override
            public void onMemberInvited(String groupID, V2TIMGroupMemberInfo opUser, List<V2TIMGroupMemberInfo> memberList) {
                super.onMemberInvited(groupID, opUser, memberList);
            }

            //有成员被踢出群
            @Override
            public void onMemberKicked(String groupID, V2TIMGroupMemberInfo opUser, List<V2TIMGroupMemberInfo> memberList) {
                WritableMap params = Arguments.createMap();
                params.putString("groupID",groupID);
                WritableMap handler = Arguments.createMap();
                handler.putString("userID",opUser.getUserID());
                handler.putString("nickName",opUser.getNickName());
                handler.putString("avatarPic",opUser.getFaceUrl());

                WritableArray userList = Arguments.createArray();

                for (V2TIMGroupMemberInfo info : memberList) {
                    WritableMap temp = Arguments.createMap();
                    temp.putString("userID",info.getUserID());
                    temp.putString("nickName",info.getNickName());
                    temp.putString("avatarPic",info.getFaceUrl());

                    userList.pushMap(temp);
                }
                params.putMap("opUser",handler);
                params.putArray("userList",userList);

                sendEvent(reactContext,ON_MEMBER_KICKED, params);

                super.onMemberKicked(groupID, opUser, memberList);
            }

            @Override
            public void onMemberInfoChanged(String groupID, List<V2TIMGroupMemberChangeInfo> v2TIMGroupMemberChangeInfoList) {
                super.onMemberInfoChanged(groupID, v2TIMGroupMemberChangeInfoList);
            }

            @Override
            public void onGroupCreated(String groupID) {
                super.onGroupCreated(groupID);
            }

            //群被解散
            @Override
            public void onGroupDismissed(String groupID, V2TIMGroupMemberInfo opUser) {
                WritableMap params = Arguments.createMap();
                WritableMap userInfo = Arguments.createMap();

                userInfo.putString("userID",opUser.getUserID());
                userInfo.putString("nickName",opUser.getNickName());
                userInfo.putString("avatarPic",opUser.getFaceUrl());

                params.putMap("userInfo",userInfo);
                params.putString("groupID",groupID);

                sendEvent(reactContext,ON_GROUP_DISMISSED, params);
                super.onGroupDismissed(groupID, opUser);
            }

            @Override
            public void onGroupRecycled(String groupID, V2TIMGroupMemberInfo opUser) {
                super.onGroupRecycled(groupID, opUser);
            }

            @Override
            public void onGroupInfoChanged(String groupID, List<V2TIMGroupChangeInfo> changeInfos) {
                super.onGroupInfoChanged(groupID, changeInfos);
            }

            @Override
            public void onReceiveJoinApplication(String groupID, V2TIMGroupMemberInfo member, String opReason) {
                super.onReceiveJoinApplication(groupID, member, opReason);
            }

            @Override
            public void onApplicationProcessed(String groupID, V2TIMGroupMemberInfo opUser, boolean isAgreeJoin, String opReason) {
                super.onApplicationProcessed(groupID, opUser, isAgreeJoin, opReason);
            }

            //指定管理员身份
            @Override
            public void onGrantAdministrator(String groupID, V2TIMGroupMemberInfo opUser, List<V2TIMGroupMemberInfo> memberList) {
                WritableMap params = Arguments.createMap();
                params.putString("groupID",groupID);
                WritableMap handler = Arguments.createMap();
                handler.putString("userID",opUser.getUserID());
                handler.putString("nickName",opUser.getNickName());
                handler.putString("avatarPic",opUser.getFaceUrl());

                WritableArray userList = Arguments.createArray();

                for (V2TIMGroupMemberInfo info : memberList) {
                    WritableMap temp = Arguments.createMap();
                    temp.putString("userID",info.getUserID());
                    temp.putString("nickName",info.getNickName());
                    temp.putString("avatarPic",info.getFaceUrl());

                    userList.pushMap(temp);
                }
                params.putMap("opUser",handler);
                params.putArray("userList",userList);
                sendEvent(reactContext,ON_GRANT_ADMINISTRATOR, params);
                super.onGrantAdministrator(groupID, opUser, memberList);
            }

            //取消管理员身份
            @Override
            public void onRevokeAdministrator(String groupID, V2TIMGroupMemberInfo opUser, List<V2TIMGroupMemberInfo> memberList) {
                WritableMap params = Arguments.createMap();
                params.putString("groupID",groupID);
                WritableMap handler = Arguments.createMap();
                handler.putString("userID",opUser.getUserID());
                handler.putString("nickName",opUser.getNickName());
                handler.putString("avatarPic",opUser.getFaceUrl());

                WritableArray userList = Arguments.createArray();

                for (V2TIMGroupMemberInfo info : memberList) {
                    WritableMap temp = Arguments.createMap();
                    temp.putString("userID",info.getUserID());
                    temp.putString("nickName",info.getNickName());
                    temp.putString("avatarPic",info.getFaceUrl());

                    userList.pushMap(temp);
                }
                params.putMap("opUser",handler);
                params.putArray("userList",userList);
                sendEvent(reactContext,ON_REVOKE_ADMINISTRATOR, params);
                super.onRevokeAdministrator(groupID, opUser, memberList);
            }

            @Override
            public void onQuitFromGroup(String groupID) {
                super.onQuitFromGroup(groupID);
            }

            @Override
            public void onReceiveRESTCustomData(String groupID, byte[] customData) {
                super.onReceiveRESTCustomData(groupID, customData);
            }

            @Override
            public void onGroupAttributeChanged(String groupID, Map<String, String> groupAttributeMap) {
                super.onGroupAttributeChanged(groupID, groupAttributeMap);
            }
        });
    }


}
