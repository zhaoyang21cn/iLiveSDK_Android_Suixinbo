# TXCVideoPreprocessor(ilivefilter) 使用说明

## 使用前说明（重要）：
1，TXCVideoPreprocessor 分为“p图收费版” 和 “非p图普通版”
p图收费版：
> compile 'com.tencent.ilivefilter:liteav_pitu:1.1.21'

非p图普通版：
> compile 'com.tencent.ilivefilter:liteav_normal:1.1.21'

2，需要申请p图的licence后，大眼、瘦脸、动效才能生效；
   p图licence 需要联系商务获取

3，“p图收费版” 只支持 armeabi 架构的库，
  “非p图普通版” 同时支持 armeabi 和 armeabi-v7a 架构

4，“p图收费版” 需要申请手机 READ_PHONE_STATE（访问电话状态） 权限；否则p图功能会无效

5，“纹理id输入”或“纹理id输出”的功能，只在有GL环境场景下才有效

6， 美颜、滤镜和p图功能，只支持Android API Level >=  17（Android 4.2和以上系统）

7，新版TXCVideoPreprocessor，同时兼容老版的 ilivefilter；
只需要将类名TXCVideoPreprocessor ，改为 TILFilter；即可；
[点击跳转老版 ilivefilter 使用文档](https://github.com/zhaoyang21cn/TILFilterSdk/blob/master/README.md
)

8，“AVSDK版本 < 1.9.5” 并且使用 “liteav_pitu版本”，因avsdk 1.9.5以前版本的 setAfterPreviewListener 接口没有返回画面旋转角度，导致p图无法根据旋转角度识别人脸，所以只能仍然使用 setLocalVideoPreProcessCallback；

9，“AVSDK版本 >= 1.9.5”，建议使用新版的 setAfterPreviewListener 接口，效率更高

### 费用说明

美颜、美白、红润等基础功能是免费的。基于人脸识别的功能由于采用了优图实验室的专利技术，授权费用约 50W/年（目前国内同类图像处理产品授权均在百万左右）。如有需要可以提工单或联系我们（jerryqian QQ:364993028 ），商务同学会提供P图SDK，并替您向优图实验室申请试用 License。

最新版本说明
> V1.1.21(2018-4-2)</br>
(1) 优化绿幕功能；添加绿幕参数设置接口 setGreenScreenParam </br>
(2) 优化预处理sdk代码</br>
</br>

[查看更多版本更新记录](https://github.com/zhaoyang21cn/iLiveSDK_Android_Suixinbo/blob/master/doc/ILiveSDK/TILFilterSDK_ChangeList.md)

</br>

| 接口名|  接口描述  |参数定义|返回值|
|---------|---------|---------|---------|
| **TXCVideoPreprocessor(Context activity, boolean bGLContext)** | 构造函数|activity：当前Activity的this指针  bGLContext：当前是否有Opengl 环境|无|
| **逻辑代码接口** | 
| **void setListener(TXIVideoPreprocessorListener listener)** | 设置sdk数据监听回调|listener: sdk数据返回监听（用于接收process* 处理函数返回的数据）|无|
| **void setNotifyListener(TXINotifyListener notify)** | 设置sdk事件监听回调|notify: sdk事件监听；事件为 TXCVideoPreprocessor.EventVideoProcess.*|无|
| **int processFrame(int texture, int width, int height, int processAngle, int inputFormat, int outputFormat)** | 纹理输入处理|texture：输入纹理 width：纹理宽 height：纹理高 processAngle：纹理旋转角度 inputFormat：视频输入格式（0：2D纹理输入 1：I420数据输入 2：RGBA 数据输出 3：NV21数据输入 4:外部纹理输入（如相机纹理，需要调用 setInputMatrix 设置旋转矩阵）） outputFormat：视频输出格式（0：2D纹理输出 1：I420数据输出 2：RGBA 数据输出 3：NV21数据输出） |-1： 失败 >=0：成功|
| **int processFrame(byte[] data, int width, int height, int processAngle, int inputFormat, int outputFormat)** | 原始数据输入处理|data：输入原始数据 width：数据宽 height：数据高 processAngle：纹理旋转角度 inputFormat：视频输入格式（0：2D纹理输入 1：I420数据输入 2：RGBA 数据输出 3：NV21数据输入 4:外部纹理输入（如相机纹理，需要调用 setInputMatrix 设置旋转矩阵）） outputFormat：视频输出格式（0：2D纹理输出 1：I420数据输出 2：RGBA 数据输出 3：NV21数据输出） |-1： 失败 >=0：成功|
| **void setInputMatrix(float[] mtx)** | 设置外部纹理输入时的旋转矩阵（仅在外部纹理输入时，才使用）|mtx: 外部纹理输入时，从SurfaceTexture.getTransformMatrix() 中获取|无|
| **void release()** | 释放sdk资源|无 |无|
| **美颜相关接口** | 
| **void setBeautyStyle(int style)** | 设置美颜风格|style: 美颜风格 0: 光滑 1: 自然 2: 朦胧|无|
| **void setBeautyLevel(int level)** | 设置美颜级别|level: 美颜级别 （0~9）|无|
| **void setWhitenessLevel(int level)** | 设置美白级别|level: 美白级别（0~9）|无|
| **void setRuddyLevel(int level)** | 设置红润级别|level: 红润级别（0~9）|无|
| **滤镜相关接口** | 
| **int setFilterType(int type)** | 切换滤镜|type: 滤镜编号 1:无  2：浪漫 3：清新 4：唯美 5：粉嫩 6：怀旧 7: 蓝调 8:清凉  9: 日系|-1：失败 0：成功|
| **void setFilterImage(Bitmap bmp)** | 设置滤镜图片|bmp: 滤镜图片|无|
| **void setFilterImage(String imagePath)** | 设置滤镜图片路径|imagePath：滤镜文件路径|无|
| **void setFilterMixLevel(final float specialValue)** | 设置滤镜程度|specialValue：滤镜程度（0.0 ～ 1.0）|无|
| **视频编辑相关接口** | 
| **void setCrop(CropRect cutRect)** | 设置剪裁大小|cutRect：剪裁矩阵 cutRect.x：x轴坐标偏移 cutRect.y：y轴坐标偏移 cutRect.cropWidth：x轴剪裁长度 cutRect.cropHeight：y轴剪裁高度 |无|
| **void setRotate(int angle)** | 设置输出旋转顺时针角度|angle：输出顺时针旋转角度|无|
| **void setOutputFrameSize(int width, int height)** | 设置输出长宽|width：输出数据宽 height：输出数据高 |无|
| **void setMirror(boolean enable)** | 设置输出图像左右镜像|enable：true：开启左右镜像 false：不开启左右镜像 |无|
| **void setWaterMark(Bitmap bitmap, float x, float y, float width)** | 设置水印（位置以左上角为原点）|bitmap: 水印图片BitMap x：(0.0~1.0)归一化坐标，左上角x轴偏移 y：(0.0~1.0)归一化坐标，左上角y轴偏移 width：(0.0~1.0)归一化宽度；左上角x轴宽度|无|
| **void setWaterMarkList(final List< WaterMakeTag> markList)** | 设置多个水印（setWaterMark 的加强版）|markList：多个水印 WaterMakeTag 链表|无|
| **p图升级版相关接口** | 
| **void setFaceSlimLevel(int level)** | 设置瘦脸级别|level: 瘦脸级别（0~9）|无|
| **void setEyeScaleLevel(int level)** | 设置大眼级别|level: 大眼级别（0~9）|无|
| **void setFaceVLevel(int level)** | 设置V脸级别|level: V脸级别（0~9）|无|
| **void setFaceShortLevel(int level)** | 设置短脸级别|level: 短脸级别（0~9）|无|
| **void setChinLevel(int level)** | 设置长下巴级别|level: 长下巴级别（0~9）|无|
| **void setNoseSlimLevel(int level)** | 设置小鼻级别|level: 小鼻级别（0~9）|无|
| **void setMotionTmpl(String tmplPath)** | 设置动态贴纸路径|tmplPath: 动态贴纸路径|无|
| **boolean setGreenScreenFile(String path, boolean isLoop)** | 设置绿幕文件路径|path: 绿幕文件路径(目前图片支持jpg/png/bmp，视频仅支持mp4格式)      isLoop：是否循环播放设置的视频文件（只针对视频）|无|
| **boolean setGreenScreenParam(TXCGreenScreenParam param)** | 设置绿幕参数|param.fillMode：绿幕背景填充参数     param.xMirror：x轴是否镜像；因为Android相机前置摄像头为左右镜像；所以如果想要主播看到绿幕背景为正，则需要左右镜像绿幕|无|
### 无 OpenglGL 环境（AVSDK/iLiveSDK，或其他无 OpenglGL 环境场景）使用代码范例：
[查看 demo 中的【接入AVSDK/iLiveSDK通用方法】](https://github.com/zhaoyang21cn/TILFilterSdk)

1，如果是p图升级版，请先申请p图licence；并将licence改名为 YTFaceSDK.licence，放入app的asset目录下，即可

2： 在工程中添加配置，引入 TXCVideoPreprocessor 
<pre>
build.gradle 的dependency中添加
// p图版
compile 'com.tencent.ilivefilter:liteav_pitu:1.1.21'
// 非 p图版
compile 'com.tencent.ilivefilter:liteav_normal:1.1.21'

defaultConfig{
    ....
    ndk {
        // p图收费版；只支持 armeabi 架构
        abiFilters 'armeabi'
        
        // 非p图版
        // abiFilters 'armeabi', 'armeabi-v7a'
    }
}
</pre>
3：创建和初始化 预处理类 和 初始化参数
<pre>
boolean bGLContext = false;     // 在 AVSDk 场景下，设置当前为无 OpenGL 环境
TXCVideoPreprocessor mTxcFilter = new TXCVideoPreprocessor(this, bGLContext);
</pre>
4，设置参数
<pre>
／／ 根据自己的需要，在适当的地方设置参数
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

    mTxcFilter.setBeautyStyle(0);           // 设置美颜风格，0: 光滑 1: 自然 2: 朦胧
    mTxcFilter.setBeautyLevel(5);           // 设置美颜级别,范围 0～10
    mTxcFilter.setWhitenessLevel(3);        // 设置美白级别,范围 0～10
    mTxcFilter.setRuddyLevel(2);            // 设置红润级别，范围 0～10
    
    // p 图版本
    mTxcFilter.setFaceSlimLevel(5);         // 设置小脸级别,范围 0～10
    mTxcFilter.setEyeScaleLevel(5);         // 设置大眼级别,范围 0～10
    mTxcFilter.setFaceVLevel(5)             // 设置 V脸级别,范围 0～10
    mTxcFilter.setFaceShortenLevel(5)       // 设置短脸级别,范围 0～10
    mTxcFilter.setChinSlim(5)               // 设置长下巴级别,范围 0～10
    mTxcFilter.setNoseScale(5)              // 设置小鼻级别,范围 0～10
    mTxcFilter.setMotionTmpl(mMotionTmplPath);  // 设置动效文件路径
    mTxcFilter.setGreenScreenFile(mGreenFile, true);            // 设置绿幕文件路径，如果是视频，循环播放
    mTxcFilter.setGreenScreenParam(mParam);  // 设置绿幕参数
}
</pre>
5，设置事件回调（用于监听p图人脸识别 成功/失败 事件）
<pre>
/*
EVENT_VIDEOPROCESS_FACERECOGNISE_SUCESS = 4001;     // 人脸识别成功事件（p图版）
EVENT_VIDEOPROCESS_FACERECOGNISE_FAILED = 4002;     // 人脸识别失败事件（p图版）
EVENT_GREENFILE_DECODE_FAILED = 4003;     // 绿幕文件解码失败；（绿幕文件不存在，或者文件损坏）
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
          }else if (TXCVideoPreprocessor.EventVideoProcess.EVENT_GREENFILE_DECODE_FAILED == event){
            // 绿幕文件解析失败
            Log.e(TAG, "Green file is error!");
          }
      }
});
</pre>
6：设置 AVSDK 相机数据回调；示例为基于iLiveSDK;
如果是直接集成AVSDK，请直接找到AVSDK 的 setAfterPreviewListener（废弃掉 setLocalVideoPreProcessCallback 接口；但是如果 “AVSDK版本 < 1.9.5” 并且使用 “liteav_pitu版本”，只能继续使用setLocalVideoPreProcessCallback   ） 就好了（**设置回调，一定要在进入房间成功后，才有效！！**）
<pre>
// "AVSDK版本 &lt 1.9.5" && "liteav_pitu版本"，只能使用setLocalVideoPreProcessCallback，avsdk版本可以通过 AVContext.getVersion() 获取
/*
boolean bRet = ILiveSDK.getInstance().getAvVideoCtrl().setLocalVideoPreProcessCallback(new AVVideoCtrl.LocalVideoPreProcessCallback(){
    @Override
    public void onFrameReceive(AVVideoCtrl.VideoFrame var1) {
        // 回调的数据，传递给 ilivefilter processFrame 接口处理;
        // avsdk回调函数，默认为 I420 格式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mTxcFilter.processFrame(var1.data, var1.width, var1.height, var1.rotate, var1.videoFormat, var1.videoFormat);
        }
    }
});
*/

// "AVSDK版本 &gt= 1.9.5"建议使用 setAfterPreviewListener；效率高
boolean bRet = ILiveSDK.getInstance().getAvVideoCtrl().setAfterPreviewListener(new AVVideoCtrl.AfterPreviewListener(){
    @Override
    public void onFrameReceive(AVVideoCtrl.VideoFrame var1) {
    
        // 回调的数据，传递给 ilivefilter processFrame 接口处理;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mTxcFilter.processFrame(var1.data, var1.width, var1.height, var1.rotate, var1.videoFormat, var1.videoFormat);
        }
    }
});
</pre>
7：**退出房间时，必须销毁滤镜资源，下次使用时，在重新创建！！否则下次进入房间，设置滤镜不生效！日志也会出现 “please realloc new TXCVideoPreprocessor” 错误**
<pre>
ILVLiveManager.getInstance().quitRoom(new ILiveCallBack() {
    @Override
    public void onSuccess(Object data) {
    // 取消 AVSDK 相机数据回调（参数传null）
    boolean bRet = ILiveSDK.getInstance().getAvVideoCtrl().setAfterPreviewListener(null);
        // 退出房间后，一定要销毁filter 资源；否则下次进入房间，setFilter将不生效或其他异常
        mTxcFilter.release();
        mTxcFilter = null;
    }
    @Override
    public void onError(String module, int errCode, String errMsg) {
    }
});
</pre>

8:  **扩展：如果用户需要自定义滤镜 或者 需要采用回调函数方式获取数据，则调用流程稍有区别；**
请查看 ：[无 OpenglGL 环境（AVSDK/iLiveSDK，或其他无 OpenglGL 环境场景）异步回调方式（自定义滤镜或其他场景） ](https://github.com/zhaoyang21cn/TILFilterSdk/blob/master/ilivefilter_NoGLCallback.md)

### 有 OpenglGL 环境（如自定义采集，使用GLSurfaceView建立了OpenGL 环境或其他场景）使用代码范例：
1，同（无 OpenglGL 环境 1）

2，同（无 OpenglGL 环境 2）

3：创建和初始化 预处理类 和 初始化参数
<pre>
boolean bGLContext = true;     // 有GL环境，则应设置当前为有 OpenGL 环境
TXCVideoPreprocessor mTxcFilter = new TXCVideoPreprocessor(this, bGLContext);
</pre>
4，同（无 OpenglGL 环境 4）

5，同（无 OpenglGL 环境 5）

6，设置数据输出回调
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

7：预处理数据 
<pre>
/* 
1,此处需特别注意输入角度 mPreviewAngle 的值；因为p图sdk只能识别图像为正的人脸；
所以mPreviewAngle代表图像应该旋转多少度，才能为正
*/
// 纹理输入；纹理输入-->纹理输出
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
    textureId = mTxcFilter.processFrame(textureId, mPreviewWidth, mPreviewHeight, mPreviewAngle, TXEFrameFormat.TEXTURE, TXEFrameFormat.TEXTURE);
    if (textureId &lt; 0) {
        Log.e(TAG, "processTexture failed!");
        return;
    }
}

// 原始数据输入；此处演示 nv21数据输入--》textureId 输出
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
    textureId = mTxcFilter.processFrame(bytes, mPreviewWidth, mPreviewHeight, mPreviewAngle, TXEFrameFormat.NV21, TXEFrameFormat.TEXTURE);
    if (textureId &lt; 0){
        Log.e(TAG, "process Data failed!");
        return;
    }
}

</pre>
8：释放sdk 资源
<pre>
if (null != mTxcFilter){
    mTxcFilter.release();
    mTxcFilter = null;
}
</pre>
