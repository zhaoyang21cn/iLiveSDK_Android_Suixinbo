# TXCVideoPreprocessor(ilivefilter) 使用说明

## 使用前说明（重要）：
1，TXCVideoPreprocessor 分为“p图收费版” 和 “非p图普通版”
p图收费版：
> compile 'com.tencent.ilivefilter:liteav_pitu:1.1.16'

非p图普通版：
> compile 'com.tencent.ilivefilter:liteav_normal:1.1.16'

2，需要申请p图的licence后，大眼、瘦脸、动效才能生效；
   p图licence 需要联系商务获取

3，“p图收费版” 只支持 armeabi 架构的库，
  “非p图普通版” 同时支持 armeabi 和 armeabi-v7a 架构

4，“p图收费版” 需要申请手机 READ_PHONE_STATE（访问电话状态） 权限；否则p图功能会无效

5，“纹理id输入”或“纹理id输出”的功能，只在有GL环境场景下才有效

6，新版TXCVideoPreprocessor，同时兼容老版的 ilivefilter；
只需要将类名TXCVideoPreprocessor ，改为 TILFilter；即可；
[点击跳转老版 ilivefilter 使用文档](https://github.com/zhaoyang21cn/TILFilterSdk/blob/master/README.md
)

### 费用说明

美颜、美白、红润等基础功能是免费的。基于人脸识别的功能由于采用了优图实验室的专利技术，授权费用约 50W/年（目前国内同类图像处理产品授权均在百万左右）。如有需要可以提工单或联系我们（louishliu QQ:7399644 ），商务同学会提供P图SDK，并替您向优图实验室申请试用 License。

最新版本说明

> V1.1.16(2017-09-19)</br>
(1) ilivefiler更名为TXMVideoPreprocessor
(2) 以回调函数方式返回处理结果
(3) 增加多套美颜方案（光滑、自然、朦胧）
(4) 增加红润、水印、裁剪、缩放、旋转、镜像、v脸、短脸、下巴、瘦鼻功能
(5) TXCVideoPreprocessor 兼容 老版本 ilivefilter</br>
(6) 增加设置多个水印接口 setWaterMarkList</br>
(7) 接口名字调整
setFaceShortenLevel->setFaceShortLevel
setChinSlim->setChinLevel
setNoseScale->setNoseSlimLevel
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
| **int setFilterType(int type)** | 切换滤镜|type: 滤镜编号 0：无 1：浪漫 2：清新 3：唯美 4：粉嫩 5：怀旧 6: 蓝调 7:清凉  8: 日系|-1：失败 0：成功|
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
| **boolean setGreenScreenFile(String path)** | 设置绿幕文件路径|path: 绿幕文件路径|无|
### 无 OpenglGL 环境（AVSDK/iLiveSDK，或其他无 OpenglGL 环境场景）使用代码范例：

1，如果是p图升级版，请先申请p图licence；并将licence改名为 YTFaceSDK.licence，放入app的asset目录下，即可

2： 在工程中添加配置，引入 TXCVideoPreprocessor 
<pre>
build.gradle 的dependency中添加
// p图版
compile 'com.tencent.ilivefilter:liteav_pitu:1.1.16'
// 非 p图版
compile 'com.tencent.ilivefilter:liteav_normal:1.1.16'

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
/*
static public class FrameFormat {
        static public final int TEXTURE = 0;        // 纹理格式
        static public final int I420 = 1;           // I420格式
        static public final int RGBA = 2;           // RGBA格式
        static public final int NV21 = 3;           // NV21格式
        static public final int TEXTURE_EXT = 4;    // 外部纹理输入(相机纹理输入时，可用)
        static public final int NONE = 5;           // 无输入
}
// TXCVideoPreprocessor.EventVideoProcess 事件定义
    static public class EventVideoProcess{
        static public final int EVENT_VIDEOPROCESS_FACERECOGNISE_SUCESS = 4001;     // 人脸识别成功事件
        static public final int EVENT_VIDEOPROCESS_FACERECOGNISE_FAILED = 4002;     // 人脸识别失败事件
    }
*/
boolean bGLContext = false;     // 在 AVSDk 场景下，设置当前为无 OpenGL 环境
TXCVideoPreprocessor mTxcFilter = new TXCVideoPreprocessor(this, bGLContext);
</pre>
4，设置参数
<pre>
／／ 根据自己的需要，在适当的地方设置参数
mTxcFilter.setBeautyStyle(0);           // 设置美颜风格，0: 光滑 1: 自然 2: 朦胧
mTxcFilter.setBeautyLevel(5);           // 设置美颜级别,范围 0～10
mTxcFilter.setWhitenessLevel(3);        // 设置美白级别,范围 0～10
mTxcFilter.setRuddyLevel(2);

// p 图版本
mTxcFilter.setFaceSlimLevel(5);         // 设置小脸级别,范围 0～10
mTxcFilter.setEyeScaleLevel(5);         // 设置大眼级别,范围 0～10
mTxcFilter.setFaceVLevel(5)             // 设置 V脸级别,范围 0～10
mTxcFilter.setFaceShortenLevel(5)       // 设置短脸级别,范围 0～10
mTxcFilter.setChinSlim(5)               // 设置长下巴级别,范围 0～10
mTxcFilter.setNoseScale(5)              // 设置小鼻级别,范围 0～10
mTxcFilter.setMotionTmpl(mMotionTmplPath);  ／／ 设置动效文件路径
</pre>
5，设置事件回调
<pre>
/*
EVENT_VIDEOPROCESS_FACERECOGNISE_SUCESS = 4001;     // 人脸识别成功事件（p图版）
EVENT_VIDEOPROCESS_FACERECOGNISE_FAILED = 4002;     // 人脸识别失败事件（p图版）
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
6：设置 AVSDK 相机数据回调；示例为基于iLiveSDK;
如果是直接集成AVSDK，请直接找到AVSDK 的 setLocalVideoPreProcessCallback就好了（**设置回调，一定要在进入房间成功后，才有效！！**）
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
7：**退出房间时，必须销毁滤镜资源！！否则下次进入房间，设置滤镜不生效！**
<pre>
ILVLiveManager.getInstance().quitRoom(new ILiveCallBack() {
    @Override
    public void onSuccess(Object data) {
    // 取消 AVSDK 相机数据回调（参数传null）
    boolean bRet = ILiveSDK.getInstance().getAvVideoCtrl().setLocalVideoPreProcessCallback(null);
        // 退出房间后，一定要销毁filter 资源；否则下次进入房间，setFilter将不生效或其他异常
        mTxcFilter.release();
    }
    @Override
    public void onError(String module, int errCode, String errMsg) {
    }
});
</pre>
### 有 OpenglGL 环境（如自定义采集，使用GLSurfaceView建立了OpenGL 环境或其他场景）使用代码范例：
1，同（无 OpenglGL 环境 1）

2，同（无 OpenglGL 环境 2）

3：创建和初始化 预处理类 和 初始化参数
<pre>
/*
static public class FrameFormat {
        static public final int TEXTURE = 0;        // 纹理格式
        static public final int I420 = 1;           // I420格式
        static public final int RGBA = 2;           // RGBA格式
        static public final int NV21 = 3;           // NV21格式
        static public final int TEXTURE_EXT = 4;    // 外部纹理输入(相机纹理输入时，可用)
        static public final int NONE = 5;           // 无输入
}
// TXCVideoPreprocessor.EventVideoProcess 事件定义
    static public class EventVideoProcess{
        static public final int EVENT_VIDEOPROCESS_FACERECOGNISE_SUCESS = 4001;     // 人脸识别成功事件
        static public final int EVENT_VIDEOPROCESS_FACERECOGNISE_FAILED = 4002;     // 人脸识别失败事件
    }
*/
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
8：释放sdk 资源
<pre>
if (null != mTxcFilter){
    mTxcFilter.release();
    mTxcFilter = null;
}
</pre>
