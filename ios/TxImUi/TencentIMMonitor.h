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

@interface TencentIMMonitor : NSObject <V2TIMAdvancedMsgListener>

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
@end

NS_ASSUME_NONNULL_END
