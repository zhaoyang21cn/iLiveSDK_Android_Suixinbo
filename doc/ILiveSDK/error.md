## 错误码


模块名称常量|模块名称取值|错误码
:--|:--|:--:
Module_ILIVESDK|ILiveSDK|见下表
Module_AVSDK|AVSDK|[查看](./avsdkErr.md)
Module_IMSDK|IMSDK|[查看](https://www.qcloud.com/doc/product/269/1671)
Module_TLSSDK|TLSSDK|[查看](http://bbs.qcloud.com/thread-8309-1-1.html)

### ILiveSDK错误码
常量定义|错误码|错误描述
:--|--:|:--:
NO_ERR                  |0|成功
ERR_IM_NOT_READY        |8001|IM模块未就绪或
ERR_AV_NOT_READY        |8002|AV模块未就绪或未加载
ERR_NO_ROOM             |8003|无有效的房间
ERR_ALREADY_EXIST       |8004|目标已存在
ERR_NULL_POINTER        |8005|空指针错误
ERR_ENTER_AV_ROOM_FAIL  |8006|进入AV房间失败
ERR_USER_CANCEL         |8007|用户取消
ERR_WRONG_STATE         |8008|状态异常(已废弃,现仅在CallSDK通话状态切换失败时返回)
ERR_NOT_LOGIN           |8009|未登录
ERR_ALREADY_IN_ROOM     |8010|已在房间中
ERR_BUSY_HERE           |8011|内部忙(上一请求未完成)
ERR_NET_UNDEFINE        |8012|网络未识别或网络不可达
ERR_SDK_FAILED          |8020|iLiveSDK处理失败(通用)
ERR_INVALID_PARAM       |8021|接口传入无效的参数(如创建或加入房间时option为空)
ERR_NOT_FOUND           |8022|无法找到目标
ERR_NOT_SUPPORT         |8023|请求不支持
ERR_ALREADY_STATE       |8024|状态已到位(一般为重复调用引起)
ERR_KICK_OUT            |8050|被踢下线
ERR_EXPIRE              |8051|票据过期(需更新票据userSig)
ERR_PARSE_FAIL          |8052|解析网络请求失败
ERR_ALLOC_FAIL          |8053|内存分配失败，检查内存是否充足

#### 视频通话错误码
*主叫方和接听方错误码一致*

常量定义|错误码|错误描述
:--|--:|:--:
ERR_CALL_SPONSOR_CANCEL     |1|呼叫方取消
ERR_CALL_SPONSOR_TIMEOUT    |2|呼叫方超时
ERR_CALL_RESPONDER_REFUSE   |3|接听方拒绝
ERR_CALL_HANGUP             |4|挂断(通话建立后)
ERR_CALL_RESPONDER_LINEBUSY |5|接听方占线
ERR_CALL_DISCONNECT         |6|通话被服务器回收
