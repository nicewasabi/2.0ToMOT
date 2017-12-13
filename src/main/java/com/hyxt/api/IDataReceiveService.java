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
     * 创建主链路服务端
     * @param port 端口号
     * @return 开启成功返回通道名称，否则返回错误编号
     */
    public String open(int port);

    /**
     * 监听创建的通道
     * @param channelName 通道名称
     * @return
     */
    public Boolean listen(String channelName);

}
