### ilivefilterSDK 使用说明

| 接口名|  接口描述  |参数定义|返回值|
|---------|---------|---------|---------|
| **TILFilter(Context activity)** | 构造函数|activity：当前Activity的this指针|无|
| **int setFilter(int number)** | 切换滤镜|number: 滤镜编号 <=0：原始滤镜 1：美颜 2：浪漫 3：清新 4：唯美 5：粉嫩 6: 怀旧 7:蓝调  8: 清凉 9: 日系|-1：失败 0：成功|
| **void setBeauty(int blur)** | 设置美颜级别|blur: 美颜级别（0~7； 如果设置高于7，使用默认值7）|无|
| **void setWhite(int white)** | 设置美白级别|white: 美白级别（（0~9； 如果设置高于9，使用默认值9）||
| **int processData(byte[] inOutData, int len, int width, int height, int type)** | 数据预处理|inOutData：原始数据（预处理后，数据仍然通过data返回） len：数据长度 width：视频宽 height：视频高 type：输入视频格式 1：I420 2：NV12 3：NV21（处理后输出数据固定为 I420）|-1： 失败 0：成功|
| **void destroyFilter()** | 销毁滤镜资源；在退出房间quitRoom 时，一定要调用此函数销毁滤镜资源；否则下次调用 setFilter无效|无|无|

### 使用代码范例：

1： 在工程中添加配置，引入 ilivefilter 
<pre>
build.gradle 的dependency中添加

compile 'com.tencent.ilivefilter:ilivefilter:1.1.14'
</pre>
2：创建预处理类
<pre>
TILFilter mUDFilter = new TILFilter(this);

</pre>
3：设置 AVSDK相机数据回调（**设置回调，一定要在进入房间成功后，才有效！！**;顺序可与 4步骤 互换）
<pre>
boolean bRet = ILiveSDK.getInstance().getAvVideoCtrl().setLocalVideoPreProcessCallback(new AVVideoCtrl.LocalVideoPreProcessCallback(){
    @Override
    public void onFrameReceive(AVVideoCtrl.VideoFrame var1) {
        // 回调的数据，传递给 ilivefilter processData接口处理
        mUDFilter.processData(var1.data, var1.dataLen, var1.width, var1.height, var1.srcType);
    }
});
</pre>
4：在任意地方，设置滤镜，和美颜级别（**美颜级别适用所有的滤镜**；可与  3步骤 互换）
<pre>
// 小于或等于0：原始滤镜 1：美颜 2：浪漫 3：清新 4：唯美 5：粉嫩 6: 怀旧 7:蓝调  8: 清凉 9: 日系
mUDFilter.setFilter(1);

// 设置美颜级别 （级别为 0~7）
mUDFilter.setBeauty(5);

// 设置美白级别（级别为 0~9）
mUDFilter.setWhite(3)；
</pre>
5：**退出房间时，必须销毁滤镜资源！！否则下次进入房间，设置滤镜不生效！**
<pre>
ILVLiveManager.getInstance().quitRoom(new ILiveCallBack() {
    @Override
    public void onSuccess(Object data) {
    // 取消 AVSDK 相机数据回调（参数传null）
    boolean bRet = ILiveSDK.getInstance().getAvVideoCtrl().setLocalVideoPreProcessCallback(null);
        // 退出房间后，一定要销毁filter 资源；否则下次进入房间，setFilter将不生效或其他异常
        mUDFilter.setFilter(-1);
        mUDFilter.destroyFilter();
    }
    @Override
    public void onError(String module, int errCode, String errMsg) {
    }
});
            
</pre>

### 视频预处理实现流程
关于具体的实现流程，可以参考（自定义预处理视频数据）：
https://www.qcloud.com/document/product/268/7645
