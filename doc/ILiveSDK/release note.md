## Release Note

### V1.6.0(2017-08-07)
 - 优化iLiveSDK登录及进出房间流程
 - 格式化iLiveSDK日志(统一配置IMSDK,AVSDK日志)
 - 绑定视频与view时添加不清除最后一帧配置
 - 更新AVSDK 1.9.2.13

### V1.5.3(2017-07-12)
 - 优化平板、分开渲染等相关的旋转问题
 - 添加PC播片支持
 - ILiveRoomOption中添加屏幕分享的获取模式配置接口screenRecvMode
 - 修复概率注销无回调问题
 - 修复房间被回收时摄像头未关闭问题

### V1.5.2(2017-06-21)
 - 修复横屏旋转与前后置摄像头的兼容问题

### V1.5.1(2017-06-16)
 - 优化静默模式
 - AVRootView中添加旋转校正setLocalRotationFix(本地)和setRemoteRotationFix(远程)*【对一些非常规机器，旋转方案不能匹配的，优先校正远程】*
 
### V1.5.0(2017-06-05)
 - 优化旋转功能(支持横屏)
 - 新增视频数上限为10路
 - ILiveRoomOption中添加主播镜像配置setHostMirror
 - 更新AVSDK到1.9.1.17（IMSDK 2.5.4.10006.10017）

### V1.4.2(2017-05-11)
 - 修复加入群组失败引起进房间异常问题
 
### V1.4.1(2017-04-26)
 - 修复无法指定IM群组问题

### V1.4.0(2017-04-25)
 - 更新AVSDK 1.9.0.10
 - 添加跨房[连麦功能](https://github.com/zhaoyang21cn/ILiveSDK_iOS_Demos/blob/master/doc/%E8%B7%A8%E6%88%BF%E8%BF%9E%E9%BA%A6.md)
 - 添加更多[特色功能](https://github.com/zhaoyang21cn/ILiveSDK_iOS_Demos/blob/master/doc/%E7%89%B9%E8%89%B2%E5%8A%9F%E8%83%BD.md)(滤镜、挂件、变声，动态角色，日志上报等)
 - 添加分开[渲染](https://github.com/zhaoyang21cn/ILiveSDK_Android_Demos/blob/master/doc/ILiveSDK/AndroidRenderIntr.md)支持
 - 优化群组绑定解接口可实现[大咖模式](https://github.com/zhaoyang21cn/ILiveSDK_Android_Demos/blob/master/doc/ILiveSDK/bigstar.md)
 - 优化登录逻辑等，修复切换房间异常等问题
---
 
### V1.3.4(2017-03-24)
 - 重构ILiveRoomManager推流接口startPushStream(部分参数做了调整)
 - 增加[日志上报](https://github.com/zhaoyang21cn/suixinbo_doc/blob/master/doc2/loguploader.md)功能
 - 增加[音视频测速](https://github.com/zhaoyang21cn/suixinbo_doc/blob/master/doc2/speed%20test.md)接口
 - ILiveRoomOption开放硬件编解码配置接口(enableHwEnc, enableHwDnc)
 - 添加拖动边界面保护(避免小AVVideoView拖出AVRootView)
 
### V1.3.3(2017-03-06)
 - 修正Android 7.0重复打LOG问题
 - 更新AVSDK 1.8.5.44 修正OPPO R9S黑屏问题
 - 重构录制接口
 - 进房可以配置是否开启AVSDK房间

 
### V1.3.2.2(2017-02-29)
 - 修复多个bugly的库冲突问题
 
### V1.3.2(2017-02-28)
 - 优化进出房间状态管理
 - 修复进房间10004问题
 - 细化WRONG_STATE[错误码](./error.md)
 - 优化质量数据获取接口[getQualityData](./quality.md)
 
### V1.3.1(2017-02-14)
 - 修正两处渲染空指针
 - 更新AVSDK1.8.5
 - 自动创建房间号算法更新
 
### V1.2.2(2017-01-22)
 - 修复横屏渲染问题
 - 优化房间内流程

### V1.2.1(2017-01-18)
 - 修复主播本地预览默认全屏模式
 - 修复渲染保护
 - 默认关闭初始化拉取最近联系人及消息(用到的用户请在初始化iLiveSDK后调用IMSDK接口开启)
 - 新版本随心播 更新后台协议，调整为独立模式，更新部分信令格式
 
### V1.2.0(2017-01-16)
 - 修改默认为支持后模式([参考文档](https://github.com/zhaoyang21cn/suixinbo_doc/blob/master/doc2/breakEvent.md))
 - 修复PC端多路视频不关闭问题
 - 开放surfaceView创建回调(AVRootView的setSurfaceCreateListener)
 - 优化旋转方案([参考文档](https://github.com/zhaoyang21cn/suixinbo_doc/blob/master/doc2/rotate.md))
 
 ---

 
### V1.1.1(2017-01-09)
- 修复低概率渲染空指针异常
- 修复绑定状态无画面被清除问题
- 修复一些极端情况下的异常问题
- 封装云API计算Signature接口

### V1.1.0 (2016-12-27)
- 更新到AVSDK 1.8.4.4 beta
- 优化注销流程，修复被踢资源未回收问题
- 在ILiveRoomManager添加切换房间接口switchRoom
- 在ILiveRoomManager添加切换角色接口changeRole，废弃~~changeAuthAndRole~~

---

### V1.0.6 (2016-12-21)
- PC多路流(摄像头，屏幕分享)支持，影响AVRootView中接口findUserViewIndex,closeUserView,bindIdAndView,getUserAvVideoView[需传视频类型，如AVView.VIDEO_SRC_TYPE_CAMERA]
- 优化视频请求流程

### V1.0.5 (2016-12-19)
- 修复状态未更新问题
- 更新音视频模块
- 添加arm-v7a支持

---

### V1.0.4 (2016-12-09)
- 优化进房间后音频服务及摄像头回调相关流程
- 优化美颜/美白接口，保证相互独立

### V1.0.3 (2016-12-07)
- 添加事件上报
- 添加[关键日志](./Logs.md)输出

### V1.0.2 (2016-12-02)
- 修复重复登录(或快速登录)导致异常
- 修复录制回调重复调用
- 在ILiveSDK中添加TIManager的获取

### V1.0.1 (2016-11-24)
- 优化日志(统一输出到/tencent/imsdklogs/包名)

### V1.0.0 (2016-11-23)
- 分离ILiveSDK的Core与business层(ILiveSDK=>ILiveSDK + [CallSDK](https://github.com/zhaoyang21cn/CallSDK_Android_Demo) + [LiveSDK](https://github.com/zhaoyang21cn/ILiveSDK_Android_Demos/blob/master/doc/ILiveSDK/ILVLiveManager.md))

---

### V0.4.1 (2016-11-22)
- 修复切换后置摄像头相关获取cameraId不准确问题
- 增加一个tlsLoginAll接口 一次性登录TLS IM AV 

### V0.4.0 (2016-11-21)
- 合并TLS登陆方式 和IOS对齐
- 屏幕旋转方案兼容转置
- 屏幕旋转方案兼容横屏landscape（只支持智能旋转和裁剪模式）
- 默认改为全屏模式
- Room的option中开放后台模式(videoMode可以设置后台、普通、静默三种模式)

### V0.3.8 (2016-11-17)
- 统一视频通话[错误码](./error.md)
- 修复getQualityData无下行速率问题

### V0.3.7 (2016-11-14)
- 修复自动渲染临时绑定未解绑问题
- 修复双人视频心跳相关问题
- 统一双人视频主叫方和接收方错误码

### V0.3.6 (2016-11-10)
- 双人视频的option中添加在线模式配置setOnlineCall
- AVRootView添加自动旋转配置setAutoOrientation
- 进房间option中添加录制视频纠正配置degreeFix
- 双人视频内部忽略sender为自己的信令
- 修正退出房间curCameraId未重置问题

### V0.3.5 (2016-11-08)
- 在AVVideoView中添加首帧到达事件回调setRecvFirstFrameListener
- 在ILiveRoomManager中添加获取当前打开摄像头id接口getActiveCameraId
- 在ILiveRoomManager中添加判断是否加入房间接口isEnterRoom
- 优化登录模块
- 重新实现摄像头对焦功能

### V0.3.4 (2016-11-02)
- 添加多人视频支持
- 修改自定义信令无效等bug

### V0.3.3 (2016-10-28)
- [在Room类新增质量数据获取接口(getQualityData)](./quality.md)
- 修正部分房间配置到Option(自动渲染、高清音质、应用回调，自动对焦等)
- 更新双人视频内部协议(添加心跳支持，可通过Call的Option调整间隔)
- 添加通知回调(ILVCallNotificationListener，可以Call的Option配置，支持自定义消息)
- 移动getMyUserId方法到ILiveLoginManager

### V0.3.1 (2016-10-20)
