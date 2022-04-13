//
//  UIViewController+RNTIMConversationModel.m
//  fangbossApp
//
//  Created by 云超 on 2020/9/24.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import "TencentIMConversationModel.h"
#import "TUIKit.h"
#import <ImSDK_Plus/V2TIMManager.h>
#import <ImSDK_Plus/ImSDK_Plus.h>
#import "TencentIMManager.h"
#import "TUIConversationListController.h"
@implementation TencentIMConversationModel

RCT_EXPORT_MODULE();
- (UIView *)view
{
  return [[ConversationView alloc] init];
}
@end
