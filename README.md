# react-native-txim-ui 腾讯云即时通信 IM 服务的react-native，使用原生ui版本得sdk
起因，项目中需要用到基础的im功能（聊天和聊天列表），晚上搜了一圈也没有找到，技术栈已经定好，也只能硬着头皮搞了。

直接给大家分享出来，目前功能简单，如果有简单需求的可以直接使用。

当前基于 TIMSDK UI版本 标准版 5.0.6 @2020.09.18

项目地址： [https://github.com/mengyou658/react-native-txim-ui](https://github.com/mengyou658/react-native-txim-ui)

## 支持功能
1. 聊天列表功能
1. 聊天功能

## 待支持的功能
1. 不支持自定义界面（可以yarn install 后，在node_models/react-native-txim-ui/更改里面的代码或者直接clone项目复制先来粘贴过去改吧，虽然不方便，但是也能实现，一个个封装代码都不够项目成本的😂）
1. 离线消息
1. 用户信息编辑
1. 加好友
1. 等等。。。

## 支持版本
react-native 0.60 以上版本
## 如何安装
### 1.安装包

`$ npm install react-native-txim-ui --save`

### 2. link
react-native 0.60以上 使用的autolink
## Usage
```javascript
import TximUi from 'react-native-txim-ui';

// TODO: What to do with the module?
TximUi;
```
