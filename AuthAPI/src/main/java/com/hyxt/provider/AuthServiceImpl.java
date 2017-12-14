package com.hyxt.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.hyxt.DTO.handler.TransferLinkHandler;
import com.hyxt.api.IAuthService;
import com.hyxt.utils.RedisPoolUtil;

import java.util.Map;

/**
 * @author songm
 * @version v1.0
 * @Description 认证接口API实现类
 * @Date: Create in 18:41 2017/12/12
 * @Modifide By:
 **/
@Service(interfaceClass=IAuthService.class)
public class AuthServiceImpl implements IAuthService {

    @Override
    public String connectMOT(String platId, String ip, String port) {
        //调用链路管理接口创建与交通部链路客户端（还未提供接口，暂时空缺）

        return null;
    }


    @Override
    public Boolean listenTransferLink(String channelName) {
        RedisPoolUtil.getJedis().subscribe(new TransferLinkHandler(),channelName);
        return true;
    }

}
