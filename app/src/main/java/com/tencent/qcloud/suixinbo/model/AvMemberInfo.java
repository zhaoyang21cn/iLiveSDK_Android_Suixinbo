package com.tencent.qcloud.suixinbo.model;

import android.graphics.Bitmap;

/**
 * 互动直播成员
 */
public class AvMemberInfo {
	public String identifier = "";
	public boolean hasAudio = false;
	public boolean hasCameraVideo = false;
	public boolean hasScreenVideo = false;
	public boolean isShareMovie = false;
	public boolean hasGetInfo = false;
	public String name = null;
	public Bitmap faceBitmap = null;

	@Override
	public String toString() {
		return "AvMemberInfo identifier = " + identifier + ", hasAudio = " + hasAudio
				+ ", hasCameraVideo = " + hasCameraVideo + ", hasScreenVideo = " + hasScreenVideo
				+ ", isShareMovie = " + isShareMovie + ", hasGetInfo = "
				+ hasGetInfo + ", name = " + name;
	}


}