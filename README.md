# iLiveSDK
iLiveSDK 提供了账号登录，音视频互动，文本互动等基础功能，顺利的话一天之内即可集成音视频能力。

![](https://zhaoyang21cn.github.io/ilivesdk_help/readme_img/ilivesdk_construction.png)

支持以下场景     
>* [视频直播类]
     类似now直播,映客 一人直播,多人观看,发文本消息,赞,送礼物。[具体参考LiveSDK](/doc/ILiveSDK/ILVLiveManager.md)
>* [视频聊天类]
     类似微信视频通话功能呢,支持多人同时上麦(最多4路)。[具体参考CallSDK](https://github.com/zhaoyang21cn/CallSDK)

##iLiveSDK导入
iLiveSDK在Android Studio上开发。
导入只需要在gradle里增加两行（后面是版本号）,查看[版本更新说明](/doc/ILiveSDK/release note.md)


直播业务功能       
compile 'com.tencent.livesdk:livesdk:1.0.2'      
核心功能     
compile 'com.tencent.ilivesdk:ilivesdk:1.0.6'      
            

##DEMO
有两个示例 <br />
1简单直播 ：直播主线流程示例  <br />
2新随心播 ：基于ILiveSDK接口重构的随心播   

##API文档
[API文档(1.0.5)](https://zhaoyang21cn.github.io/ilivesdk_help/android_help/)

##异常事件
[异常事件](/doc/ILiveSDK/exception.md)

##错误码
[错误码表](/doc/ILiveSDK/error.md)

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

如果您还在使用eclipse进行Android的开发，请参考这里[eclipse集成方案](/doc/ILiveSDK/eclipse_readme.md)。    
Android Studio在google支持度，编译便利性等多方面远超eclipse。我们强烈建议还在使用eclipse的用户尽快升级。

##关键路径LOG
[关键路径LOG 请遇到问题先自行对比](/doc/ILiveSDK/Logs.md)

