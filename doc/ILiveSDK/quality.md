##质量相关接口简介

###ILiveQualityData

###数据内容

ILiveQualityData是ILiveSDK对音视频播放质量数据的封装。具体数据内容如下：

    public class ILiveQualityData {
		//获取开始时间
	    public long getStartTime() {
	        return startTime;
	    }
		//获取结束时间
	    public long getEndTime() {
	        return endTime;
	    }
		//获取发包丢包率，以百分比乘以100为返回值。如丢包率为12.34%，则sendLossRate = 1234
	    public int getSendLossRate() {
	        return sendLossRate;
	    }
		//获取收包丢包率，以百分比乘以100为返回值。如丢包率为12.34%，则recvLossRate = 1234
	    public int getRecvLossRate() {
	        return recvLossRate;
	    }
		//获取app占用CPU，以百分比乘以100为返回值。如占用率为12.34%，则appCPURate = 1234
	    public int getAppCPURate() {
	        return appCPURate;
	    }
		//获取系统占用CPU，以百分比乘以100为返回值。如占用率为12.34%，则sysCPURate = 1234
	    public int getSysCPURate() {
	        return sysCPURate;
	    }
		//获取发送码率
	    public int getSendKBS() {
	        return sendKBS;
	    }
		//获取接收码率
	    public int getRecvKBS() {
	        return recvKBS;
	    }
		//获取视频帧率
	    public int getInteractiveSceneFPS() {
	        return interactiveSceneFPS;
	    }
	}


####使用方式
客户端程序可以在主线程中调用ILiveRoomManager.getInstance().getQualityData()获取当前质量数据。