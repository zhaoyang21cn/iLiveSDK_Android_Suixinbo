# iLiveSDK
iLiveSDK 提供了账号登录，音视频互动，文本互动等基础功能，顺利的话一天之内即可集成音视频能力。

## iLiveSDK导入

iLiveSDK在Android Studio上开发。
导入只需要在gradle里增加一行（后面是版本号）


核心功能     
compile 'com.tencent.ilivesdk:ilivesdk:1.9.6'      

            
## SDK最近更新说明

### V1.9.6(2018-11-30)
 - 更新AVSDK到1.9.9.1067
 - 在网络较差的情况下优化先保证音频
 - 修复已知问题

[更多版本更新信息](doc/ILiveSDK/release%20note.md)

> 进房出现角色不存在(而确认角色已配置时)
可参考[进房失败(Role no exists)](doc/ILiveSDK/NoRoleErr.md)

> 老用户(曾经用1.9.0以前版本接入)初始化时添加配置:
```Java
// 老用户使用IMSDK通道
ILiveSDK.getInstance().setChannelMode(CommonConstants.E_ChannelMode.E_ChannelIMSDK);
```

## Demo
本工程为ILiveSDK的示例工程，包含Demo：

1、简单直播

### 其它Demo
[随心播](https://github.com/zhaoyang21cn/ILiveSDK_Android_Demos.git)




## API文档
[常用接口](/doc/ILiveSDK/api_interface.md)<br />
[API文档(1.8.2)](https://zhaoyang21cn.github.io/iLiveSDK_Help/android_help/)

## 直播术语解释
[房间，房间生命周期](/doc/ILiveSDK/Names.md)

## 异常事件
[异常事件](/doc/ILiveSDK/exception.md)

## 常见问题
[常见问题](/doc/ILiveSDK/comQA.md)<br />
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

[角色配置](/doc/ILiveSDK/roleIntr.md)<br />
[音视频预处理](https://www.qcloud.com/document/product/268/7645)<br/>
[如何渲染](doc/ILiveSDK/AndroidRenderIntr.md)<br/>
[如何旋转和裁剪画面](https://github.com/zhaoyang21cn/suixinbo_doc/blob/master/doc2/rotate.md)<br/>
[画面对焦](https://www.qcloud.com/document/product/268/7646)<br/>
[美颜包](/doc/ILiveSDK/ilivefiltersdk-README.md)<br/>
[大咖模式](/doc/ILiveSDK/bigstar.md)<br/>
[如何录制混流视频](/doc/ILiveSDK/MixStream.md)<br/>
[如何计算跨房连麦密钥](/doc/ILiveSDK/cross_sign.md)<br />

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

## QAVSDK下载
iLiveSDK内部集成了腾讯云包括IMSDK，QAVSDK。使用iLiveSDK的用户不需要额外集成IMSDK或QAVSDK，就可以直接使用其所有功能。

对于仍在集成QAVSDK的老用户，也可以在这里获取QAVSDK的最新版本:

[QAVSDK_1.9.8.2](http://dldir1.qq.com/hudongzhibo/ILiveSDK/Android/QAVOPENSDK_1.9.8.2_Android_Publish.zip)

[QAVSDK_1.9.7.54](http://dldir1.qq.com/hudongzhibo/ILiveSDK/Android/QAVOPENSDK_1.9.7.54_Android_Publish.zip)

[QAVSDK 1.9.6.49](http://dldir1.qq.com/hudongzhibo/TCShow/AVSDK/AVSDK196/QAVOPENSDK_1.9.6.49_Android_Publish.zip )


## 联系我们

技术支持QQ群：594923937 207177891

技术需求反馈：[https://github.com/zhaoyang21cn/iLiveSDK_Android_Suixinbo/issues](https://github.com/zhaoyang21cn/iLiveSDK_Android_Suixinbo/issues)

# 联系我们

关注公众号"腾讯云视频"，给公众号发关键字"技术支持"，会有专人联系。

![](https://main.qcloudimg.com/raw/769293c3dbc0df8fbfb7d6a7cc904692.jpg)
