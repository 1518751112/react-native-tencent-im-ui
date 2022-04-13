//
//  IMManager.m
//  RCTTxim
//
//  Created by 张建军 on 2019/5/5.
//  Copyright © 2019 feewee. All rights reserved.
//

#import "TencentIMManager.h"
#import "TUIKit.h"
#import <ImSDK_Plus/V2TIMManager.h>
#import <ImSDK_Plus/ImSDK_Plus.h>

@implementation TencentIMManager {
  /// 是否初始化
  BOOL isInit;
  /// 应用ID
  int sdkAppId;
  /// 会话
  V2TIMConversation *conversation;
  /// 会话
  NSString *currentReceiver;
  /// 设备token
  NSData *deviceToken;
  /// IM配置
  NSDictionary *configDict;
}


+ (instancetype)getInstance {
  __strong static TencentIMManager *instance;
  static dispatch_once_t onceToken;
  dispatch_once(&onceToken, ^{
    instance = [self new];
  });
  return instance;
}

#pragma clang diagnostic push
#pragma ide diagnostic ignored "ResourceNotFoundInspection"

- (BOOL)initSdk:(int)sdkAppId {
  if (isInit) {
    return YES;
  }
    
//  V2TIMManager *tm = [V2TIMManager sharedInstance];
//  int sdkAppId = [sdkConfig sdkAppId];

  // 是否允许log打印
  id disableLogPrintValue = [configDict valueForKey:@"disableLogPrint"];
  if (disableLogPrintValue) {
//    sdkConfig.disableLogPrint = [disableLogPrintValue boolValue];
  }
  // Log输出级别, 默认DEBUG等级
  id logLevelValue = [configDict valueForKey:@"logLevel"];
  if (logLevelValue) {
//    sdkConfig.logLevel = (TIMLogLevel) [logLevelValue integerValue];
  }
  [[TUIKit sharedInstance] setupWithAppId:sdkAppId]; // SDKAppID 可以在 即时通信 IM 控制台中获取
  isInit = YES;
  return isInit;
}

#pragma clang diagnostic pop

- (void)loginWithIdentify:(NSString *)identify
                  userSig:(NSString *)userSig
                     succ:(V2TIMSucc)succ
                     fail:(V2TIMFail)fail {
  // 登录参数
  V2TIMManager *tm = [V2TIMManager sharedInstance];
  void (^login)(void) = ^(void) {
      [tm login:identify userSig:userSig succ:^{
          [self configAppAPNSDeviceToken];
          succ();
      } fail:fail];
//      fail(result, @"调用登录失败");
    
  };
  // 判断是否已经登录
  if ([tm getLoginStatus] == V2TIM_STATUS_LOGINED) {
    // 判断是否已经登录了当前账号
    if ([[tm getLoginUser] isEqualToString:identify]) {
      login();
    } else {
      // 登出之前的账号
      [tm logout:^{
        login();
      }                  fail:fail];
//        fail(result, @"切换登录失败");
      
    }
  } else {
    login();
  }
}

- (void)logoutWithSucc:(V2TIMSucc)succ fail:(V2TIMFail)fail {
  V2TIMManager *tm = [V2TIMManager sharedInstance];
  if ([tm getLoginStatus] == V2TIM_STATUS_LOGOUT) {
    succ();
  } else {
      [tm logout:succ fail:fail];
    
//      fail(result, @"调用登出失败");
    
  }
}


- (void)configDeviceToken:(NSData *)token {
  deviceToken = token;
}

/**
 * 配置设备token
 */
- (void)configAppAPNSDeviceToken {
V2TIMManager *tm = [V2TIMManager sharedInstance];
  // APNS配置
  V2TIMAPNSConfig *apnsConfig = [V2TIMAPNSConfig new];
    apnsConfig.token=[NSData new];
  [tm setAPNS:apnsConfig succ:^{
//    IM_LOG_TAG_INFO(@"APNS", @"APNS配置成功");
  } fail:^(int code, NSString *msg) {
//    IM_LOG_TAG_WARN(@"APNS", @"APNS配置失败，错误码：%d，原因：%@", code, msg);
  }];
//  NSString *token = [NSString stringWithFormat:@"%@", deviceToken];
//  IM_LOG_TAG_INFO(@"SetToken", @"Token is : %@", token);

    
}


/**
 * 加入群
 * @param groupID 群id
 * @param succ 成功回调
 * @param fail 失败回调
 */
-(void)joinGroup:(NSString *)groupID succ:(V2TIMSucc)succ fail:(V2TIMFail)fail{
    V2TIMManager *tm = [V2TIMManager sharedInstance];
    [tm joinGroup:groupID msg:@""succ:succ fail:fail];
}

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
                       fail:(V2TIMFail)fail{
    V2TIMManager *tm = [V2TIMManager sharedInstance];
    [tm sendGroupTextMessage:text to:groupID priority:priority succ:succ fail:fail];
}

-(void)sendMessage:(V2TIMMessage *)message
        receiver:(NSString *)receiver
        to:(NSString *)groupID
        priority:(V2TIMMessagePriority)priority
        succ:(V2TIMSucc)succ
        fail:(V2TIMFail)fail
{
    
    V2TIMManager *tm = [V2TIMManager sharedInstance];
    [tm sendMessage:message receiver:receiver groupID:groupID priority:priority onlineUserOnly:NO offlinePushInfo:nil progress:^(uint32_t progress) {
        
    } succ:succ fail:fail];
}

-(void)getGroupHistoryMessageList:(NSString *)groupID
             count:(int)count
           msgID:(NSString *)msgID
        succ:(V2TIMMessageListSucc)succ
        fail:(V2TIMFail)fail
{
    V2TIMManager *tm = [V2TIMManager sharedInstance];
    if(msgID!=nil){
        NSLog(@"指定");
        [tm findMessages:@[msgID] succ:^(NSArray<V2TIMMessage *> *msgs) {

            if([msgs count]!=0){
                [tm getGroupHistoryMessageList:groupID count:count lastMsg:[msgs objectAtIndex:0] succ:succ fail:fail];
            }else{
                succ([NSArray<V2TIMMessage*> array]);

            }
            
        } fail:fail];
    }else{
        NSLog(@"未指定");
        [tm getGroupHistoryMessageList:groupID count:count lastMsg:nil succ:succ fail:fail];
    }
    
}
@end
