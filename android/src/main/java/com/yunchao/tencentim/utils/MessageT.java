package com.yunchao.tencentim.utils;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.tencent.imsdk.v2.V2TIMImageElem;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMTextElem;

public class MessageT {
    /**
     * 消息归类 发送给javaScript
     * @param msg  消息
     * @return WritableMap
     */
    public static WritableMap MessageSort(V2TIMMessage msg){
        String msgID = msg.getMsgID();
        String groupID = msg.getGroupID();
        String userID = msg.getSender(); //发送者 userID
        //定义监听回调参数
        WritableMap params = Arguments.createMap();
        params.putString("msgID",msgID);
        params.putString("timestamp",msg.getTimestamp()+"");
        params.putString("userID",userID);
        params.putString("sort",msg.getSeq()+"");
        params.putInt("type",msg.getElemType());

        switch (msg.getElemType()){
            case V2TIMMessage.V2TIM_ELEM_TYPE_TEXT: //文本消息
                V2TIMTextElem v2TIMTextElem = msg.getTextElem();
                params.putString("groupID",groupID);
                params.putString("text",v2TIMTextElem.getText());
                break;
            case V2TIMMessage.V2TIM_ELEM_TYPE_CUSTOM://自定义消息

                break;
            case V2TIMMessage.V2TIM_ELEM_TYPE_IMAGE://图片消息
                V2TIMImageElem v2TIMImageElem = msg.getImageElem();
                WritableArray imgPaths = Arguments.createArray();

                for (V2TIMImageElem.V2TIMImage v2TIMImage : v2TIMImageElem.getImageList()) {
                    imgPaths.pushString(v2TIMImage.getUrl());
                }

                params.putString("groupID",groupID);
                params.putArray("imgPaths",imgPaths);
                break;
        }

        return params;
    }
}
