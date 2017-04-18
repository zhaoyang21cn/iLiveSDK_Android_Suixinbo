package com.tencent.qcloud.suixinbo.presenters.viewinface;

import com.tencent.qcloud.suixinbo.model.RecordInfo;

import java.util.ArrayList;

/**
 * Created by xkazerzhang on 2016/12/22.
 */
public interface RecListView {
    void onUpdateRecordList(ArrayList<RecordInfo> list);
}
