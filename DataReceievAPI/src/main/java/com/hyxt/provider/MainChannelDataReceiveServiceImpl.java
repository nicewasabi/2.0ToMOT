package com.hyxt.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.hyxt.DTO.handler.MainLinkHandler;
import com.hyxt.api.IMainChannelDataReceiveService;
import com.hyxt.utils.RedisPoolUtil;

/**
 * @author songm
 * @version v1.0
 * @Description
 * @Date: Create in 18:43 2017/12/12
 * @Modifide By:
 **/
@Service(interfaceClass=IMainChannelDataReceiveService.class)
public class MainChannelDataReceiveServiceImpl implements IMainChannelDataReceiveService {

    /**
     * 开启主链路监听端口
     *
     * @param port 端口号
     * @return 开启成功返回通道名称，否则返回错误编号
     */
    @Override
    public String openLocalPort(int port) {
        return null;
    }

    /**
     * @param channelName 监听到的消息往哪个channel放，可能是2.0到转发平台的channel，也可能是转发到交通部的channel
     */
    @Override
    public void handleMessage(String channelName) {

        RedisPoolUtil.getJedis().subscribe(new MainLinkHandler(),channelName);


    }
}
