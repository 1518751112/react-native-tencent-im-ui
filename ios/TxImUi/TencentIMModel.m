//
//  UIViewController+TencentIMModel.m
//  fangbossApp
//
//  Created by 云超 on 2020/9/24.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import "TencentIMModel.h"


@implementation TencentIMModel{
    TencentIMMonitor * monitor;
}

RCT_EXPORT_MODULE(TencentIMModel);
-(id) init{
    //初始化监听事件
//    NSLog(@"初始化监听事件");
    V2TIMManager *tm = [V2TIMManager sharedInstance];
    TencentIMMonitor * m = [[TencentIMMonitor alloc] init:self];
    monitor =m;
    [tm addAdvancedMsgListener:m];
    [tm addGroupListener:m];
    
    return self;
}


- (NSArray<NSString *> *)supportedEvents
{
  return @[@"groupMessage",@"onMemberEnter",@"onMemberLeave",@"onMemberKicked",
           @"onGroupDismissed",@"onGrantAdministrator",@"onRevokeAdministrator"
  ];
}

/**
 * 初始化SDK
 * @param configFilePath 配置文件路径，默认为mainBundle下的txim.plist
 */
RCT_EXPORT_METHOD(initSdk:(int)sdkAppId)
{
  TencentIMManager *tm = [TencentIMManager getInstance];
  [tm initSdk:sdkAppId];
}

/**
 * 用户登录
 * @param identify identify
 * @param userSig userSig
 * @param succ 成功回调
 * @param fail 失败回调
 */
RCT_EXPORT_METHOD(login:(NSString *)identify
                  userSig:(NSString *)userSig
                     resolver:(RCTPromiseResolveBlock)resolve
                     rejecter:(RCTPromiseRejectBlock)reject)
{
  TencentIMManager *tm = [TencentIMManager getInstance];
  [tm loginWithIdentify:identify
                       userSig:userSig
                       succ:^{
      V2TIMManager *txManager = [V2TIMManager sharedInstance];
      [txManager getUsersInfo:@[identify] succ:^(NSArray<V2TIMUserFullInfo *> *infoList) {
          V2TIMUserFullInfo *info =infoList[0];
          resolve(@{
            @"code": @(0),
            @"msg": @"登录成功!",
            @"module": @"success",
            @"userInfo":@{
                    @"userID":info.userID,
                    @"nickName":info.nickName? info.nickName:@"",
                    @"avatarPic":info.faceURL
            }
          });
      } fail:^(int code, NSString *desc) {
          reject([NSString stringWithFormat:@"%d", code], desc, nil);
      }];
                         
                       }
                       fail:^(int code, NSString *msg) {
                         reject([NSString stringWithFormat:@"%d", code], msg, nil);
                       }];
}

/**
 * 用户登出
 * @param succ 成功回调
 * @param fail 失败回调
 */
RCT_EXPORT_METHOD(logout:(RCTPromiseResolveBlock)resolve
rejecter:(RCTPromiseRejectBlock)reject){
  TencentIMManager *tm = [TencentIMManager getInstance];
  [tm logoutWithSucc:^{
    resolve(@(YES));
  }                  fail:^(int code, NSString *msg) {
    reject([NSString stringWithFormat:@"%@", @(code)], msg, nil);
  }];
}

RCT_EXPORT_METHOD(joinGroup:(NSString *)groupID resolve:(RCTPromiseResolveBlock)resolve
rejecter:(RCTPromiseRejectBlock)reject){
  TencentIMManager *tm = [TencentIMManager getInstance];
  [tm joinGroup:groupID succ:^{
      resolve(@{@"code":@0,@"desc":@"加入群成功"});
  }                  fail:^(int code, NSString *msg) {
    reject([NSString stringWithFormat:@"%@", @(code)], msg, nil);
  }];
}

RCT_EXPORT_METHOD(sendGroupTextMessage:(NSString *)text
                  to:(NSString *)groupID
                 priority:(NSInteger)priority
                  resolve:(RCTPromiseResolveBlock)resolve
rejecter:(RCTPromiseRejectBlock)reject)
{
  TencentIMManager *tm = [TencentIMManager getInstance];
    [tm sendGroupTextMessage:text to:groupID priority:priority succ:^{
        resolve(@{@"code":@0,@"desc":@"发送成功"});
    } fail:^(int code, NSString *desc) {
        reject([NSString stringWithFormat:@"%@", @(code)], desc, nil);
    }];
}

RCT_EXPORT_METHOD(sendGroupImageMessage:(NSString *)imagePath
                  receiver:(NSString *)receiver
                  to:(NSString *)groupID
                 priority:(NSInteger)priority
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
  TencentIMManager *tm = [TencentIMManager getInstance];
    V2TIMManager *V2tm = [V2TIMManager sharedInstance];
    V2TIMMessage *message = [V2tm createImageMessage:imagePath];
    
    [tm sendMessage:message receiver:receiver to:groupID priority:priority succ:^{
        resolve(@{@"code":@0,@"desc":@"发送成功"});
    } fail:^(int code, NSString *desc) {
        reject([NSString stringWithFormat:@"%@", @(code)], desc, nil);
    }];
}

RCT_EXPORT_METHOD(getGroupHistoryMessageList:(NSString *)groupID
                  count:(int)count
                msgID:(NSString *)msgID
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    NSLog(@"msgID%@",msgID);
    if ([msgID isEqual:@"null"]) {
        NSLog(@"是否为null:%@",msgID);
        msgID=nil;
    }
  TencentIMManager *tm = [TencentIMManager getInstance];
    [tm getGroupHistoryMessageList:groupID count:count msgID:msgID succ:^(NSArray<V2TIMMessage *> *msgs) {
        NSLog(@"获取消息列表");
        NSMutableArray<NSMutableDictionary *> * arr = [NSMutableArray array];
        for (V2TIMMessage *msg in msgs) {
            [arr addObject:[TencentIMMonitor MessageSort:msg]];
        }
        resolve(arr);
    } fail:^(int code, NSString *desc) {
        NSLog(@"获出错了");
        reject([NSString stringWithFormat:@"%@", @(code)], desc, nil);
    }];
}

RCT_EXPORT_METHOD(getGroupOnlineMemberCount:(NSString *)groupID
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    V2TIMManager *txManager = [V2TIMManager sharedInstance];
    
    [txManager getGroupOnlineMemberCount:groupID succ:^(NSInteger count) {
        resolve(@{
            @"code":@0,
            @"number":[NSNumber numberWithInteger:count]
                });
    } fail:^(int code, NSString *desc) {
        reject([NSString stringWithFormat:@"%@", @(code)], desc, nil);
    }];
}

RCT_EXPORT_METHOD(setSelfInfo:(NSDictionary *)config
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    V2TIMManager *txManager = [V2TIMManager sharedInstance];
    V2TIMUserFullInfo * info = [[V2TIMUserFullInfo alloc] init];
    if(config[@"nickName"]){
        [info setNickName:config[@"nickName"]];
    }
    if(config[@"avatarPic"]){
        [info setFaceURL:config[@"avatarPic"]];
    }
    [txManager setSelfInfo:info succ:^{
        resolve(@{
            @"code":@0,
            @"desc":@"修改成功"
                });
    } fail:^(int code, NSString *desc) {
        NSLog(@"出问题了:%@",desc);
        reject([NSString stringWithFormat:@"%@", @(code)], desc, nil);
    }];
}
@end
