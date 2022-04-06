import ConversationModel from './lib/ConversationView';
import {
    initSdk,
    login,
    logout,
    startChat,
    joinGroup,
    on,
    remove,
    sendGroupTextMessage,
    getGroupMessageList, sendGroupImageMessage
} from './lib/TencentIMModel';

export const TIMConversationModel = ConversationModel;
export const TIMInitSdk = initSdk;
export const TIMLogin = login;
export const TIMLogout = logout;
export const TIMStartChat = startChat;
export const TIMJoinGroup = joinGroup;
export const TIMOn = on;
export const TIMGetGroupMessageList = getGroupMessageList;
export const TIMOnRemove = remove;
export const TIMSendGroupTextMessage = sendGroupTextMessage;
export const TIMSendGroupImageMessage = sendGroupImageMessage;

export default {
  TIMConversationModel,
  TIMInitSdk: TIMInitSdk,
  TIMLogin: TIMLogin,
  TIMLogout: TIMLogout,
  TIMStartChat: TIMStartChat,
    TIMJoinGroup: TIMJoinGroup,
    TIMOn: TIMOn,
    TIMOnRemove: TIMOnRemove,
    TIMSendGroupTextMessage: TIMSendGroupTextMessage,
    TIMGetGroupMessageList: TIMGetGroupMessageList,
    TIMSendGroupImageMessage: TIMSendGroupImageMessage,
};
