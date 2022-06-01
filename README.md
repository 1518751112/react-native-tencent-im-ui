# react-native-tencent-im-ui 腾讯云即时通信 IM 服务的react-native，使用原生ui版本得sdk修改了原作者的依赖冲突

起因，项目中需要用到基础的im功能（聊天和聊天列表），晚上搜了一圈也没有找到，技术栈已经定好，也只能硬着头皮搞了。

直接给大家分享出来，目前功能简单，如果有简单需求的可以直接使用。

当前基于 TIMSDK UI版本 标准版 5.0.6 @2020.09.18

项目地址：[https://github.com/1518751112/react-native-tencent-im-ui](https://github.com/1518751112/react-native-tencent-im-ui)

## 一、支持功能

1. 聊天功能
2. 提供方法自己拼装组件

## 二、待支持的功能

1. 不支持自定义界面（可以yarn install 后，在node_models/react-native-tencent-im-ui/更改里面的代码或者直接clone项目复制先来粘贴过去改吧，虽然不方便，但是也能实现，一个个封装代码都不够项目成本的😂）

1. 离线消息

1. 用户信息编辑

1. 加好友

1. 等等。。。

## 三、支持版本

react-native 0.60 以上版本

## 四、如何安装

### 4.1.安装包

注意需要 --save 参数，react-native会自动link

`$ npm install @tg1518/react-native-tencent-im-ui --save`

`$ yarn add @tg1518/react-native-tencent-im-ui `

### 4.2. link

react-native 0.60以上 使用的autolink，注意需要 --save 参数，react-native会自动link

#### 4.2.1 android 特别处理

1.  **需要在AndroidManifest.xml增加 activity**

    `<activity android:name="com.yunchao.tencentim.activity.ChatActivity" />`

1.  **在你自己的项目中的，android/app/src/main/java/<你的包名>/MainApplication.java 中onCreate()方法中增加如下**

    ```java
    
    @Override
    
    public void onCreate() {
    
    super.onCreate();
    
    SoLoader.init(this, /* native exopackage */ false);
    
    ...
    
    // 然后添加这一行，这里很重要，initSdk中使用到这个，初始化必须在主Application中初始化，否则会出现线程错误问题
    
    IMApplication.setContext(this,  MainActivity.class);
    
    ...
    
    }
    
    ```

1. demo 中增加的有华为push的示例（使用的低版本的push，高版本的总是提示安装hms-core，有点烦），完整的请参考腾讯im-sdk内的demo

#### 4.2.2 ios 特别处理



	```

## 五、示例 请参考 demo 文件夹
注意，LiteAVSDK_TRTC不支持模拟器运行，请使用真机运行

[comment]: <> (android demo 截图)

[comment]: <> (![在这里插入图片描述]&#40;https://img-blog.csdnimg.cn/20201002214658908.jpg#pic_center&#41;)

[comment]: <> (![在这里插入图片描述]&#40;https://img-blog.csdnimg.cn/20201002214658906.jpg#pic_center&#41;)

[comment]: <> (ios demo 截图（同上，忘记截图了）)

##  六、接口

```javascript
//事件列表

/**
 * 群消息
 */
const GROUP_MESSAGE = "groupMessage"
/**
 * 有用户进入群
 */
const ON_MEMBER_ENTER = "onMemberEnter";
/**
 * 有用户离开群
 */
const ON_MEMBER_LEAVE = "onMemberLeave";
/**
 * 有成员被踢出群
 */
const ON_MEMBER_KICKED = "onMemberKicked";
/**
 * 群被解散
 */
const ON_GROUP_DISMISSED = "onGroupDismissed";
/**
 * 指定管理员身份
 */
const ON_GRANT_ADMINISTRATOR = "onGrantAdministrator";
/**
 * 取消管理员身份
 */
const ON_REVOKE_ADMINISTRATOR = "onRevokeAdministrator";
/**
 * 初始化
 * @param sdkAppId
 */
export function TIMInitSdk(sdkAppId) {

}

/**
 * 登录
 * @param userId
 * @param userSig
 * @returns {*|PromiseLike<*>|Promise<*>}
 */
export function TIMLogin(userId, userSig) {

}

/**
 * 登出
 * @returns {Promise<*>}
 */
export async function TIMLogout() {

}

/**
 * 从其他界面跳转到聊天界面
 * @param userId im用户id
 * @param conTitle 聊天标题
 * @param type:
 *  1 = 用户会话
 *  2 = 分组会话
 */
export function TIMStartChat(userId, conTitle, type = 1) {

}

/**
 * 加入群
 * @param groupID 群号码
 */
export function TIMJoinGroup(groupID) {

}

/**
 *  发送群聊普通文本消息（最大支持 8KB）
 * @param text 内容
 * @param groupID 群聊id
 * @param priority 发送等级 0：云端按默认优先级传输，适用于在群里发送非重要消息，比如观众发送的弹幕消息等， 1：云端会优先传输，适用于在群里发送重要消息，比如主播发送的文本消息等。
 * @param promise 回调
 */
export function TIMSendGroupTextMessage(text,groupID,priority) {

}

/**
 *事件监听
 * @param event 事件名
 * @param ck 回调
 */
export function TIMOn(event,ck){

}

/**
 * 组件卸载时记得移除监听事件
 */
export function TIMOnRemove(){
}

/**
 *
 * @param groupID 群id
 * @param count 获取数量 拉取消息的个数，不宜太多，会影响消息拉取的速度，这里建议一次拉取 20 个
 * @param msgID 获取消息的起始消息，如果传 null，起始消息为会话的最新消息
 * @returns {Promise<*>}
 */
export async function TIMGetGroupMessageList(groupID,count,msgID=null){

}

/**
 * 发送群图片消息
 * @param imagePath 本地图片路径
 * @param receiver 消息接收者的 userID, 如果是发送 C2C 单聊消息，只需要指定 receiver 即可[二选一]。
 * @param groupID 群聊id [二选一]
 * @param priority 发送等级 0
 */
export async function TIMSendGroupImageMessage(imagePath,receiver,groupID,priority=0){

}

/**
 * 获取直播群在线人数
 * @param groupID 群id
 * @returns 回调
 */
export async function TIMGetGroupOnlineCount(groupID){
}

/**
 * 退出群
 * @param groupID 群id
 * @returns 回调
 */
export async function TIMQuitGroup(groupID){
}
```

## 七、使用示例

先初始化

```javascript

import {TIMConversationModel, TIMInitSdk, TIMLogin, TIMLogout, TIMStartChat} from 'react-native-tencent-im-ui';

// 先初始化

TIMInitSdk(sdkAppId);

```

调用登录

```javascript

import {TIMConversationModel, TIMInitSdk, TIMLogin, TIMLogout, TIMStartChat} from 'react-native-tencent-im-ui';

// 调用登录

TIMLogin(userId, userSig).then(res=>{

  console.log(res);

}).catch(e => {

});

```

[comment]: <> (从其他界面跳转打开会话)

[comment]: <> (```javascript)

[comment]: <> (import {TIMConversationModel, TIMInitSdk, TIMLogin, TIMLogout, TIMStartChat} from 'react-native-tencent-im-ui';)

[comment]: <> (// 从其他界面跳转打开会话)

[comment]: <> (TIMStartChat&#40;userId, "xxx聊天", 1&#41;;)

[comment]: <> (```)

[comment]: <> (展示聊天列表界面)

[comment]: <> (```javascript)

[comment]: <> (// 展示聊天列表界面)

[comment]: <> (import {TIMConversationModel, TIMInitSdk, TIMLogin, TIMLogout, TIMStartChat} from 'react-native-tencent-im-ui';)

[comment]: <> (import React from "react";)

[comment]: <> (import {)

[comment]: <> (SafeAreaView,)

[comment]: <> (StatusBar,)

[comment]: <> (} from 'react-native';)

[comment]: <> (export default class Conversation extends React.Component{)

[comment]: <> (  render&#40;&#41; {)

[comment]: <> (    return <SafeAreaView style={{flex:1, paddingTop: &#40;Platform.OS === 'ios' ?  10 : StatusBar.currentHeight&#41;}}>)

[comment]: <> (      <TIMConversationModel style={{ flex: 1 }}  {...this.props} />)

[comment]: <> (})

[comment]: <> (})

[comment]: <> (```)

### 参考鸣谢项目

1. [https://github.com/yz1311/react-native-txim/](https://github.com/yz1311/react-native-txim/)

1. [https://github.com/kurisu994/react-native-txim](https://github.com/kurisu994/react-native-txim)
