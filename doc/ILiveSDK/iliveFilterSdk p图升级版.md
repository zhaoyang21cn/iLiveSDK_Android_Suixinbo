### iliveFilterSdk-1.2.3 p图升级版使用说明
使用前说明：
1，该sdk为带p图sdk的升级版本，同时带有视频剪裁和旋转的功能；

2，需要申请p图的licence后，大眼、瘦脸、动效才能生效；
   p图licence 需要联系商务获取
   
3，“纹理id输入”或“纹理id输出”的功能，只在有GL环境场景下才有效

SDK下载路径：	http://dldir1.qq.com/hudongzhibo/ILiveSDK/iliveFilterSdk-1.2.3.aar

| 接口名|  接口描述  |参数定义|返回值|
|---------|---------|---------|---------|
| **TXCVideoPreprocessor(Context activity, boolean bGLContext)** | 构造函数|activity：当前Activity的this指针  bGLContext：当前是否有Opengl 环境|无|
| **void release()** | 释放sdk资源|无 |无|
| **void setFaceSlimLevel(int level)** | 设置瘦脸级别|level: 瘦脸级别（0~9）|无|
| **void setEyeScaleLevel(int level)** | 设置大眼级别|level: 大眼级别（0~9）|无|
| **void setMotionTmpl(String tmplPath)** | 设置动态贴纸路径|tmplPath: 动态贴纸路径|无|
| **boolean setGreenScreenFile(String path)** | 设置绿幕文件路径|path: 绿幕文件路径|无|
| **int setFilter(int type)** | 切换滤镜|type: 滤镜编号 0：美颜 1：清新 2：浪漫 3：唯美 4：粉嫩 5：怀旧 6: 蓝调 7:清凉  8: 日系|-1：失败 0：成功|
| **void setBeautyLevel(int level)** | 设置美颜级别|level: 美颜级别 （0~9）|无|
| **void setWhitenessLevel(int level)** | 设置美白级别|level: 美白级别（0~9）|无|
| **void setWaterMark(Bitmap bitmap, float x, float y, float width)** | 设置水印（位置以左上角为原点）|bitmap: 水印图片BitMap x：(0.0~1.0)归一化坐标，左上角x轴偏移 y：(0.0~1.0)归一化坐标，左上角y轴偏移 width：(0.0~1.0)归一化宽度；左上角x轴宽度|无|
| **void setListener(TXIVideoPreprocessorListener listener)** | 设置数据回调监听函数|listener: 数据回调监听函数（用于接收process* 处理函数返回的数据）|无|
| **void setNotifyListener(TXINotifyListener notify)** | 设置事件回调监听函数|notify: 事件回调监听函数（用于接收sdk返回的事件）|无|
| **int processFrame(int texture, int width, int height, int processAngle, int outputFormat)** | 纹理输入处理|texture：输入纹理 width：纹理宽 height：纹理高 processAngle：纹理旋转角度 outputFormat：视频输出格式（0纹理输出 1：I420数据输出 2：RGBA 数据输出 3：NV21数据输出） |-1： 失败 >=0：成功|
| **int processFrame(byte[] data, int width, int height, int processAngle, int inputFormat, int outputFormat)** | 原始数据输入处理|data：输入原始数据 width：数据宽 height：数据高 processAngle：纹理旋转角度 inputFormat：视频输出格式（0：纹理输入 1：I420数据输入 2：RGBA 数据输出 3：NV21数据输入） outputFormat：视频输出格式（0纹理输出 1：I420数据输出 2：RGBA 数据输出 3：NV21数据输出） |-1： 失败 >=0：成功|
| **void setMirror(boolean enable)** | 设置输出图像左右镜像|enable：true：开启左右镜像 false：不开启左右镜像 |无|
| **void setCrop(CropRect cutRect)** | 设置剪裁大小|cutRect：剪裁矩阵 cutRect.x：x轴坐标偏移 cutRect.y：y轴坐标偏移 cutRect.cropWidth：x轴剪裁长度 cutRect.cropHeight：y轴剪裁高度 |无|
| **void setRotate(int angle)** | 设置输出旋转顺时针角度|angle：输出顺时针旋转角度|无|
| **void setOutputFrameSize(int width, int height)** | 设置输出长宽|width：输出数据宽 height：输出数据高 |无|

### AVSDK 场景（无GL环境）使用代码范例：
1，申请p图licence；并将licence文件，放入app的asset目录

2： 在工程中添加配置，引入 TXCVideoPreprocessor 
<pre>
build.gradle 的dependency中添加
compile(name:'iliveFilterSdk-1.2.3', ext:'aar')
／／ 因为目前该升级库，只支持 armeabi 架构；所以需要在 build.gradle 中指定；防止出现找不到 *.so 库
defaultConfig{
    ....
    ndk {
        abiFilters 'armeabi'
    }
}
</pre>
3：创建和初始化 预处理类 和 初始化参数
<pre>
static public class FrameFormat {
        static public final int TEXTURE = 0;        // 纹理格式
        static public final int I420 = 1;        //I420格式
        static public final int RGBA = 2;        //RGBA格式
        static public final int NV21 = 3;      //NV21格式
        static public final int TEXTURE_EXT = 4;        // 外部纹理输入(相机纹理输入时，可用)
        static public final int NONE = 5;        // 无输入
}
// TXCVideoPreprocessor.EventVideoProcess 事件定义
    static public class EventVideoProcess{
        static public final int EVENT_VIDEOPROCESS_FACERECOGNISE_SUCESS = 4001;     // 人脸识别成功事件
        static public final int EVENT_VIDEOPROCESS_FACERECOGNISE_FAILED = 4002;     // 人脸识别失败事件
    }

boolean bGLContext = false;     // 在 AVSDk 场景下，设置当前为无 OpenGL 环境
TXCVideoPreprocessor mTxcFilter = new TXCVideoPreprocessor(this, bGLContext);

／／ 根据自己的需要，在适当的地方设置参数
mTxcFilter.setFaceSlimLevel(5);         ／／ 设置小脸级别
mTxcFilter.setEyeScaleLevel(5);         ／／ 设置大眼级别
mTxcFilter.setBeautyLevel(5);           ／／ 设置美颜级别
mTxcFilter.setWhitenessLevel(3);        ／／ 设置美白级别
mTxcFilter.setMotionTmpl(mMotionTmplPath);  ／／ 设置动效文件路径
</pre>
4：设置 AVSDK 相机数据回调（**设置回调，一定要在进入房间成功后，才有效！！**）
<pre>
boolean bRet = ILiveSDK.getInstance().getAvVideoCtrl().setLocalVideoPreProcessCallback(new AVVideoCtrl.LocalVideoPreProcessCallback(){
    @Override
    public void onFrameReceive(AVVideoCtrl.VideoFrame var1) {
        // 回调的数据，传递给 ilivefilter processFrame 接口处理;
        // avsdk回调函数，默认为 I420 格式
        mTxcFilter.processFrame(var1.data, var1.width, var1.height, var1.rotate, TXCVideoPreprocessor.FrameFormat.I420, TXCVideoPreprocessor.FrameFormat.I420);
    }
});
</pre>
5：**退出房间时，必须销毁滤镜资源！！否则下次进入房间，设置滤镜不生效！**
<pre>
ILVLiveManager.getInstance().quitRoom(new ILiveCallBack() {
    @Override
    public void onSuccess(Object data) {
    // 取消 AVSDK 相机数据回调（参数传null）
    boolean bRet = ILiveSDK.getInstance().getAvVideoCtrl().setLocalVideoPreProcessCallback(null);
        // 退出房间后，一定要销毁filter 资源；否则下次进入房间，setFilter将不生效或其他异常
        mTxcFilter.setFilter(-1);
        mTxcFilter.release();
    }
    @Override
    public void onError(String module, int errCode, String errMsg) {
    }
});
</pre>
### 自定义相机采集场景（有GL环境）使用代码范例：
1，申请p图licence；并将licence文件，放入app的asset目录

2： 在工程中添加配置，引入 TXCVideoPreprocessor 
<pre>
build.gradle 的dependency中添加
compile(name:'iliveFilterSdk-1.2.3', ext:'aar')
</pre>
3：创建和初始化 预处理类 和 初始化参数
<pre>
// TXCVideoPreprocessor.FrameFormat 类格式定义
static public class FrameFormat {
        static public final int TEXTURE = 0;        // 纹理格式
        static public final int I420 = 1;        //I420格式
        static public final int RGBA = 2;        //RGBA格式
        static public final int NV21 = 3;      //NV21格式
        static public final int TEXTURE_EXT = 4;        // 外部纹理输入
        static public final int NONE = 5;        // 无输入
}
// TXCVideoPreprocessor.EventVideoProcess 事件定义
    static public class EventVideoProcess{
        static public final int EVENT_VIDEOPROCESS_FACERECOGNISE_SUCESS = 4001;     // 人脸识别成功事件
        static public final int EVENT_VIDEOPROCESS_FACERECOGNISE_FAILED = 4002;     // 人脸识别失败事件
    }

boolean bGLContext = true;     // 非 AVSDk 场景下，如果有GL环境，则应设置当前为有 OpenGL 环境
TXCVideoPreprocessor mTxcFilter = new TXCVideoPreprocessor(this, bGLContext);

／／ 根据自己的需要，在适当的地方设置参数
mTxcFilter.setFaceSlimLevel(5);         ／／ 设置小脸级别
mTxcFilter.setEyeScaleLevel(5);         ／／ 设置大眼级别
mTxcFilter.setBeautyLevel(5);           ／／ 设置美颜级别
mTxcFilter.setWhitenessLevel(3);        ／／ 设置美白级别
mTxcFilter.setMotionTmpl(mMotionTmplPath);  ／／ 设置动效文件路径
</pre>
4:设置数据输出回调
<pre>
mTxcFilter.setListener(new TXIVideoPreprocessorListener(){
@Override
    public int willAddWatermark(int texture, int width, int height){
    ／／ texture 为sdk返回给用户自定义处理的纹理id；处理 后，通过返回值返回处理后的纹理
        Log.i(TAG, "willAddWatermark texture id " + texture + " width " + width + " height " + height);
        return 0;
    }
    @Override
    public void didProcessFrame(int texture, int width, int height, long ts) {
    ／／ 纹理id输出回调
        mOutProcessTextureId = texture;
        mOutProcessWidth = width;
        mOutProcessHeight = height;
        mOutProcessPts = ts;
    }
    @Override
    public void didProcessFrame(byte[] data, int width, int height, int format, long ts) {
    ／／ 原始数据输出回调
        mOutProcessByte = data;
        mOutProcessWidth = width;
        mOutProcessHeight = height;
        mOutProcessFormat = format;
        mOutProcessPts = ts;
    }
});
</pre>
5，设置事件回调
<pre>
/*
EVENT_VIDEOPROCESS_FACERECOGNISE_SUCESS = 4001;     // 人脸识别成功事件
EVENT_VIDEOPROCESS_FACERECOGNISE_FAILED = 4002;     // 人脸识别失败事件
*/
mTxcFilter.setNotifyListener(new TXINotifyListener(){
    @Override
    public void onNotifyEvent(final int event, final Bundle param){
          Log.i(TAG, "recv event id " + event);
          // 人脸识别成功
          if (TXCVideoPreprocessor.EventVideoProcess.EVENT_VIDEOPROCESS_FACERECOGNISE_SUCESS == event){
            Log.i(TAG, "Face Recognise sucess");
          }else if (TXCVideoPreprocessor.EventVideoProcess.EVENT_VIDEOPROCESS_FACERECOGNISE_FAILED == event){
          ／／ 人脸识别失败
            Log.i(TAG, "Face Recognise failed");
          }
      }
});
</pre>
6：预处理数据 
<pre>
/* 
1,此处需特别注意输入角度 mPreviewAngle 的值；因为p图sdk只能识别图像为正的人脸；
所以mPreviewAngle代表图像应该旋转多少度，才能为正
2,因为Android相机的特性，前置摄像头数据为左右镜像；如果为前置摄像头，需要调用 setMirror(true)
*/
// 纹理输入；纹理输入-->纹理输出
textureId = mTxcFilter.processFrame(textureId, mPreviewWidth, mPreviewHeight, mPreviewAngle, TXCVideoPreprocessor.FrameFormat.TEXTURE, TXCVideoPreprocessor.FrameFormat.TEXTURE);
if (textureId &lt; 0) {
    Log.e(TAG, "processTexture failed!");
    return;
}

// 原始数据输入；此处演示 nv21数据输入--》textureId 输出
textureId = mTxcFilter.processFrame(bytes, mPreviewWidth, mPreviewHeight, mPreviewAngle, TXCVideoPreprocessor.FrameFormat.NV21, TXCVideoPreprocessor.FrameFormat.TEXTURE);
if (textureId &lt; 0){
    Log.e(TAG, "process Data failed!");
    return;
}

</pre>
7：释放sdk 资源
<pre>
if (null != mTxcFilter){
    mTxcFilter.release();
    mTxcFilter = null;
}
</pre>
