import ConversationModel from './lib/ConversationView';
import {initSdk, login, logout, startChat, joinGroup, on, remove, sendGroupTextMessage} from './lib/TencentIMModel';

export const TIMConversationModel = ConversationModel;
export const TIMInitSdk = initSdk;
export const TIMLogin = login;
export const TIMLogout = logout;
export const TIMStartChat = startChat;
export const TIMJoinGroup = joinGroup;
export const TIMOn = on;
export const TIMOnRemove = remove;
export const TIMSendGroupTextMessage = sendGroupTextMessage;

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
};
