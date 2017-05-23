## 直播质量数据接口

直播质量数据接口可以获取当前直播质量情况。该接口可以获取收发包丢包率，上下行码率，上行视频帧率，CPU使用率，视频分辨率等。

## Android接口

**质量数据获取**

    //必须在房间建立好后调用，建议判空逻辑
    ILiveQualityData  data = ILiveRoomManager.getInstance().getQualityData()

**数据结构**


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
	    public int getUpFPS() {
            return upFPS;
        }
        //获取各路视频信息，key为id，value为该id的视频信息
        public Map<String, LiveInfo> getLives();
	}

      public class LiveInfo {
           //获取视频宽度
           public int getWidth();
           //获取视频高度
           public int getHeight()
      }
