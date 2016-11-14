##Release Note

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
- [在Room类新增质量数据获取接口(getQualityData)](/doc/ILiveSDK/quality.md)
- 修正部分房间配置到Option(自动渲染、高清音质、应用回调，自动对焦等)
- 更新双人视频内部协议(添加心跳支持，可通过Call的Option调整间隔)
- 添加通知回调(ILVCallNotificationListener，可以Call的Option配置，支持自定义消息)
- 移动getMyUserId方法到ILiveLoginManager

###V0.3.1 (2016-10-20)
