## 默认配置


[腾讯云控制台](https://console.cloud.tencent.com/rav)可以按平台，配置自己定制的音视频参数(角色)。sdk会在登录成功时从后台拉取这个配置。

但是在网络不理想的情况下，(首次)拉配置失败后，将面临进房找不到角色的问题，这里推荐使用默认角色来进行配置：


### 默认角色配置

ilivesdk 1.9.6之后版本，在原登录接口中添加可选参数用于配置默认角色(进房角色不存在时将使用默认角色)


参数|类型|描述
--:|:--:|:--
userId|String|用户标识
userSig|String|用户签名
roleCfg|String|角色配置

roleCfg的格式参考 [配置格式](#数据格式)

### 自定义角色配置

同时，为满足部分用户本地配置的需要，sdk支持完全本地配置(不再从后台拉取配置，Spear配置将失败)

新接口名称 iLiveLoginWithSpear

参数|类型|描述
--:|:--:|:--
userId|String|用户标识
userSig|String|用户签名
spearCfg|String|自定义配置信息

spearCfg的格式参考 [配置格式](#数据格式)


同时在[统一事件回调](EventListener.md)中添加onSetSpearConfigEvent上抛配置是否成功


### 数据格式

这里不推荐用户自行填写配置参数，即便是打算自定义配置，也建议在Spear后台配置好后，通过以下地址获取:
```
http://conf.voice.qcloud.com/index.php?sdk_appid=[sdkappid]&interface=Voice_Conf_Download&platform=[platform]
```

sdkappid 为用户自己的应用标识

platform 为对应的平台类型((0 : pc/web, 1 : ios, 2 : android,  4: mac)

例如sdkappid为1400049564的iOS平台配置可通过下面地址获取(可以直接浏览器打开):
```
http://conf.voice.qcloud.com/index.php?sdk_appid=1400049564&interface=Voice_Conf_Download&platform=1
```

如果是配置默认角色配置，只需找到对应角色的字段内容填入roleCfg即可(示例):
```
{
    "audio":{
        "aec":1,
        "agc":0,
        "ans":1,
        "anti_dropout":0,
        "au_scheme":1,
        "channel":2,
        "codec_prof":4106,
        "frame":40,
        "kbps":24,
        "max_antishake_max":1000,
        "max_antishake_min":400,
        "min_antishake":120,
        "sample_rate":48000,
        "silence_detect":0
    },
    "is_default":0,
    "net":{
        "rc_anti_dropout":1,
        "rc_init_delay":100,
        "rc_max_delay":500
    },
    "role":"LiveGuest",
    "type":3,
    "video":{
        "anti_dropout":0,
        "codec_prof":5,
        "format":-2,
        "format_fix_height":480,
        "format_fix_width":640,
        "format_max_height":-1,
        "format_max_width":-1,
        "fps":15,
        "fqueue_time":-1,
        "live_adapt":0,
        "maxkbps":400,
        "maxqp":-1,
        "minkbps":400,
        "minqp":-1,
        "qclear":1,
        "small_video_upload":0
    }
}
```
如果采用自定义角色配置，则需将全部内容传入(建议保存在本地文件读取后传入)

**请勿自行修改其中的参数**
