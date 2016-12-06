#主线流程LOG对照

##主播端发起一个直播LOG如下

![](../../raw/rightProcess.png)

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




##主播端发起一个直播LOG如下

![](../../raw/joinRoomProcess.png)
