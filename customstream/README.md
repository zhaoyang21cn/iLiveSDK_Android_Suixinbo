# AVSDK（Android）自定义视频输入

------

AVSDK可以接受用户自定义的视频输入，本示例以摄像头预览数据为例展示如何将自定义的视频内容直播给观众。

## AVSDK初始化
在开始传输数据之前需要初始化AVSDK和创建房间。本示例使用了ILiveSDK封装的接口，你也可以使用IMSDK和AVSDK的接口完成相同的功能。
### 1. 登录和初始化

```java
ILiveSDK.getInstance().initSdk(getApplicationContext(), SDKAPPID, AccountType);
ILiveRoomManager.getInstance().init(new ILiveRoomConfig().autoRender(true));
ILiveLoginManager.getInstance().tilvbLogin(id, sig, callback);
```

### 2. 创建AVSDK房间
```java
ILiveRoomOption option = new ILiveRoomOption("")
                    .imsupport(false)
                    .autoCamera(false)
                    .autoMic(false);
iRoomNum = Integer.valueOf(inputId.getText().toString());
ILiveRoomManager.getInstance().createRoom(iRoomNum, option, callback);
```
## 准备视频数据
这里开启摄像头，使用surfaceview进行预览，并取出预览的画面数据传输给AVSDK。
### 3. 开启摄像头和Mic
1）开启摄像头
```java
Camera c = Camera.open();

```
2）在界面创建SurfaceView，监听SurfaceHolder.Callback，在surfaceCreated中开始预览。
```java
@Override
public void surfaceCreated(SurfaceHolder holder) {
    try {
        if (mCamera == null) return;
        mCamera.setPreviewDisplay(holder);
        mCamera.startPreview();
    } catch (IOException e) {
        Log.d(TAG, "Error setting camera preview: " + e.getMessage());
    }
}

@Override
public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    if (mCamera == null||mHolder.getSurface() == null){
        // preview surface does not exist
        return;
    }

    // stop preview before making changes
    try {
        mCamera.stopPreview();
    } catch (Exception e){
        // ignore: tried to stop a non-existent preview
    }

    // set preview size and make any resize, rotate or
    // reformatting changes here

    // start preview with new settings
    try {
        mCamera.setPreviewDisplay(mHolder);
        mCamera.setDisplayOrientation(90);
        ILiveSDK.getInstance().getAvVideoCtrl().enableExternalCapture(true,callback);
        ILiveRoomManager.getInstance().enableMic(true);
        mCamera.setPreviewCallback(this);
        mCamera.startPreview();

    } catch (Exception e){
        Log.d(TAG, "Error starting camera preview: " + e.getMessage());
    }
}

```

### 4. 获取视频数据，将之传入AVSDK
开启预览时，设置好PreviewCallback回调，在回调中将视频数据传递给AVSDK。这里主要使用AVVideoCtrl.fillExternalCaptureFrame方法来输入数据，这个方法的参数依次是：视频数据，数据长度，图像宽，图像高，图像旋转方向，颜色编码方式，数据类型。


 - 图像旋转方向

| 角度值  | 前置摄像头   |  后置摄像头  |
| --------   | -----  | ----  |
| 0        | 手机左旋90度 |   手机左旋90度     |
| 1        |   手机倒置   |   手机正向   |
| 2        |    手机右旋90度    |  手机右旋90度  |
| 3        |    手机正向    |  手机倒置   | 

- 颜色编码方式仅支持I420
- 数据类型支持AVView.VIDEO_SRC_TYPE_NONE,AVView.VIDEO_SRC_TYPE_CAMERA,AVView.VIDEO_SRC_TYPE_SCREEN,AVView.VIDEO_SRC_TYPE_MEDIA这四种

```java
mCamera.setPreviewCallback(this);
@Override
public void onPreviewFrame(byte[] data, Camera camera) {
    if (null != camera && !bPreviewInit) {
        Camera.Parameters parameters = camera.getParameters();
        mCameraSize = parameters.getPreviewSize();
        bPreviewInit = true;
    }
    if (null != ILiveSDK.getInstance().getAvVideoCtrl()) {
        
        ILiveSDK.getInstance().getAvVideoCtrl().fillExternalCaptureFrame(data, data.length,
                mCameraSize.width, mCameraSize.height, 1, AVVideoCtrl.COLOR_FORMAT_I420, AVView.VIDEO_SRC_TYPE_CAMERA);

        
    }
}
```
## 加入房间观看视频
加入房间观看视频和正常的流程一致，即如前文所述，完成初始化，登录，进入房间即可。

