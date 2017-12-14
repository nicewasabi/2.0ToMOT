package com.hyxt.api;

import com.alibaba.fastjson.JSONObject;

/**
 * @author songm
 * @version v1.0
 * @Description 数据转发接口API，将交通部数据下发至2.0平台
 * @Date: Create in 11:18 2017/12/12
 * @Modifide By:
 **/
public interface IDataTransferService {

    /**
     * 监听与交通部连接的通道
     * @param channelName 通道名称
     * @param params 下发消息参数（接入码，平台id，主、从、转发链路通道。。。）
     * @return
     */
    public Boolean listen(String channelName, JSONObject params);

}
