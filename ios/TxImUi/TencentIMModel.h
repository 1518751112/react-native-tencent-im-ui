//
//  UIViewController+RNTIMModel.h
//  fangbossApp
//
//  Created by 云超 on 2020/9/24.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import "TUIKit.h"
#import <React/RCTViewManager.h>
#import <ImSDK_Plus/V2TIMManager.h>
#import <ImSDK_Plus/ImSDK_Plus.h>
#import <React/RCTBridgeModule.h>
#import <ImSDK_Plus/V2TIMManager+Message.h>
#import "TencentIMManager.h"
#import "TencentIMMonitor.h"
#import <React/RCTEventEmitter.h>
@class TencentIMMonitor;

@interface TencentIMModel : RCTEventEmitter <RCTBridgeModule>
-(id) init;
- (NSArray<NSString *> *)supportedEvents;
@end

