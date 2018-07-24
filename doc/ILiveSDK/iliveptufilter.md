# iliveptufilter 使用说明

| 接口名|  接口描述  |参数定义|返回值|
|---------|---------|---------|---------|
| **void init(Context var0, String var1)**| 初始化|var0：当前Activity的this指针 var1：从p图申请的licence文件名（assets根目录下）|无|
| **void setFilter(final String var1)** | 切换滤镜|var1: 滤镜文件绝对路径（如果在assets目录前缀都为"assets://qaveffect/filter/"） null：取消滤镜 "COMIC"：漫画 "GESE"：盛夏 "BRIGHTFIRE"：暖阳 "SKYLINE"：月光 "G1"：蔷薇 "ORCHID": 幽兰 "SHENGDAI":圣代  "AMARO": 薄荷 "FENBI": 浪漫|无|
| **void setPendant(final String var1)** | 设置动态贴纸|var1: 动态贴纸绝对路径（如果在assets目录前缀都为"assets://qaveffect/pendant/"）     null：取消所有动效    "video_rabbit"：兔子  "video_snow_white"：白雪公主|无|

### 使用代码范例：

1： 在工程中添加配置，引入 iliveptufilter 
<pre>
build.gradle 的dependency中添加

compile 'com.tencent.iliveptufilter:iliveptufilter:1.0.2'
</pre>
2：初始化p图资源
<pre>
// ptusdk_opensdk_test.licence 为从p图申请的licence文件名字；默认在 assets目录
AVVideoEffect.getInstance(LiveActivity.this).init(this, "ptusdk_opensdk_test.licence");
</pre>
3：绑定p图 与 AVSDK （要求AVSDK 1.9或以上版本）
<pre>
AVVideoEffect mEffect = AVVideoEffect.getInstance(LiveActivity.this);

AVVideoCtrl avVideoCtrl = ILiveSDK.getInstance().getAvVideoCtrl();
if (null != avVideoCtrl){
    avVideoCtrl.setEffect(mEffect);
}
</pre>
4：根据需要设置对应的滤镜和动态贴纸
<pre>
// 设置滤镜 （绝对路径 或 assets 路径）
// null：取消滤镜 "COMIC"：漫画 "GESE"：盛夏 "BRIGHTFIRE"：暖阳 "SKYLINE"：月光 "G1"：蔷薇 "ORCHID": 幽兰 "SHENGDAI":圣代  "AMARO": 薄荷 "FENBI": 浪漫
final String filterRootPath = "assets://qaveffect/filter/";

AVVideoEffect.getInstance(LiveActivity.this).setFilter(filterRootPath + "COMIC");

// 设置动态贴纸 （绝对路径 或 assets 路径）
// null：取消所有动效    "video_rabbit"：兔子  "video_snow_white"：白雪公主
final String pendantRootPath = "assets://qaveffect/pendant/";

AVVideoEffect.getInstance(LiveActivity.this).setPendant(pendantRootPath + "video_rabbit");
</pre>
