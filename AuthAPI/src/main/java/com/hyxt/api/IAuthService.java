package com.hyxt.api;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * @author songm
 * @version v1.0
 * @Description 认证接口API
 * @Date: Create in 11:18 2017/12/12
 * @Modifide By:
 **/
public interface IAuthService {

    /**
     * 建立与交通部连接
     * @param platId 平台ID
     * @param ip 连接的ip
     * @param port 连接的端口
     * @return 如果创建连接成功返回通道名称，否则返回错误编号
     */
    public String connectMOT(String platId, String ip, String port);

    /**
     * 建立与2.0平台从链路
     * @param platId 平台ID
     * @param ip 连接的ip
     * @param port 连接的端口
     * @return 如果创建连接成功返回通道名称，否则返回错误编号
     */
    //public String connectVMS(String platId, String ip, String port);

    /**
     * 监听与交通部链路的通道
     * @param channelName 通道名称
     * @return
     */
    public Boolean listenTransferLink(String channelName);
    /**
     * 监听与2.0从链路的通道
     * @param channelName 通道名称
     * @return
     */
    //public Boolean listenSlaveLink(String channelName);
}
