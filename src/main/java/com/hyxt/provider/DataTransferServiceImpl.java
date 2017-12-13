package com.hyxt.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.hyxt.DTO.handler.TransferLinkHandler;
import com.hyxt.api.IDataTransferService;
import com.hyxt.utils.RedisPoolUtil;

/**
 * @author songm
 * @version v1.0
 * @Description
 * @Date: Create in 18:42 2017/12/12
 * @Modifide By:
 **/
@Service(interfaceClass=IDataTransferService.class)
public class DataTransferServiceImpl implements IDataTransferService {
    @Override
    public Boolean listen(String channelName) {
        RedisPoolUtil.getJedis().subscribe(new TransferLinkHandler(channelName),channelName);
        return true;
    }
}
