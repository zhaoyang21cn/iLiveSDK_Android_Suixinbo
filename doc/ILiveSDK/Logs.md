#关键路径的LOG 
<br/>
*在ILiveSDK 1.03 以后版本过滤LOG关键字 Key_Procedure 会搜到创建房间加入房间的关键路径*
<br/>

##创建一个房间  流程正确LOG如下

![](../../raw/rightProcess.png)

- 具体包括了8个步骤 解释如下    



```C
/com.tencent.ilivedemo I/ILiveSDK: Key_Procedure｜initSdk->init appid:1400001692, accountType:884 1初始化步骤     
/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|ILVB-Room|init root view  2设置渲染层       
/com.tencent.ilivedemo I/AVVideoGroup: Key_Procedure|ILVB-AVVideoGroup|init sub views  2设置渲染层     
/com.tencent.ilivedemo I/ILVBLogin: Key_Procedure｜ILVB-iLiveLogin strart |id:will 3 iLive登录   
/com.tencent.ilivedemo I/ILVBLogin: Key_Procedure｜ILVB-iLiveLogin|login success 3 iLive登录   
/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|ILVB-Room|start create room:6357 enter with im:true|video:true  4创建直播       
/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|createRoom->im room ok:6357 4聊天室OK       
/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure｜onNewMessage->size:1
/com.tencent.ilivedemo W/ILVBRoom: Key_Procedure|ILVB-Room|enter av room complete result: 0      4AV房间OK   
/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|ILVB-Room|onSurfaceCreated
/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|ILVB-Room|strart enableCamera 5打开摄像头   
/com.tencent.ilivedemo I/AVRootView: Key_Procedure|ILVB-AVRootView|renderVideoView->enter index:0|0,0,1080,1845
/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|ILVB-Room|onEndpointsUpdateInfo myself id has camera will     6 server回调用户上线     
/com.tencent.ilivedemo I/AVRootView: Key_Procedure|ILVB-AVRootView|renderVideoView->enter index:0|0,0,1080,1845 7渲染      
/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|ILVB-Room|enable camera id:0/true 8摄像头上报成功回调
```



##加入一个房间  流程正确LOG如下

![](../../raw/joinRoomProcess.png)

- 具体包括了6个步骤 解释如下 



```C
/com.tencent.ilivedemo I/ILiveSDK: Key_Procedure｜initSdk->init appid:1400001692, accountType:884 // 1初始化   
/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|ILVB-Room|init root view // 2设置渲染层   
/com.tencent.ilivedemo I/AVVideoGroup: Key_Procedure|ILVB-AVVideoGroup|init sub views2设置渲染层   
/com.tencent.ilivedemo I/ILVBLogin: Key_Procedure｜ILVB-iLiveLogin strart |id:will // 3iLive登录    
/com.tencent.ilivedemo I/ILVBLogin: Key_Procedure｜ILVB-iLiveLogin|login success 3iLive登录  
/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|joinRoom->id: 6352 isIMsupport: true 4加入房间   
/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|joinLiveRoom joinIMChatRoom callback succ 4直播聊天室加入成功      
/com.tencent.ilivedemo W/ILVBRoom: Key_Procedure|ILVB-Room|enter av room complete result: 0 4AV房间加入成功   
/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|ILVB-Room|onSurfaceCreated   
/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure｜onNewMessage->size:1    
/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|ILVB-Endpoint | requestRemoteVideo id [willguo]  5获取server 成员上线回调      
/com.tencent.ilivedemo I/AVRootView: Key_Procedure|ILVB-AVRootView|renderVideoView->enter index:0| 0,0,1080,1845  6渲染界面   
```
