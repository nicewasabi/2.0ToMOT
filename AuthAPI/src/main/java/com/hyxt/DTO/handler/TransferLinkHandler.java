package com.hyxt.DTO.handler;

import cn.com.cnpc.vms.common.exception.ProtocolEscapeExeption;
import cn.com.cnpc.vms.common.util.BytesUtil;
import com.hyxt.DTO.protocol.JTBZFHeader;
import com.hyxt.DTO.protocol.ProtocolUtil;
import com.hyxt.utils.IRedisMessageListener;
import com.hyxt.utils.MessageConstant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * @author songm
 * @version v1.0
 * @Description 交通部链路消息处理类（接受来自交通部转发链路的下行消息）
 * @Date: Create in 18:51 2017/12/12
 * @Modifide By:
 **/
public class TransferLinkHandler extends IRedisMessageListener{

    private static final Log log = LogFactory.getLog(TransferLinkHandler.class);

    private Map<String,Object> config;

    /**
     * 给一个无参构造器
     */
    public TransferLinkHandler() {

    }

    /**
     * 平台的一些配置参数，如接入码、平台id、链接地址。。。
     * @param config
     */
    public TransferLinkHandler(Map<String,Object> config) {
        this.config = config;
    }

    /**
     * 监听缓存收到的消息
     * @param channel
     * @param msg
     */
    public void onMessage(String channel, String msg) {
        // 转换
        byte[] code = msg.getBytes();
        log.info("<<<交通部转发平台链路接入信息:" + BytesUtil.bytesToHexString(code));
        try {
            // 反转义
            code = ProtocolUtil.reverseEscapeData(code);
            // 获取外部消息头
            JTBZFHeader messageHeader = ProtocolUtil.getHeader(code);
            // 外部消息流水号
            int seqNumber = messageHeader.getNum();
            // 拿到计时缓存
           /* LimitedCache cache = TransferConnection.getCache();
            // 如果还没超时 就从计时缓存里删除
            if (cache.containsKey(seqNumber)) {
                cache.remove(seqNumber);
            }*/
            // 通过消息ID来分类判断一下
            switch (messageHeader.getId()) {
                case (byte) 0x80:// 认证应答
                    try {
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                byte[] beatMsg = MessageConstant.transferLinkHeartBeat();
                                // 然后启动心跳，与交通部的心跳（暂未提供发消息接口）

                            }
                        }, 0, 30 * 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case (byte) 0x81:// 心跳应答
                    break;
            }
        } catch (ProtocolEscapeExeption protocolEscapeExeption) {
            protocolEscapeExeption.printStackTrace();
        }
    }
}
