
### 统一回调事件

在统一类**ILiveSDK**中，添加方法**addEventListener**接口,用于监听处理回调事件(用户可以重写需要处理的事件)

Android添加:
```Java
ILiveSDK.getInstance().addEventHandler(this);
```

iOS添加:
```ObjC
[ILiveSDK getInstance].ilveEventListener = self;
```

- 登录事件
    - [登录成功事件onLoginSuccess](#登录成功事件onloginsuccess)
    - [登录失败事件onLoginFailed](#登录失败事件onloginfailed)
    - [注销成功事件onLogoutSuccess](#注销成功事件onlogoutsuccess)
    - [注销失败事件onLogoutFailed](#注销失败事件onlogoutfailed)
    - [帐号下线事件onForceOffline](#帐号下线事件onforceoffline)
    - [自定义角色配置事件onSetSpearConfigEvent](#自定义角色配置事件onsetspearconfigevent)
- 房间事件
    - [创建房间成功事件onCreateRoomSuccess](#创建房间成功事件oncreateroomsuccess)
    - [创建房间失败事件onCreateRoomFailed](#创建房间失败事件oncreateroomfailed)
    - [加入房间成功事件onJoinRoomSuccess](#加入房间成功事件onjoinroomsuccess)
    - [加入房间失败事件onJoinRoomFailed](#加入房间失败事件onjoinroomfailed)
    - [退出房间成功事件onQuitRoomSuccess](#退出房间成功事件onquitroomsuccess)
    - [退出房间失败事件onQuitRoomFailed](#退出房间失败事件onquitroomfailed)
    - [房间断开连接事件onRoomDisconnected](#房间断开连接事件onroomdisconnected)
    - [聊天群组解散事件onGroupDisband](#聊天群组解散事件ongroupdisband)
- 状态事件
    - [成员进入房间回调onRoomMemberIn](#成员进入房间回调onroommemberin)
    - [成员离开房间事件onRoomMemberOut](#成员离开房间事件onroommemberout)
    - [摄像头状态变更事件onCameraUpdate](#摄像头状态变更事件oncameraupdate)
    - [摄像头操作失败onCameraFailed](#摄像头操作失败oncamerafailed)
    - [视频上行开始事件onRoomHasVideo](#视频上行开始事件onroomhasvideo)
    - [视频上行结束事件onRoomNoVideo](#视频上行结束事件onroomnovideo)
    - [音频上行开始事件onRoomHasAudio](#音频上行开始事件onroomhasaudio)
    - [音频上行结束事件onRoomNoAudio](#音频上行结束事件onroomnoaudio)
    - [视频数据到达事件onRecvVideoEvent](#视频数据到达事件onrecvvideoevent)

#### 登录成功事件onLoginSuccess
由登录接口iLiveLogin接口产生

参数名|参数类型|描述
:--:|:--:|:--
userId|String|用户登录标识

#### 登录失败事件onLoginFailed
由登录接口iLiveLogin接口产生

参数名|参数类型|描述
:--:|:--:|:--
userId|String|用户登录标识
module|String|错误模块
errCode|int|错误id
errMsg|String|错误描述

#### 注销成功事件onLogoutSuccess
由注销接口iLiveLogout接口产生

参数名|参数类型|描述
:--:|:--:|:--
userId|String|用户登录标识

#### 注销失败事件onLogoutFailed
由注销接口iLiveLogout接口产生

参数名|参数类型|描述
:--:|:--:|:--
userId|String|用户登录标识
module|String|错误模块
errCode|int|错误id
errMsg|String|错误描述


#### 帐号下线事件onForceOffline
登录签名过期或帐号在其它设备登录时产生

参数名|参数类型|描述
:--:|:--:|:--
userId|String|用户登录标识
module|String|错误模块
errCode|int|错误id
errMsg|String|错误描述

#### 自定义角色配置事件onSetSpearConfigEvent
调用iLiveLoginWithSpear接口登录时参生

参数名|参数类型|描述
:--:|:--:|:--
result|int|配置结果,0为成功
errMsg|String|错误描述


#### 创建房间成功事件onCreateRoomSuccess
由创建房间createRoom接口产生

参数名|参数类型|描述
:--:|:--:|:--
roomId|int|音视频房间id
groupId|String|IM聊天群组id

#### 创建房间失败事件onCreateRoomFailed
由创建房间createRoom接口产生

参数名|参数类型|描述
:--:|:--:|:--
roomId|int|音视频房间id
module|String|错误模块
errCode|int|错误id
errMsg|String|错误描述

#### 加入房间成功事件onJoinRoomSuccess
由加入房间joinRoom接口产生

参数名|参数类型|描述
:--:|:--:|:--
roomId|int|音视频房间id
groupId|String|IM聊天群组id

#### 加入房间失败事件onJoinRoomFailed
由加入房间joinRoom接口产生

参数名|参数类型|描述
:--:|:--:|:--
roomId|int|音视频房间id
module|String|错误模块
errCode|int|错误id
errMsg|String|错误描述


### 成员进入房间回调onRoomMemberIn
有新成员加入聊天室时产生
> *依赖IM模块的群组成员事件通知*

参数名|参数类型|描述
:--:|:--:|:--
roomId|int|音视频房间id
groupId|String|IM聊天群组id
userId|String|加入的用户id

### 成员离开房间事件onRoomMemberOut
有成员离开聊天室时产生
> *依赖IM模块的群组成员事件通知*

参数名|参数类型|描述
:--:|:--:|:--
roomId|int|音视频房间id
groupId|String|IM聊天群组id
userId|String|加入的用户id

#### 退出房间成功事件onQuitRoomSuccess
由退出房间quitRoom接口产生

参数名|参数类型|描述
:--:|:--:|:--
roomId|int|音视频房间id
groupId|String|IM聊天群组id

#### 退出房间失败事件onQuitRoomFailed
由退出房间quitRoom接口产生

参数名|参数类型|描述
:--:|:--:|:--
roomId|int|音视频房间id
module|String|错误模块
errCode|int|错误id
errMsg|String|错误描述


### 房间断开连接事件onRoomDisconnected
一般由网络中断，或后台长时间没有收到房间内的上行数据时产生

参数名|参数类型|描述
:--:|:--:|:--
roomId|int|音视频房间id
module|String|错误模块
errCode|int|错误id
errMsg|String|错误描述

### 聊天群组解散事件onGroupDisband
由群组创建者解散聊天群组时产生

参数名|参数类型|描述
:--:|:--:|:--
roomId|int|音视频房间id
groupId|String|IM聊天群组id


### 摄像头状态变更事件onCameraUpdate
由摄像头操作接口产生

参数名|参数类型|描述
:--:|:--:|:--
cameraId|int|摄像头id
enable|boolean|是否开启

### 摄像头操作失败onCameraFailed
由摄像头操作接口产生

参数名|参数类型|描述
:--:|:--:|:--
module|String|错误模块
errCode|int|错误id
errMsg|String|错误描述

### 视频上行开始事件onRoomHasVideo
房间内有视频上行数据时产生

参数名|参数类型|描述
:--:|:--:|:--
roomId|int|音视频房间id
videoType|int|视频数据类型(摄像头，屏幕，文件)
userId|String|用户标识

### 视频上行结束事件onRoomNoVideo
房间内有视频上行数据中断时产生

参数名|参数类型|描述
:--:|:--:|:--
roomId|int|音视频房间id
videoType|int|视频数据类型(摄像头，屏幕，文件)
userId|String|用户标识

### 音频上行开始事件onRoomHasAudio
房间内有音频上行数据时产生

参数名|参数类型|描述
:--:|:--:|:--
roomId|int|音视频房间id
userId|String|用户标识

### 音频上行结束事件onRoomNoAudio
房间内有音频上行数据中断时产生

参数名|参数类型|描述
:--:|:--:|:--
roomId|int|音视频房间id
userId|String|用户标识

### 视频数据到达事件onRecvVideoEvent
房间内有首次收到用户视频数据时产生

参数名|参数类型|描述
:--:|:--:|:--
videoType|int|视频数据类型(摄像头，屏幕，文件)
userId|String|用户标识
