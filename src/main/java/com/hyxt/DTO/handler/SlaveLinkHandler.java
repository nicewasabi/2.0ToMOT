package com.hyxt.DTO.handler;

import com.hyxt.utils.IRedisMessageListener;

/**
 * @author songm
 * @version v1.0
 * @Description 从链路消息处理类（接受来自2.0通过从链路发送的消息）
 * @Date: Create in 18:51 2017/12/12
 * @Modifide By:
 **/
public class SlaveLinkHandler extends IRedisMessageListener {

    private String ChannelName;

    /**
     * 根据channelName可以确定是哪个平台的链路
     * @param channelName
     */
    public SlaveLinkHandler(String channelName) {
        ChannelName = channelName;
    }

    /**
     * 监听缓存收到的消息
     * @param channel
     * @param message
     */
    public void onMessage(String channel, String message) {
        super.onMessage(channel, message);
    }
}
