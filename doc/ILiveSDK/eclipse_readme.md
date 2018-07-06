# 使用Eclipse集成

------

 1. 在[jcenter核心库](https://bintray.com/ilive/maven/ilivesdk)上面下载iliveSDK最新版本的aar包
 将aar包解压缩，将classes.jar改名为ilivesdk.jar后和libs文件夹下的jar一起放在eclipse工程的libs文件夹中
 >*1.8.7版本以后依赖了[datareport](https://bintray.com/ilive/maven/datareport)模块*
 2. 如果是直播业务，在[jcenter直播库](https://bintray.com/ilive/maven/livesdk)上面下载直播业务层aar包 
  将aar包解压缩，将classes.jar改名为livesdk.jar后一起放在eclipse工程的libs文件夹中
 3. 如果是电话业务，在[jcenter电话库](https://bintray.com/ilive/maven/callsdk)上面下载电话业务层aar包 
  将aar包解压缩，将classes.jar改名为callsdk.jar后一起放在eclipse工程的libs文件夹中
 4. 将aar包中jni文件夹中内容放到eclipse工程的libs文件夹中，如图  <br />  ![](http://i.imgur.com/hufucC2.png)
 5. 在工程的AndroidManifest.xml中加入权限以及一些必须的服务（[详细参考](https://www.qcloud.com/doc/product/269/%E6%A6%82%E8%BF%B0%EF%BC%88Android%20SDK%EF%BC%89#1.3-.E5.88.9B.E5.BB.BA.E5.BA.94.E7.94.A8)）

```
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
    <uses-permission android:name="android.permission.GET_TASKS" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
    <uses-permission android:name="android.permission.READ_LOGS" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
```

在Application标签中加入

    <!-- 消息收发service -->
        <service
            android:name="com.tencent.qalsdk.service.QalService"
            android:exported="false"
            android:process=":QALSERVICE" />
        <service
            android:name="com.tencent.qalsdk.service.QalAssistService"
            android:exported="false"
            android:process=":QALSERVICE" />
        <!-- 离线消息广播接收器 -->
        <receiver
            android:name="com.tencent.qalsdk.QALBroadcastReceiver"
            android:exported="false" >
            <intent-filter>
                <action android:name="com.tencent.qalsdk.broadcast.qal" />
            </intent-filter>
        </receiver>
        <!-- 系统消息广播接收器 -->
        <receiver
            android:name="com.tencent.qalsdk.core.NetConnInfoCenter"
            android:process=":QALSERVICE" >
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
            <intent-filter>
                <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
            </intent-filter>
            <intent-filter>
                <action android:name="android.intent.action.TIME_SET" />
            </intent-filter>
            <intent-filter>
                <action android:name="android.intent.action.TIMEZONE_CHANGED" />
            </intent-filter>
        </receiver>




