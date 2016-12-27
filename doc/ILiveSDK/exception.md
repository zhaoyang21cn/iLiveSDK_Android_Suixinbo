## 异常事件

ILiveSDK封装了创建/加入房间的相关操作，中间有可能会出现非主线流程的失败，此时会通过异常抛出

### 监听异常事件
可在创建或加入房间的Option中配置异常事件回调
```java
ILiveRoomOption option = new ILiveRoomOption(strHostId)
                .exceptionListener(this)
                .imsupport(false);
```

### 异常事件回调

接口名|接口描述
:--|:--:
onException|异常事件回调

参数类型|参数名|说明
:--|:--|:--:
int   |exceptionId |异常事件id
int   |errCode     |异常事件错误码
String|errMsg      |异常事件错误描述

```java
@Override
public void onException(int exceptionId, int errCode, String errMsg) {
    switch (exceptionId){
    }
}
```

### 异常事件描述

常量定义|异常id|异常描述
:--|--:|:--:
EXCEPTION_IMROOM_EXIST          |1|要创建的IM房间已存在
EXCEPTION_ALREADY_MEMBER        |2|已是房间成功
EXCEPTION_ENABLE_CAMERA_FAILED  |3|打开摄像头失败
EXCEPTION_ENABLE_MIC_FAILED     |4|打开Mic失败
EXCEPTION_NO_ROOT_VIEW          |5|没有配置AVRootView
EXCEPTION_REQUEST_VIDEO_FAILED  |6|请求用户画面失败
EXCEPTION_RENDER_USER_FAILED    |7|渲染用户画面失败
EXCEPTION_MESSAGE_EXCEPTION     |8|发送信令失败
