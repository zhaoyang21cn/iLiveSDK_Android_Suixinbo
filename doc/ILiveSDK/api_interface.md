## iLiveSDK接口概述

- ILiveSDK类
    - 初始化SDK(initSDK)
    - 设置通道(setChannelMode)
- ILiveLoginManager类
    - 登录(iLiveLogin)
    - 注销(iLiveLogout)
- ILiveRoomManager类
    - 基础接口
        - 初始化房间模块(init)
        - 设置渲染控件(initAvRootView)
        - 创建房间(createRoom)
        - 加入房间(joinRoom)
        - 切换房间（switchRoom)
        - 退出房间(quitRoom)
    - 设备相关
        - 操作扬声器(enableSpeaker)
        - 操作摄像头(enableCamera)
        - 切换前后置摄像头(switchCamera)
        - 操作麦克风(enableMic)
    - 音视频配置
        - 切换角色(changeRole)
    - 聊天相关
        - 发送群组消息(sendGroupMessage)
        - 发送C2C消息(sendC2CMessage)



详细参考[API文档](https://zhaoyang21cn.github.io/iLiveSDK_Help/android_help/)
