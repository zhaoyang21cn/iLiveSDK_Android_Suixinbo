## 常见问题

### 黑屏
- 确认进入房间成功(进房间成功回调)
- 确认正常设置渲染控件(AVRootView或ILiveRootView)
- [本地]确认摄像头打开(autoCamera为true或手动调用enableCamera)
    *摄像头打开失败会通过exceptionListener上抛异常*
```java
// 常见异常
"android.view.WindowManager$BadTokenException: 
Unable to add window android.view.ViewRootImpl$W@cc80cad -- permission denied for window type 2002]"

这种为应用被禁用了悬浮窗权限
手机【设置】 【应用】 找到应用 检查下 【可出现在顶部的应用程序】 是否开启
```
- [远程]有收到对方画面(onEndPoint收到has camera事件)
- [远程]确认请求对方画面成功(失败会通过exceptionListener上抛)
- 有触发renderUserView渲染方法
