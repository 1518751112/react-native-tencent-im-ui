//
//  TenGroupMessage.m
//  react-native-tencent-im-ui
//
//  Created by yons on 2022/4/13.
//

#import "TencentIMMonitor.h"

@implementation TencentIMMonitor{
    TencentIMModel * m;
}

-(id) init:(TencentIMModel *)imModel
{
    m =imModel;
    return self;
}


/// 收到新消息
- (void)onRecvNewMessage:(V2TIMMessage *)msg{
    NSMutableDictionary * body = [TencentIMMonitor MessageSort:msg];

    [m sendEventWithName:@"groupMessage" body:body];
}

/// 收到消息已读回执（仅单聊有效）
- (void)onRecvC2CReadReceipt:(NSArray<V2TIMMessageReceipt *> *)receiptList{


}
/// 收到消息撤回
- (void)onRecvMessageRevoked:(NSString *)msgID{

}

/// 消息内容被修改（第三方服务回调修改了消息内容）
- (void)onRecvMessageModified:(V2TIMMessage *)msg{

}
/**
 *消息归类 发送给javaScript
 *@param msg 消息
 *@return 处理后的信息
 */
+ (NSMutableDictionary *)MessageSort:(V2TIMMessage *)msg{
    V2TIMElemType type = (msg.elemType);
    NSMutableDictionary * params =[[NSMutableDictionary alloc] init];
    NSTimeInterval interval = [msg.timestamp timeIntervalSince1970];
    NSString *timeStamp = [NSString stringWithFormat:@"%i", (int)interval];
    [params setObject:msg.msgID forKey:@"msgID"];
    [params setObject:timeStamp forKey:@"timestamp"];
    [params setObject:msg.sender forKey:@"userID"];
    [params setObject:msg.faceURL? msg.faceURL:@"" forKey:@"avatarPic"];
    [params setObject:msg.nickName? msg.nickName:@"" forKey:@"nickName"];

    [params setObject:[NSString stringWithFormat:@"%llu",msg.seq] forKey:@"sort"];
    [params setObject:[NSString stringWithFormat:@"%ld",type] forKey:@"type"];
    NSMutableArray<NSString *> * imgPaths = [NSMutableArray array];
    switch (type) {
        case V2TIM_ELEM_TYPE_TEXT:
            [params setObject:[msg.textElem text] forKey:@"text"];
            break;
        case V2TIM_ELEM_TYPE_CUSTOM:
            break;
        case V2TIM_ELEM_TYPE_IMAGE:

            for (V2TIMImage *v2TIMImage in msg.imageElem.imageList) {
                [imgPaths addObject:v2TIMImage.url];
            }

            [params setObject:imgPaths forKey:@"imgPaths"];
            break;
        default:
            break;
    }

    NSLog(@"消息类型:%ld",type);
    return params;
}

/// 有新成员加入群（该群所有的成员都能收到）
- (void)onMemberEnter:(NSString *)groupID memberList:(NSArray<V2TIMGroupMemberInfo *>*)memberList{

    NSMutableDictionary * params =[[NSMutableDictionary alloc] init];

    NSMutableArray<NSMutableDictionary *> * userList = [NSMutableArray array];
    for (V2TIMGroupMemberInfo *info in memberList) {
        NSMutableDictionary * temp =[[NSMutableDictionary alloc] init];
        [temp setObject:info.userID forKey:@"userID"];
        [temp setObject:info.nickName?info.nickName:@"" forKey:@"nickName"];
        [temp setObject:info.faceURL?info.faceURL:@"" forKey:@"avatarPic"];
        [userList addObject:temp];
    }

    [params setObject:groupID forKey:@"msgID"];
    [params setObject:userList forKey:@"userList"];

    [m sendEventWithName:@"onMemberEnter" body:params];
}

/// 有成员离开群（该群所有的成员都能收到）
- (void)onMemberLeave:(NSString *)groupID member:(V2TIMGroupMemberInfo *)member{

    NSMutableDictionary * params =[[NSMutableDictionary alloc] init];


    [params setObject:groupID forKey:@"msgID"];
    [params setObject:member.userID forKey:@"userID"];

    [m sendEventWithName:@"onMemberLeave" body:params];
}

/// 某个已加入的群被解散了（该群所有的成员都能收到）
- (void)onGroupDismissed:(NSString *)groupID opUser:(V2TIMGroupMemberInfo *)opUser{

    NSMutableDictionary * params =[[NSMutableDictionary alloc] init];
    NSMutableDictionary * userInfo =[[NSMutableDictionary alloc] init];
    [userInfo setObject:opUser.userID forKey:@"userID"];
    [userInfo setObject:opUser.nickName? opUser.nickName:@"" forKey:@"nickName"];
    [userInfo setObject:opUser.faceURL? opUser.faceURL:@"" forKey:@"avatarPic"];

    [params setObject:groupID forKey:@"msgID"];
    [params setObject:userInfo forKey:@"userInfo"];

    [m sendEventWithName:@"onGroupDismissed" body:params];
}

/// 指定管理员身份
- (void)onGrantAdministrator:(NSString *)groupID opUser:(V2TIMGroupMemberInfo *)opUser memberList:(NSArray <V2TIMGroupMemberInfo *> *)memberList{
    NSMutableDictionary * params =[[NSMutableDictionary alloc] init];
    NSMutableDictionary * handler =[[NSMutableDictionary alloc] init];

    [handler setObject:opUser.userID forKey:@"userID"];
    [handler setObject:opUser.nickName? opUser.nickName:@"" forKey:@"nickName"];
    [handler setObject:opUser.faceURL? opUser.faceURL:@"" forKey:@"avatarPic"];

    NSMutableArray<NSMutableDictionary *> * userList = [NSMutableArray array];
    for (V2TIMGroupMemberInfo *info in memberList) {
        NSMutableDictionary * temp =[[NSMutableDictionary alloc] init];
        [temp setObject:info.userID forKey:@"userID"];
        [temp setObject:info.nickName? info.nickName:@"" forKey:@"nickName"];
        [temp setObject:info.faceURL? info.faceURL:@"" forKey:@"avatarPic"];
        [userList addObject:temp];
    }

    [params setObject:groupID forKey:@"msgID"];
    [params setObject:handler forKey:@"opUser"];
    [params setObject:userList forKey:@"userList"];

    [m sendEventWithName:@"onGrantAdministrator" body:params];
}

/// 取消管理员身份
- (void)onRevokeAdministrator:(NSString *)groupID opUser:(V2TIMGroupMemberInfo *)opUser memberList:(NSArray <V2TIMGroupMemberInfo *> *)memberList{

    NSMutableDictionary * params =[[NSMutableDictionary alloc] init];
    NSMutableDictionary * handler =[[NSMutableDictionary alloc] init];

    [handler setObject:opUser.userID forKey:@"userID"];
    [handler setObject:opUser.nickName? opUser.nickName:@"" forKey:@"nickName"];
    [handler setObject:opUser.faceURL? opUser.faceURL:@"" forKey:@"avatarPic"];

    NSMutableArray<NSMutableDictionary *> * userList = [NSMutableArray array];
    for (V2TIMGroupMemberInfo *info in memberList) {
        NSMutableDictionary * temp =[[NSMutableDictionary alloc] init];
        [temp setObject:info.userID forKey:@"userID"];
        [temp setObject:info.nickName? info.nickName:@"" forKey:@"nickName"];
        [temp setObject:info.faceURL? info.faceURL:@"" forKey:@"avatarPic"];
        [userList addObject:temp];
    }

    [params setObject:groupID forKey:@"msgID"];
    [params setObject:handler forKey:@"opUser"];
    [params setObject:userList forKey:@"userList"];

    [m sendEventWithName:@"onRevokeAdministrator" body:params];
}
@end
