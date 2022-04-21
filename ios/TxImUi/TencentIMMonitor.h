//
//  TenGroupMessage.h
//  react-native-tencent-im-ui
//
//  Created by yons on 2022/4/13.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import <ImSDK_Plus/V2TIMManager+Message.h>
#import <ImSDK_Plus/V2TIMManager.h>
#import <ImSDK_Plus/ImSDK_Plus.h>
#import "TencentIMModel.h"
@class TencentIMModel;

NS_ASSUME_NONNULL_BEGIN

@interface TencentIMMonitor : NSObject <V2TIMAdvancedMsgListener,V2TIMGroupListener>

-(id) init:(TencentIMModel *)imModel;

+ (NSMutableDictionary *)MessageSort:(V2TIMMessage *)msg;

/// 收到新消息
- (void)onRecvNewMessage:(V2TIMMessage *)msg;

/// 收到消息已读回执（仅单聊有效）
- (void)onRecvC2CReadReceipt:(NSArray<V2TIMMessageReceipt *> *)receiptList;

/// 收到消息撤回
- (void)onRecvMessageRevoked:(NSString *)msgID;

/// 消息内容被修改（第三方服务回调修改了消息内容）
- (void)onRecvMessageModified:(V2TIMMessage *)msg;

/////////////////////////////////////////////////////////////////////////////////
//        群成员相关通知
/////////////////////////////////////////////////////////////////////////////////

/// 有新成员加入群（该群所有的成员都能收到）
- (void)onMemberEnter:(NSString *)groupID memberList:(NSArray<V2TIMGroupMemberInfo *>*)memberList;

/// 有成员离开群（该群所有的成员都能收到）
- (void)onMemberLeave:(NSString *)groupID member:(V2TIMGroupMemberInfo *)member;

/// 某成员被拉入某群（该群所有的成员都能收到）
- (void)onMemberInvited:(NSString *)groupID opUser:(V2TIMGroupMemberInfo *)opUser memberList:(NSArray<V2TIMGroupMemberInfo *>*)memberList;

/// 有成员被踢出某群（该群所有的成员都能收到）
- (void)onMemberKicked:(NSString *)groupID opUser:(V2TIMGroupMemberInfo *)opUser memberList:(NSArray<V2TIMGroupMemberInfo *>*)memberList;

/// 某成员信息发生变更（该群所有的成员都能收到）。会议群（Meeting）和直播群（AVChatRoom）默认无此回调，如需回调请提交工单配置
- (void)onMemberInfoChanged:(NSString *)groupID changeInfoList:(NSArray <V2TIMGroupMemberChangeInfo *> *)changeInfoList;

/////////////////////////////////////////////////////////////////////////////////
//        群生命周期相关通知
/////////////////////////////////////////////////////////////////////////////////

/// 有新的群创建（创建者能收到，应用于多端消息同步的场景）
- (void)onGroupCreated:(NSString *)groupID;

/// 某个已加入的群被解散了（该群所有的成员都能收到）
- (void)onGroupDismissed:(NSString *)groupID opUser:(V2TIMGroupMemberInfo *)opUser;

/// 某个已加入的群被回收了（该群所有的成员都能收到）
- (void)onGroupRecycled:(NSString *)groupID opUser:(V2TIMGroupMemberInfo *)opUser;

/// 某个已加入的群的信息被修改了（该群所有的成员都能收到）
- (void)onGroupInfoChanged:(NSString *)groupID changeInfoList:(NSArray <V2TIMGroupChangeInfo *> *)changeInfoList;

/// 某个已加入的群的属性被修改了，会返回所在群组的所有属性（该群所有的成员都能收到）
- (void)onGroupAttributeChanged:(NSString *)groupID attributes:(NSMutableDictionary<NSString *,NSString *> *)attributes;

/////////////////////////////////////////////////////////////////////////////////
//        加群申请相关通知
/////////////////////////////////////////////////////////////////////////////////

/// 有新的加群请求（只有群主和管理员会收到）
- (void)onReceiveJoinApplication:(NSString *)groupID member:(V2TIMGroupMemberInfo *)member opReason:(NSString *)opReason;

/// 加群请求已经被群主或管理员处理了（只有申请人能够收到）
- (void)onApplicationProcessed:(NSString *)groupID opUser:(V2TIMGroupMemberInfo *)opUser opResult:(BOOL)isAgreeJoin opReason:(NSString *)opReason;

/////////////////////////////////////////////////////////////////////////////////
//        其他相关通知
/////////////////////////////////////////////////////////////////////////////////

/// 指定管理员身份
- (void)onGrantAdministrator:(NSString *)groupID opUser:(V2TIMGroupMemberInfo *)opUser memberList:(NSArray <V2TIMGroupMemberInfo *> *)memberList;

/// 取消管理员身份
- (void)onRevokeAdministrator:(NSString *)groupID opUser:(V2TIMGroupMemberInfo *)opUser memberList:(NSArray <V2TIMGroupMemberInfo *> *)memberList;

/// 自己主动退出群组（主要用于多端同步，直播群（AVChatRoom）不支持）
- (void)onQuitFromGroup:(NSString *)groupID;

/// 收到 RESTAPI 下发的自定义系统消息
- (void)onReceiveRESTCustomData:(NSString *)groupID data:(NSData *)data;

@end

NS_ASSUME_NONNULL_END
