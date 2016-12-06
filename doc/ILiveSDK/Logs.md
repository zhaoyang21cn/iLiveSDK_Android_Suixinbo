#正确流程的LOG 

##主播端发起一个直播  流程正确LOG如下

![](../../raw/rightProcess.png)

- 具体包括了8个步骤    



>
12-06 15:24:06.741 19555-19555/com.tencent.ilivedemo I/ILiveSDK: Key_Procedure｜initSdk->init appid:1400001692, accountType:884 1初始化步骤     
12-06 15:24:06.746 19555-19555/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|ILVB-Room|init root view  2设置渲染层       
12-06 15:24:06.746 19555-19555/com.tencent.ilivedemo I/AVVideoGroup: Key_Procedure|ILVB-AVVideoGroup|init sub views  2设置渲染层     
12-06 15:24:13.601 19555-19555/com.tencent.ilivedemo I/ILVBLogin: Key_Procedure｜ILVB-iLiveLogin strart |id:will 3 iLive登录   
12-06 15:24:13.746 19555-19555/com.tencent.ilivedemo I/ILVBLogin: Key_Procedure｜ILVB-iLiveLogin|login success 3 iLive登录   
12-06 15:24:19.701 19555-19555/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|ILVB-Room|start create room:6357 enter with im:true|video:true  4创建直播       
12-06 15:24:20.221 19555-19555/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|createRoom->im room ok:6357 4聊天室OK       
12-06 15:24:20.401 19555-19555/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure｜onNewMessage->size:1
12-06 15:24:20.666 19555-19555/com.tencent.ilivedemo W/ILVBRoom: Key_Procedure|ILVB-Room|enter av room complete result: 0      4AV房间OK   
12-06 15:24:20.751 19555-19555/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|ILVB-Room|onSurfaceCreated
12-06 15:24:20.751 19555-19555/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|ILVB-Room|strart enableCamera 5打开摄像头   
12-06 15:24:20.776 19555-19555/com.tencent.ilivedemo I/AVRootView: Key_Procedure|ILVB-AVRootView|renderVideoView->enter index:0|0,0,1080,1845
12-06 15:24:20.881 19555-19555/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|ILVB-Room|onEndpointsUpdateInfo myself id has camera will     6 server回调用户上线     
12-06 15:24:20.881 19555-19555/com.tencent.ilivedemo I/AVRootView: Key_Procedure|ILVB-AVRootView|renderVideoView->enter index:0|0,0,1080,1845 7渲染      
12-06 15:24:21.151 19555-19555/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|ILVB-Room|enable camera id:0/true 8摄像头上报成功回调




##观众端加入一个直播  流程正确LOG如下

![](../../raw/joinRoomProcess.png)

- 具体包括了6个步骤    



>

12-06 16:18:30.911 4017-4017/com.tencent.ilivedemo I/ILiveSDK: Key_Procedure｜initSdk->init appid:1400001692, accountType:884 // 1初始化   
12-06 16:18:30.916 4017-4017/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|ILVB-Room|init root view // 2设置渲染层   
12-06 16:18:30.941 4017-4017/com.tencent.ilivedemo I/AVVideoGroup: Key_Procedure|ILVB-AVVideoGroup|init sub views2设置渲染层   
12-06 16:18:43.271 4017-4017/com.tencent.ilivedemo I/ILVBLogin: Key_Procedure｜ILVB-iLiveLogin strart |id:will // 3iLive登录    
12-06 16:18:43.681 4017-4017/com.tencent.ilivedemo I/ILVBLogin: Key_Procedure｜ILVB-iLiveLogin|login success 3iLive登录  
12-06 16:18:56.176 4017-4017/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|joinRoom->id: 6352 isIMsupport: true 4加入房间   
12-06 16:18:56.236 4017-4017/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|joinLiveRoom joinIMChatRoom callback succ 4直播聊天室加入成功      
12-06 16:18:56.531 4017-4017/com.tencent.ilivedemo W/ILVBRoom: Key_Procedure|ILVB-Room|enter av room complete result: 0 4AV房间加入成功   
12-06 16:18:56.711 4017-4017/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|ILVB-Room|onSurfaceCreated   
12-06 16:18:56.816 4017-4017/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure｜onNewMessage->size:1    
12-06 16:18:56.851 4017-4017/com.tencent.ilivedemo I/ILVBRoom: Key_Procedure|ILVB-Endpoint | requestRemoteVideo id [willguo]  5获取server 成员上线回调      
12-06 16:18:56.886 4017-4017/com.tencent.ilivedemo I/AVRootView: Key_Procedure|ILVB-AVRootView|renderVideoView->enter index:0| 0,0,1080,1845  6渲染界面   
