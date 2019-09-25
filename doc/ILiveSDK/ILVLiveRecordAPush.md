##发消息

###发文本消息
            //发送消息
            ILVText iliveText = new ILVText("ss", "", ILVLiveConstants.GROUP_TYPE);
            iliveText.setText("" + textInput.getText());
            //发送消息
            ILiveRoomManager.getInstance().sendText(iliveText, new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    Toast.makeText(LiveActivity.this, "send succ!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {

                }

            });
            
###上麦邀请  



            //邀请上麦
            ILVCustomCmd cmd = new ILVCustomCmd();
            cmd.setCmd(ILVLiveConstants.ILVLIVE_CMD_INVITE);
            cmd.setType(ILVLiveConstants.C2C_TYPE);
            cmd.setDestid("" + memId.getText());
            cmd.setParam("");
            ILiveRoomManager.getInstance().sendCustomCmd(cmd, new ILiveCallBack<TIMMessage>() {
                @Override
                public void onSuccess(TIMMessage data) {
                    Toast.makeText(LiveActivity.this, "invite send succ!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {

                }

            });            
  
            
###下麦命令
           //关闭上麦
            ILVCustomCmd cmd = new ILVCustomCmd();
            cmd.setCmd(ILVLiveConstants.ILVLIVE_CMD_INVITE_CLOSE);
            cmd.setType(ILVLiveConstants.C2C_TYPE);
            cmd.setDestid("" + memId.getText());
            cmd.setParam("");
            ILiveRoomManager.getInstance().sendCustomCmd(cmd, new ILiveCallBack<TIMMessage>() {
                @Override
                public void onSuccess(TIMMessage data) {
                    Toast.makeText(LiveActivity.this, "invite send succ!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {

                }

            });   

###解析信令上麦
        ILVLiveConfig liveConfig = new ILVLiveConfig();

        liveConfig.setLiveMsgListener(new ILVLiveConfig.ILVLiveMsgListener() {
            @Override
            public void onNewTextMsg(String text, String id) {
                Toast.makeText(LiveActivity.this, "onNewTextMsg : " + text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNewCmdMsg(int cmd, String param, String id) {
                switch (cmd) {
                    case ILVLiveConstants.ILVLIVE_CMD_INVITE:
                        Toast.makeText(LiveActivity.this, "onNewCmdMsg : received a invitation! ", Toast.LENGTH_SHORT).show();
                        ILiveLog.d(TAG, "ILVB-LiveApp|received ");
                        ILiveRoomManager.getInstance().upToVideoMember(ILVLiveConstants.VIDEO_MEMBER_AUTH, ILVLiveConstants.VIDEO_MEMBER_ROLE, new ILiveCallBack() {
                            @Override
                            public void onSuccess(Object data) {

                            }

                            @Override
                            public void onError(String module, int errCode, String errMsg) {

                            }
                        });
                        break;
                    case  ILVLiveConstants.ILVLIVE_CMD_INVITE_CANCEL:

                        break;
                    case ILVLiveConstants.ILVLIVE_CMD_INVITE_CLOSE:
                        ILiveRoomManager.getInstance().downToNorMember(ILVLiveConstants.NORMAL_MEMBER_AUTH, ILVLiveConstants.NORMAL_MEMBER_ROLE, new ILiveCallBack() {
                            @Override
                            public void onSuccess(Object data) {

                            }

                            @Override
                            public void onError(String module, int errCode, String errMsg) {

                            }
                        });
                        break;
                    case ILVLiveConstants.ILVLIVE_CMD_INTERACT_AGREE:
                        break;
                    case  ILVLiveConstants.ILVLIVE_CMD_INTERACT_REJECT:
                        break;
                }

            }

            @Override
            public void onNewCustomMsg(int cmd, String param, String id) {
                Toast.makeText(LiveActivity.this, "cmd "+ cmd, Toast.LENGTH_SHORT).show();

            }
        });
                     
            
            
            