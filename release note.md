##Release Note
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
