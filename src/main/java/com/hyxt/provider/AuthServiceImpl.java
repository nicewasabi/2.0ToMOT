package com.hyxt.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.hyxt.DTO.handler.SlaveLinkHandler;
import com.hyxt.DTO.handler.TransferLinkHandler;
import com.hyxt.api.IAuthService;
import com.hyxt.utils.RedisPoolUtil;

/**
 * @author songm
 * @version v1.0
 * @Description
 * @Date: Create in 18:41 2017/12/12
 * @Modifide By:
 **/
@Service(interfaceClass=IAuthService.class)
public class AuthServiceImpl implements IAuthService {

    @Override
    public String connectMOT(String ip, String port) {
        //调用链路管理接口创建与交通部链路客户端（还未提供接口，暂时空缺）
        return null;
    }

    @Override
    public String connectVMS(String ip, String port) {
        //调用链路管理接口创建从链路客户端（还未提供接口，暂时空缺）
        return null;
    }

    @Override
    public Boolean listenTransferLink(String channelName) {
        RedisPoolUtil.getJedis().subscribe(new TransferLinkHandler(channelName),channelName);
        return true;
    }

    @Override
    public Boolean listenSlaveLink(String channelName) {
        RedisPoolUtil.getJedis().subscribe(new SlaveLinkHandler(channelName),channelName);
        return true;
    }
}
