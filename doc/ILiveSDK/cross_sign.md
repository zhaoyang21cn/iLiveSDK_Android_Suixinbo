## 跨房密钥的生成

### 跨房连麦
iLiveSDK中的每个用户都可以创建自己的直播间，直播间内可以允许多个用户上麦，同时还允许与其它正在直播中的主播进行互动，这种跨房能力，我们称为跨房连麦。

### 跨房密钥
跨房连麦需要知道对方所在房间号，对方的用户id，同里还需要以此计算出跨房密钥，作为鉴权。该密钥作为使用跨房连麦接口linkRoom的参数。

```diff
-随心播使用的跨房密钥由随心播业务服务器计算生成
```

### 计算条件
计算跨房密钥需要使用[音视频权限密钥](https://www.qcloud.com/document/product/268/3220)对跨房信息字段进行加密，生成最终密钥。

* 音视频权限密钥可在腾讯云控制台中查看:


![](https://zhaoyang21cn.github.io/iLiveSDK_Help/readme_img/cross_key.png)

* 而跨房信息需要的信息有
> 1、发起方的房间号

> 2、发起方的用户id

> 3、被连麦的房间号

> 4、被连麦的用户id

### 计算方法
* 1、使用google protobuf序列化[ConnRoomSig](https://zhaoyang21cn.github.io/iLiveSDK_Help/download/corss/conn_room_sig.proto)对象，输出二进制字符串

* 2、使用[tea](https://zhaoyang21cn.github.io/iLiveSDK_Help/download/corss/tea.zip)加密工具用音视频密钥对二进制字符串进行加密

* 3、将加密串转换成16进制字符串即可

### 演示工具
随心播业务后使用的是基于上述计算方法生成的[linkSig](https://zhaoyang21cn.github.io/iLiveSDK_Help/download/corss/linksig.rar)工具

该工具使用方法如下:
```
./linkSig [发起方用户id] [发起方房间号] [被连麦用户id] [被连麦的房间号] [音视频密钥]
```
示例
```
# ./linkSig green 10017 ghost 10024 3b407fa2d9857f31
162A7E8A6D17A9FB8FB6608A1A441E4981226E219C57B6CA434312EBFE2CE6ADEEEB79E5E66538ACFE22BE3F1C6F58
```
