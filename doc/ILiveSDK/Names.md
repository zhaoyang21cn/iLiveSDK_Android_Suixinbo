## 直播中一些专有名词解释

### 直播房间    
在随心播中，直播房间是指的音视频AVRoom和聊天室AVChatRoom的整合。    
**音视频AVROOM** 主要负责音视频流的拉取，房间ID是32位整型。     
**聊天室AVChatRoom** 是消息和信令通道，包括文本，礼物，赞等，房间ID是String   
 
随心播创建房间过程是先创建一个音视频AVROOM （例如房间ID是int1235），在成功的回调里面再创建直播聊天室（ID是String类型1235），最后把回调抛回给用户。      



```java            
  //创建房间配置项
            ILiveRoomOption hostOption = new ILiveRoomOption(null)
                    .controlRole(Constants.HOST_ROLE)//角色设置
                    .authBits(AVRoomMulti.AUTH_BITS_DEFAULT)//权限设置
                    .cameraId(ILiveConstants.FRONT_CAMERA)//摄像头前置后置
                    .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO);//是否开始半自动接收
           //创建房间
            ILiveRoomManager.getInstance().createRoom(room, hostOption, new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    Toast.makeText(LiveActivity.this, "create room  ok", Toast.LENGTH_SHORT).show();
                    logoutBtn.setVisibility(View.INVISIBLE);
                    backBtn.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(LiveActivity.this, module + "|create fail " + errMsg + " " + errMsg,   Toast.LENGTH_SHORT).show();
                }
            });
```

如果在Option里面配置imsupport(false)则仅仅单独创建AV音视频房间，业务方可以根据业务需求灵活配置。


### 直播房间生命周期
正常情况下，退出房间主动退出音视频AVROOM，会主动结束掉聊天室AVChatRoom。    
异常情况下，例如主播Crash 音视频AVROOM房间内无上下行流量超过30S AVROOM会被系统回收。聊天室AVChatRoom主播Crash不会主动回收房间。
