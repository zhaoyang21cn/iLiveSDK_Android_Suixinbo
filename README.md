# ILiveSDK
ILiveSDK 提供了账号登录，音视频互动，文本互动等直播类应用的基础功能

##ILiveSDK导入
在gradle里增加一行（后面是版本号）    

compile 'com.tencent.ilivesdk:ilivesdk:0.2.0'

##基础使用
###初始化SDK
ILiveSDK.getInstance().initSdk(getApplicationContext(), appid, accoutype);



###账号登录
使用托管方式或独立模式，在获取到用户的sig后，使用登陆接口，完成相应初始化（包括avsdk）

      ILiveLoginManager.getInstance().tilvbLogin(ILiveSDK.getInstance().getMyUserId(), "123456", new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    bLogin = true;
                    Toast.makeText(ContactActivity.this, "login success !", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(ContactActivity.this, module + "|login fail " + errCode + " " + errMsg, Toast.LENGTH_SHORT).show();
                }
            });
            
###创建房间
            //创建房间配置项
            ILiveRoomOption hostOption = new ILiveRoomOption(null).
                    controlRole("Host")//角色设置
                    .authBits(AVRoomMulti.AUTH_BITS_DEFAULT)//权限设置
                    .cameraId(ILiveConstants.FRONT_CAMERA)//摄像头前置后置
                    .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO);//是否开始半自动接收
            //创建房间
            ILVLiveManager.getInstance().createRoom(room, hostOption, new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    Toast.makeText(LiveActivity.this, "create room  ok", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    Toast.makeText(LiveActivity.this, module + "|create fail " + errMsg + " " + errMsg, Toast.LENGTH_SHORT).show();
                }
            });
            
###渲染

    <com.tencent.ilivesdk.view.AVRootView
        android:id="@+id/av_root_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@color/white" />
        
        
        avRootView = (AVRootView) findViewById(R.id.av_root_view);
        ILVLiveManager.getInstance().setAvVideoView(avRootView);


            
##DEMO
有三个示例    
1双人视频 ：双人通话场景的简单示例，类微信视频聊天。    
2广播电台 ：纯音频场景的示例，演示混音功能     
3基础直播 ：互动直播基础演示基础功能 登录进房间 加入房间 发消息 自定义消息等
4新随心播 ：基于ILiveSDK接口重构的随心播
##API文档
在DEMO工程中附带了一份API文档 
	

