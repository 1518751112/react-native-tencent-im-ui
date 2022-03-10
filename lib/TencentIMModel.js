import {NativeEventEmitter, NativeModules} from "react-native";

export const TencentIMModel = NativeModules.TencentIMModel;
export const TencentIMMonitor = NativeModules.TencentIMMonitor;

let initFlag = false;
let loginFlag = false;

const eventListener = []
const GROUP_MESSAGE_TEXT = "groupMessageText" //群文本消息事件

/**
 * 初始化
 * @param sdkAppId
 */
export function initSdk(sdkAppId) {
  TencentIMModel.initSdk(sdkAppId);
  initFlag = true;
}

/**
 * 登录
 * @param userId
 * @param userSig
 * @returns {*|PromiseLike<*>|Promise<*>}
 */
export function login(userId, userSig) {
  return TencentIMModel.login(userId, userSig).then(res=> {
    loginFlag = true;
    return res;
  });
}

/**
 * 登出
 * @returns {Promise<*>}
 */
export async function logout() {
  return TencentIMModel.logout().then(res=>{
    loginFlag = false;
    return res;
  });
}

/**
 * 从其他界面跳转到聊天界面
 * @param userId im用户id
 * @param conTitle 聊天标题
 * @param type:
 *  1 = 用户会话
 *  2 = 分组会话
 */
export function startChat(userId, conTitle, type = 1) {
  if (!loginFlag) {
    throw new Error("请先调用登录接口：login(userId, userSig)");
  }
  TencentIMModel.startChatView(userId, conTitle, type);
}

/**
 * 加入群
 * @param groupID 群号码
 */
export function joinGroup(groupID) {
    if (!loginFlag) {
        throw new Error("请先调用登录接口：login(userId, userSig)");
    }
    return TencentIMModel.joinGroup(groupID);
}

/**
 *  发送群聊普通文本消息（最大支持 8KB）
 * @param text 内容
 * @param groupID 群聊id
 * @param priority 发送等级 0：云端按默认优先级传输，适用于在群里发送非重要消息，比如观众发送的弹幕消息等， 1：云端会优先传输，适用于在群里发送重要消息，比如主播发送的文本消息等。
 * @param promise 回调
 */
export function sendGroupTextMessage(text,groupID,priority) {
    if (!loginFlag) {
        throw new Error("请先调用登录接口：login(userId, userSig)");
    }
    return TencentIMModel.sendGroupTextMessage(text,groupID,priority);
}

/**
 *事件监听
 * @param event 事件名
 * @param ck 回调
 */
export function on(event,ck){
    const eventEmitter = new NativeEventEmitter(TencentIMMonitor)
    eventListener.push(eventEmitter.addListener(event,typeof ck =="function"?ck:()=>{}))
}

/**
 * 组件卸载时记得移除监听事件
 */
export function remove(){
    for (let i = 0; i < eventListener.length; i++) {
        eventListener[i].remove()
    }
}
