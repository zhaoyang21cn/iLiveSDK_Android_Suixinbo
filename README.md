# iLiveSDK
iLiveSDK 提供了账号登录，音视频互动，文本互动等基础功能，顺利的话一天之内即可集成音视频能力。

![](https://zhaoyang21cn.github.io/ilivesdk_help/readme_img/ilivesdk_construction.png)

支持以下场景     
>* [视频直播类]
     类似now直播,映客 一人直播,多人观看,发文本消息,赞,送礼物。[具体参考LiveSDK](/doc/ILiveSDK/ILVLiveManager.md)
>* [视频聊天类]
     类似微信视频通话功能呢,支持多人同时上麦(最多4路)。[具体参考CallSDK](https://github.com/zhaoyang21cn/CallSDK)

## iLiveSDK导入

iLiveSDK在Android Studio上开发。
导入只需要在gradle里增加两行（后面是版本号）


直播业务功能       
compile 'com.tencent.livesdk:livesdk:1.1.1'      
核心功能     
compile 'com.tencent.ilivesdk:ilivesdk:1.5.0'      
            
## SDK最近更新说明

###### V1.5.0(2017-06-05)
 - 优化旋转功能(支持横屏)
 - 新增视频数上限为10路
 - 更新AVSDK到1.9.1.17

[更多版本更新信息](doc/ILiveSDK/release%20note.md)


## DEMO
1、随心播
 
  本工程为随心播的源码，随心播是基于ILiveSDK开发的一款示例性产品，用于演示互动直播的能力 <br />
<br />
2、[简单直播(非常简单演示了一个直播的基本流程)](https://github.com/zhaoyang21cn/iLiveSDK_Android_LiveDemo.git)

## API文档
[API文档(1.4.0)](https://zhaoyang21cn.github.io/ilivesdk_help/android_help/)

## 异常事件
[异常事件](/doc/ILiveSDK/exception.md)

## 错误码
[错误码表](/doc/ILiveSDK/error.md)

## 添加混淆
由于内部有一些接口调用需要，在用户工程需要混淆时，请添加以下配置:
```
-keep class com.tencent.**{*;}
-dontwarn com.tencent.**

-keep class tencent.**{*;}
-dontwarn tencent.**

-keep class qalsdk.**{*;}
-dontwarn qalsdk.**
```
## 直播外延

[音视频预处理](https://www.qcloud.com/document/product/268/7645)<br/>
[如何渲染](doc/ILiveSDK/AndroidRenderIntr.md)<br/>
[如何旋转和裁剪画面](https://www.qcloud.com/document/product/268/7647)<br/>
[画面对焦](https://www.qcloud.com/document/product/268/7646)<br/>
[美颜包](/doc/ILiveSDK/ilivefilter.md)<br/>
[大咖模式](/doc/ILiveSDK/bigstar.md)<br/>

## 已知问题
由于目前只支持armeabi架构(1.0.5版本之后支持arm-v7a)，如果工程(或依赖库)中有多架构，需要在build.gradle中添加以下配置
<pre>
android{
    defaultConfig{
        ndk{
            abiFilters 'armeabi', 'armeabi-v7a'
        }
    }
}
</pre>

如果您还在使用eclipse进行Android的开发，请参考这里[eclipse集成方案](/doc/ILiveSDK/eclipse_readme.md)。    
Android Studio在google支持度，编译便利性等多方面远超eclipse。我们强烈建议还在使用eclipse的用户尽快升级。

## 日志
[关键路径LOG 请遇到问题先自行对比](/doc/ILiveSDK/Logs.md)

