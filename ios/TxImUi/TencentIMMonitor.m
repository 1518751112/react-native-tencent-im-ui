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

@end
