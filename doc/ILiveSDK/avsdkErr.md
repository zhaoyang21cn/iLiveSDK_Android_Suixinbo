## AVSDK 模块错误码


错误码名称|错误码|含义|原因
:--|:--|:--:|:--:
AV_ERR_FAILED                          |1      |一般错误         |具体原因需要通过分析日志等来定位
AV_ERR_REPEATED_OPERATION              |1001   |重复操作         |已经在进行某种操作，再次去做同样的操作
AV_ERR_EXCLUSIVE_OPERATION             |1002   |互斥操作         |上次相关操作尚未完成
AV_ERR_HAS_IN_THE_STATE                |1003   |状态已就绪       |对象已经处于将要进入的某种状态
AV_ERR_INVALID_ARGUMENT                |1004   |错误参数         |传入错误的参数
AV_ERR_TIMEOUT                         |1005   |超时             |在规定的时间内，还未返回操作结果
AV_ERR_NOT_IMPLEMENTED                 |1006   |未实现           |相应的功能还未支持
AV_ERR_NOT_IN_MAIN_THREAD              |1007   |不在主线程       |SDK对外接口要求在主线程执行
AV_ERR_RESOURCE_IS_OCCUPIED            |1008   |资源被占用       |需要用到某种资源被占用了
AV_ERR_CONTEXT_NOT_EXIST               |1101   |状态未就绪       |AVContext非CONTEXT_STATE_STARTED状态
AV_ERR_CONTEXT_NOT_STOPPED             |1102   |状态未就绪       |AVContext非CONTEXT_STATE_STOPPED状态
AV_ERR_ROOM_NOT_EXIST                  |1201   |状态未就绪       |AVRoom非ROOM_STATE_ENTERED状态
AV_ERR_ROOM_NOT_EXITED                 |1202   |状态未就绪       |AVRoom非ROOM_STATE_EXITED状态    
AV_ERR_DEVICE_NOT_EXIST                |1301   |设备不存在       |设备不存在或者设备初始化未完成
AV_ERR_ENDPOINT_NOT_EXIST              |1401   |对象不存在       |成员未发语音或视频时去获取AVEndpoint
AV_ERR_ENDPOINT_HAS_NOT_VIDEO          |1402   |没有发视频       |成员未发视频时去做需要发视频的相关操作
AV_ERR_TINYID_TO_OPENID_FAILED         |1501   |转换失败         |信令解析出错
AV_ERR_OPENID_TO_TINYID_FAILED         |1502   |转换失败         |初始化转换失败
AV_ERR_DEVICE_TEST_NOT_EXIST           |1601   |状态未就绪       |AVDeviceTest对象状态异常(windows特有)    
AV_ERR_DEVICE_TEST_NOT_STOPPED         |1602   |状态未就绪       |AVDeviceTest对象状态异常（windows特有）    
AV_ERR_INVITE_FAILED                   |1801   |发送失败         |发送邀请时产生的失败
AV_ERR_ACCEPT_FAILED                   |1802   |接受失败         |接受邀请时产生的失败
AV_ERR_REFUSE_FAILED                   |1803   |拒绝失败         |拒绝邀请时产生的失败
AV_ERR_SERVER_FAILED                   |10001  |一般错误         |具体原因需要通过分析日志确认
AV_ERR_SERVER_INVALID_ARGUMENT         |10002  |错误参数         |错误的参数
AV_ERR_SERVER_NO_PERMISSION            |10003  |没有权限         |没有权限使用某个功能
AV_ERR_SERVER_TIMEOUT                  |10004  |超时             |具体原因需要通过分析日志确认
AV_ERR_SERVER_ALLOC_RESOURCE_FAILED    |10005  |资源不够         |分配更多的资源(如内存)失败了
AV_ERR_SERVER_ID_NOT_IN_ROOM           |10006  |不在房间         |在不在房间内时，去执行某些操作
AV_ERR_SERVER_NOT_IMPLEMENT            |10007  |未实现           |调用SDK接口时，如果相应的功能还未支持
AV_ERR_SERVER_REPEATED_OPERATION       |10008  |重复操作         |具体原因需要通过分析日志确认
AV_ERR_SERVER_ROOM_NOT_EXIST           |10009  |房间不存在       |房间不存在时，去执行某些操作
AV_ERR_SERVER_ENDPOINT_NOT_EXIST       |10010  |成员不存在       |某个成员不存在时，去执行该成员相关的操作
AV_ERR_SERVER_INVALID_ABILITY          |10011  |错误能力         |具体原因需要通过分析日志确认
