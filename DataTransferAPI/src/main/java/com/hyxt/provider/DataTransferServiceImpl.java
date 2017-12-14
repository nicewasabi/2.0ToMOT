package com.hyxt.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.hyxt.DTO.handler.TransferLinkHandler;
import com.hyxt.api.IDataTransferService;
import com.hyxt.utils.RedisPoolUtil;

import java.util.Map;

/**
 * @author songm
 * @version v1.0
 * @Description 数据转发接口API实现类
 * @Date: Create in 18:42 2017/12/12
 * @Modifide By:
 **/
@Service(interfaceClass=IDataTransferService.class)
public class DataTransferServiceImpl implements IDataTransferService {

    @Override
    public Boolean listen(String channelName, JSONObject platInfo) {
        RedisPoolUtil.getJedis().subscribe(new TransferLinkHandler((Map)platInfo),channelName);
        return true;
    }
}
