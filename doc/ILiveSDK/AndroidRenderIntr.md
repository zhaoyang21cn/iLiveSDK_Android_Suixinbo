
# Android渲染指引文档
Android渲染支持两种渲染方式:
> * 统一渲染: 互动直播所有视频用一个Android控件进行渲染
> * 分开渲染: 每一路互动直播视频用一个Android控件进行渲染 

|渲染方式|多路渲染|视频交换|视频拖动|视频点击|花式渲染|
|:--:|:--:|:--:|:--:|:--:|:--:|
|统一渲染|O|O|O|O|X|
|分开渲染|O|X(暂不支持)|X|O|O|

## 统一渲染
在采用统一渲染时，使用的渲染控件是AVRootView
### 使用步骤:
#### 1、定义一个AVRootView
```
<com.tencent.ilivesdk.view.AVRootView
        android:id="@+id/avrv_video"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```
#### 2、查找到AVRootView:
```
avRootView = (AVRootView)findViewById(R.id.avrv_video);
```
#### 4、 设置到视频房间

*iLiveSDK*:
```
ILiveRoomManager.getInstance().initAvRootView(avRootView);
```
*LiveSDK*:
```
ILiveRoomManager.getInstance().initAvRootView(avRootView);
```
*CallSDK*:
```
ILVCallManager.getInstance().initAvView(avRootView);
```

### 视频模型:
![](https://zhaoyang21cn.github.io/iLiveSDK_Help/readme_img/AVRootViewLayers.png)

### 视频布局:
通过模型图可以看到，实际视频画面在AVVideoView上渲染，所以视频布局由AVVideoView的布局决定

* 初始布局
 AVRootView中可设置AVVideoView的默认布局
![](https://zhaoyang21cn.github.io/iLiveSDK_Help/readme_img/AVVideoView.png)
```
avRootView.setGravity(AVRootView.LAYOUT_GRAVITY_LEFT);
avRootView.setSubMarginX(10);
avRootView.setSubMarginY(10);
avRootView.setSubWidth(240);
avRootView.setSubHeight(320);
```

* 获取视频AVVideoView
- 在还没有开始渲染视频时，可以通过id来获取(需要在初始化后):
```
avRootView.setSubCreatedListener(new AVRootView.onSubViewCreatedListener() {
    @Override
    public void onSubViewCreated() {
        // AVVideoView初始化回调  
        for (int i=0; i<ILiveConstants.MAX_AV_VIDEO_NUM; i++){
            avRootView.getViewByIndex(i).setRotate(true);
        }
    }
});
```
- 在开始渲染视频后
```
// 获取渲染john的摄像头数据的AVVideoView
avRootView.findUserViewIndex("john", CommonConstants.Const_VideoType_Camera);
```

* 动态布局
AVVideoView支持动态布局

|接口名称|接口作用|
|:--:|:--|
|setPosLeft|设置X|
|setPosTop|设置Y|
|setPosWidth|设置宽度|
|setPosHeight|设置高度|
|autoLayout|刷新显示|

## 分开渲染
在采用分开渲染时，使用的渲染控件是ILiveRootView
### 使用步骤:
#### 1、定义一个ILiveRootView
```
<RelativeLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <com.tencent.ilivesdk.view.ILiveRootView
        android:id="@+id/ilrv_x"
        android:layout_margin="100dp"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
    <com.tencent.ilivesdk.view.ILiveRootView
        android:id="@+id/ilrv_y"
        android:layout_alignParentTop="true"
        android:layout_alignParentLeft="true"
        android:layout_width="96dp"
        android:layout_height="128dp" />
    <com.tencent.ilivesdk.view.ILiveRootView
        android:id="@+id/ilrv_z"
        android:layout_alignParentBottom="true"
        android:layout_alignParentRight="true"
        android:layout_margin="10dp"
        android:layout_width="96dp"
        android:layout_height="128dp" />
</RelativeLayout>
```
#### 2、查找到ILiveRootView:
```
rootViews = new ILiveRootView[3];
rootViews[0] = (ILiveRootView)findViewById(R.id.ilrv_x);
rootViews[1] = (ILiveRootView)findViewById(R.id.ilrv_y);
rootViews[2] = (ILiveRootView)findViewById(R.id.ilrv_z);
```
#### 3、 设置到视频房间
```
ILiveRoomManager.getInstance().initRootViewArr(rootViews);
```
### 视频布局:
分开渲染的视频大小由布局文件中的ILiveRootView大小决定，参考Android控件布局
