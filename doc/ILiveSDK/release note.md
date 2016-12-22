##Release Note
###V1.0.6 (2016-12-21)
- PC多路流(摄像头，屏幕分享)支持，影响AVRootView中接口findUserViewIndex,closeUserView,bindIdAndView,getUserAvVideoView[需传视频类型，如AVView.VIDEO_SRC_TYPE_CAMERA]
- 优化视频请求流程

###V1.0.5 (2016-12-19)
- 修复状态未更新问题
- 更新音视频模块
- 添加arm-v7a支持

###V1.0.4 (2016-12-09)
- 优化进房间后音频服务及摄像头回调相关流程
- 优化美颜/美白接口，保证相互独立

###V1.0.3 (2016-12-07)
- 添加事件上报
- 添加[关键日志](./Logs.md)输出

###V1.0.2 (2016-12-02)
- 修复重复登录(或快速登录)导致异常
- 修复录制回调重复调用
- 在ILiveSDK中添加TIManager的获取

###V1.0.1 (2016-11-24)
- 优化日志(统一输出到/tencent/imsdklogs/包名)

###V1.0.0 (2016-11-23)
- 分离ILiveSDK的Core与business层(ILiveSDK=>ILiveSDK + [CallSDK](https://github.com/zhaoyang21cn/CallSDK_Android_Demo) + [LiveSDK](https://github.com/zhaoyang21cn/ILiveSDK_Android_Demos/blob/master/doc/ILiveSDK/ILVLiveManager.md))

---

###V0.4.1 (2016-11-22)
- 修复切换后置摄像头相关获取cameraId不准确问题
- 增加一个tlsLoginAll接口 一次性登录TLS IM AV 

###V0.4.0 (2016-11-21)
- 合并TLS登陆方式 和IOS对齐
- 屏幕旋转方案兼容转置
- 屏幕旋转方案兼容横屏landscape（只支持智能旋转和裁剪模式）
- 默认改为全屏模式
- Room的option中开放后台模式(videoMode可以设置后台、普通、静默三种模式)

###V0.3.8 (2016-11-17)
- 统一视频通话[错误码](./error.md)
- 修复getQualityData无下行速率问题

###V0.3.7 (2016-11-14)
- 修复自动渲染临时绑定未解绑问题
- 修复双人视频心跳相关问题
- 统一双人视频主叫方和接收方错误码

###V0.3.6 (2016-11-10)
- 双人视频的option中添加在线模式配置setOnlineCall
- AVRootView添加自动旋转配置setAutoOrientation
- 进房间option中添加录制视频纠正配置degreeFix
- 双人视频内部忽略sender为自己的信令
- 修正退出房间curCameraId未重置问题

###V0.3.5 (2016-11-08)
- 在AVVideoView中添加首帧到达事件回调setRecvFirstFrameListener
- 在ILiveRoomManager中添加获取当前打开摄像头id接口getActiveCameraId
- 在ILiveRoomManager中添加判断是否加入房间接口isEnterRoom
- 优化登录模块
- 重新实现摄像头对焦功能

###V0.3.4 (2016-11-02)
- 添加多人视频支持
- 修改自定义信令无效等bug

###V0.3.3 (2016-10-28)
- [在Room类新增质量数据获取接口(getQualityData)](./quality.md)
- 修正部分房间配置到Option(自动渲染、高清音质、应用回调，自动对焦等)
- 更新双人视频内部协议(添加心跳支持，可通过Call的Option调整间隔)
- 添加通知回调(ILVCallNotificationListener，可以Call的Option配置，支持自定义消息)
- 移动getMyUserId方法到ILiveLoginManager

###V0.3.1 (2016-10-20)
