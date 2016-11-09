# ILiveSDK
ILiveSDK 提供了账号登录，音视频互动，文本互动等基础功能，顺利的话一天之内即可集成音视频能力。

支持以下场景     
>* 视频直播类 
     类似now直播,映客 一人直播,多人观看,发文本消息,赞,送礼物。    
>* [视频聊天类](./ILVCallManager.md) 
     类似微信视频通话功能呢,支持多人同时上麦(最多4路)。

##ILiveSDK导入
ILiveSDK在Android Studio上开发。
导入只需要在gradle里增加一行（后面是版本号）,查看[版本更新说明](./release note.md)

compile 'com.tencent.ilivesdk:ilivesdk:0.3.5'



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
            ILiveRoomManager.getInstance().createRoom(room, hostOption, new ILiveCallBack() {
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
有三个示例  <br />
1视频聊天 ：双人通话场景的简单示例，类微信视频聊天。    
2基础直播 ：互动直播基础演示基础功能 登录进房间 加入房间 发消息 自定义消息等     
3新随心播 ：基于ILiveSDK接口重构的随心播        
##API文档
[API文档](https://zhaoyang21cn.github.io/ilivesdk_help/android_help/)

##已知问题

由于目前只支持armeabi架构，如果工程(或依赖库)中有多架构，需要在build.gradle中添加以下配置
<pre>
android{
    defaultConfig{
        ndk{
            abiFilter 'armeabi'
        }
    }
}
</pre>

如果您还在使用eclipse进行Android的开发，请参考这里[eclipse集成方案](./eclipse_readme.md)。    
Android Studio在google支持度，编译便利性等多方面远超eclipse。我们强烈建议还在使用eclipse的用户尽快升级。



