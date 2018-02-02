## ANDROID_TILFilterSDK_ChangeList

###### V1.1.11(2017-05-25)
* 1、1，解决魅族pro5绿屏 和 oppo A33 ANR的问题
* 2、修复反馈的概率性崩溃 和 内存泄漏的一些问题

###### V1.1.12(2017-05-27)
* 1、修复切换相机时，出现第一帧画面颠倒的问题
* 2、修复 vivo X3t 等较低端机型，美颜灰屏的问题
* 3、修复 oppo a33 美颜时卡死的问题

###### V1.1.13(2017-06-19)
* 1、优化美颜效果
* 2、修复切换分辨率时，sdk崩溃的问题

###### V1.1.14(2017-06-26)
* 1、优化sdk性能
* 2、修复部分机型出现的崩溃问题

###### V1.1.16(2017-09-19)
* 1、 ilivefiler更名为TXMVideoPreprocessor
* 2、 以回调函数方式返回处理结果
* 3、 增加多套美颜方案（光滑、自然、朦胧）
* 4、 增加红润、水印、裁剪、缩放、旋转、镜像、v脸、短脸、下巴、瘦鼻功能
* 5、 TXCVideoPreprocessor 兼容 老版本 ilivefilter</br>
* 6、 增加设置多个水印接口 setWaterMarkList</br>
* 7、 接口名字调整
setFaceShortenLevel->setFaceShortLevel
setChinSlim->setChinLevel
setNoseScale->setNoseSlimLevel

###### V1.1.17(2017-09-25)
* 1、 开放 TXEFrameFormat 数据类型类
* 2、 美颜、滤镜、p图功能，添加 Android API Level >= 17（Android 4.2和以上系统）限制

###### V1.1.18(2017-10-10)
* 1、 解决sdk与视频编辑UGC SDK符号冲突的问题</br>

###### V1.1.19(2018-1-25)
* 1、 解决添加水印后，旋转手机，预览画面变形的问题

###### V1.1.20(2018-2-2)
* 1、 适配avsdk 的 setAfterPreviewListener 数据回调接口，效率更高，建议废弃以前的 setLocalVideoPreProcessCallback 数据回调接口
* 2、 更新新版p图，新版p图支持AI抠背，手势识别等新动效
* 3、 降低sdk cpu 和 gpu消耗
* 4、 修复sdk内存抖动 和 低端机型Android系统频繁 GC 的问题
