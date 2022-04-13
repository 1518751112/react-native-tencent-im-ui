//
//  IMManager.h
//  RCTTxim
//
//  Created by 张建军 on 2019/5/5.
//  Copyright © 2019 feewee. All rights reserved.
//

#import "TUIKit.h"
#import <React/RCTViewManager.h>
#import <ImSDK_Plus/ImSDK_Plus.h>
#import <React/RCTBridgeModule.h>

NS_ASSUME_NONNULL_BEGIN

@interface TencentIMManager : NSObject

/**
 * 获取实例
 */
+ (instancetype)getInstance;

/**
 * 初始化SDK
 * @param sdkAppId 配置文件路径，默认为mainBundle下的txim.plist
 */
- (BOOL)initSdk:(int)sdkAppId;

/**
 * 用户登录
 * @param identify identify
 * @param userSig userSig
 * @param succ 成功回调
 * @param fail 失败回调
 */
- (void)loginWithIdentify:(NSString *)identify
                  userSig:(NSString *)userSig
                     succ:(V2TIMSucc)succ
                     fail:(V2TIMFail)fail;

/**
 * 用户登出
 * @param succ 成功回调
 * @param fail 失败回调
 */
- (void)logoutWithSucc:(V2TIMSucc)succ fail:(V2TIMFail)fail;

/**
 * 加入群
 * @param groupID 群id
 * @param succ 成功回调
 * @param fail 失败回调
 */
-(void)joinGroup:(NSString *)groupID succ:(V2TIMSucc)succ fail:(V2TIMFail)fail;

/**
 * 发送群聊普通文本消息
 * @param text 内容
 * @param groupID 群id
 * @param priority 设置消息的优先级
 * @param succ 成功回调
 * @param fail 失败回调
 */
-(void)sendGroupTextMessage:(NSString *)text
                         to:(NSString *)groupID
                        priority:(V2TIMMessagePriority)priority
                       succ:(V2TIMSucc)succ
                       fail:(V2TIMFail)fail;

/**
 * 发送高级消息
 * @param message 消息
 * @param receiver 用户d
 * @param groupID 群id
 * @param priority 设置消息的优先级
 * @param succ 成功回调
 * @param fail 失败回调
 */
-(void)sendMessage:(V2TIMMessage *)message
        receiver:(NSString *)receiver
        to:(NSString *)groupID
        priority:(V2TIMMessagePriority)priority
        succ:(V2TIMSucc)succ
        fail:(V2TIMFail)fail;

/**
 * 发送高级消息
 * @param groupID 群id
 * @param count 拉取的数量
 * @param msgID 消息ID获取消息的起始位置
 * @param succ 成功回调
 * @param fail 失败回调
 */
-(void)getGroupHistoryMessageList:(NSString *)groupID
             count:(int)count
           msgID:(NSString *)msgID
        succ:(V2TIMMessageListSucc)succ
              fail:(V2TIMFail)fail;

@end

NS_ASSUME_NONNULL_END
