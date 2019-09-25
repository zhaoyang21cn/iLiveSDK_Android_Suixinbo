
## 进房失败(Role no exists)

iLiveSDK 1.9.5版本引入了进房角色不存在时直接报错的机制。但是在确认角色已配置的情况下，仍有概率出现进房出现角色不存在情况


### 为什么会失败

这里简单介绍一下SDK这一块的工作原理:

> 1、登录SDK时从服务器拉取角色配置表并缓存本地
>
> 2、进房时从配置表中查找角色



从上面的流程可以看到，如果在登录时拉取角色配置失败，将会导致无法进房


### 拉取角色配置地址

```
http://conf.voice.qcloud.com/index.php?sdk_appid=[SDKAPPID]&interface=Voice_Conf_Download&platform=[平台]
```
- [SDKAPPID]  对应用户的应用标识
- [平台]   对应终端平台, 0(pc/web), 1(ios), 2(android), 4(mac)

示例:
```
http://conf.voice.qcloud.com/index.php?sdk_appid=1400028285&interface=Voice_Conf_Download&platform=1
```

对于拉取配置失败时，用户可以尝试使用浏览器访问上面地址，排查下网络故障


### 如何解决这个问题

这里推荐的有两种方式可以避免这个问题：

####  1、重试

即在进房失败时，判断若失败原因是角色不存在，可以尝试直接重新登录(重新拉取角色配置)


####  1、设置默认配置

登录接口中已支持设置默认角色配置(拉取失败时会使用默认角色配置)

登录接口参数:

参数名|类型|描述
:--|:--:|:--
id|String|用户标识
userSig|String|用户签名
spearCfg|String|默认角色配置
loginCallBack|ILiveCallBack|登录回调


这里的spearCfg即可用设置默认角色配置

我们可以通过上面的拉取角色配置地址查看当前的配置，并在其中选取一个角色作为默认配置：
-  1、拼spear配置地址：
> http://conf.voice.qcloud.com/index.php?sdk_appid=1400028285&interface=Voice_Conf_Download&platform=1
-  2、在浏览器中访问第一步的地址，获取json字符串
```
{"data":{"biz_id":1400049564,"conf":[{"audio":{"aec":1,"agc":0,"ans":1,"anti_dropout":0,"au_scheme":1,"channel":2,"codec_prof":4106,"frame":40,"kbps":24,"max_antishake_max":1000,"max_antishake_min":400,"min_antishake":120,"sample_rate":48000,"silence_detect":0},"is_default":1,"net":{"rc_anti_dropout":1,"rc_init_delay":100,"rc_max_delay":500},"role":"LiveMaster","type":1,"video":{"anti_dropout":0,"codec_prof":5,"format":-2,"format_fix_height":480,"format_fix_width":640,"format_max_height":-1,"format_max_width":-1,"fps":15,"fqueue_time":-1,"live_adapt":0,"maxkbps":400,"maxqp":-1,"minkbps":400,"minqp":-1,"qclear":1,"small_video_upload":0}},{"audio":{"aec":1,"agc":0,"ans":1,"anti_dropout":0,"au_scheme":1,"channel":2,"codec_prof":4106,"frame":40,"kbps":24,"max_antishake_max":1000,"max_antishake_min":400,"min_antishake":120,"sample_rate":48000,"silence_detect":0},"is_default":0,"net":{"rc_anti_dropout":1,"rc_init_delay":500,"rc_max_delay":1000},"role":"Guest","type":2,"video":{"anti_dropout":0,"codec_prof":5,"format":-2,"format_fix_height":480,"format_fix_width":640,"format_max_height":-1,"format_max_width":-1,"fps":15,"fqueue_time":-1,"live_adapt":0,"maxkbps":400,"maxqp":-1,"minkbps":400,"minqp":-1,"qclear":1,"small_video_upload":0}},{"audio":{"aec":1,"agc":0,"ans":1,"anti_dropout":0,"au_scheme":1,"channel":2,"codec_prof":4106,"frame":40,"kbps":24,"max_antishake_max":1000,"max_antishake_min":400,"min_antishake":120,"sample_rate":48000,"silence_detect":0},"is_default":0,"net":{"rc_anti_dropout":1,"rc_init_delay":100,"rc_max_delay":500},"role":"LiveGuest","type":3,"video":{"anti_dropout":0,"codec_prof":5,"format":-2,"format_fix_height":480,"format_fix_width":640,"format_max_height":-1,"format_max_width":-1,"fps":15,"fqueue_time":-1,"live_adapt":0,"maxkbps":400,"maxqp":-1,"minkbps":400,"minqp":-1,"qclear":1,"small_video_upload":0}}],"platform":1,"scheme":1,"sequence":20},"errmsg":"success.","retcode":0}
```
- 3、找到默认角色配置(如Guest)对应的数据:
```
{"audio":{"aec":1,"agc":0,"ans":1,"anti_dropout":0,"au_scheme":1,"channel":2,"codec_prof":4106,"frame":40,"kbps":24,"max_antishake_max":1000,"max_antishake_min":400,"min_antishake":120,"sample_rate":48000,"silence_detect":0},"is_default":0,"net":{"rc_anti_dropout":1,"rc_init_delay":500,"rc_max_delay":1000},"role":"Guest","type":2,"video":{"anti_dropout":0,"codec_prof":5,"format":-2,"format_fix_height":480,"format_fix_width":640,"format_max_height":-1,"format_max_width":-1,"fps":15,"fqueue_time":-1,"live_adapt":0,"maxkbps":400,"maxqp":-1,"minkbps":400,"minqp":-1,"qclear":1,"small_video_upload":0}}
```

- 4、在登录时将上面的参数传入spearCfg

**注意: iOS注意要在双引号前加入转义符\\**
