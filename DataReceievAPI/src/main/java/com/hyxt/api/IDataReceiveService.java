package com.hyxt.api;

/**
 * @author songm
 * @version v1.0
 * @Description 数据接收接口API，将2.0平台数据转发至交通部
 * @Date: Create in 11:19 2017/12/12
 * @Modifide By:
 **/
public interface IDataReceiveService {

    /**
     * 开启主链路监听端口
     *
     * @param port 端口号
     * @return 开启成功返回通道名称，否则返回错误编号
     */
    //TODO 调用链路管理的API
    public String openLocalPort(int port);


    /**
     *
     * @param channelName 监听到的消息往哪个channel放，可能是2.0到转发平台的channel，也可能是转发到交通部的channel
     */

    public void  handleMessage(String channelName);



}
