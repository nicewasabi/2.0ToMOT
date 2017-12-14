package com.hyxt.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.hyxt.DTO.handler.MainLinkHandler;
import com.hyxt.api.IDataReceiveService;
import com.hyxt.utils.RedisPoolUtil;

/**
 * @author songm
 * @version v1.0
 * @Description
 * @Date: Create in 18:43 2017/12/12
 * @Modifide By:
 **/
@Service(interfaceClass=IDataReceiveService.class)
public class DataReceiveServiceImpl implements IDataReceiveService {
    @Override
    public String open(String platId, int port) {
        //调用链路管理接口创建主链路服务端（还未提供接口，暂时空缺）
        //
        return null;
    }

    @Override
    public Boolean listen(String channelName) {
        RedisPoolUtil.getJedis().subscribe(new MainLinkHandler(map),channelName);
        return true;
    }
}
