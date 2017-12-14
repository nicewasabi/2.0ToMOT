package com.hyxt.DTO.handler;

import cn.com.cnpc.vms.common.cache.LocalCache;
import cn.com.cnpc.vms.common.cache.LocalCacheManager;
import cn.com.cnpc.vms.common.exception.ProtocolEscapeExeption;
import cn.com.cnpc.vms.common.util.BytesUtil;
import cn.com.cnpc.vms.protocols.tcp.T809.T809_MessageHeader;
import cn.com.cnpc.vms.protocols.tcp.T809.util.T809_Util;
import com.hyxt.utils.IRedisMessageListener;
import com.hyxt.utils.U809;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * @author songm
 * @version v1.0
 * @Description 从链路消息处理类（接受来自2.0通过从链路发送的消息）
 * @Date: Create in 18:51 2017/12/12
 * @Modifide By:
 **/
public class SlaveLinkHandler extends IRedisMessageListener {

    private static final Log log = LogFactory.getLog(SlaveLinkHandler.class);

    // <流水号,接入码>
    static LocalCache reqCache = LocalCacheManager.getCache("_809seq");
    // <接入码,企业ID>
    static LocalCache accessCodeToBusinessCache = LocalCacheManager.getCache("_accessCodeToBusiness");

    private Map<String,Object> config;

    /**
     * 平台的一些配置参数，如接入码、平台id、链接地址。。。
     * @param config
     */
    public SlaveLinkHandler(Map<String,Object> config) {
        this.config = config;
    }

    /**
     * 监听缓存收到的消息
     * @param channel
     * @param msg
     */
    public void onMessage(String channel, String msg) {

        System.out.println("809从联路接入信息");
        // 这个位置,记录一下
        byte[] message = msg.getBytes();
        try {
            //反转义信息
            byte[] escape_msg  = T809_Util.reverseEscapeData(message);

            T809_MessageHeader header809 = T809_Util.getHeader(escape_msg);
            // 流水号 如果转发和809一致的话 可以直接用 不一样 可能就要选择去绑定一下了<809流水号,转发流水号> 先直接写成用的一样的
            int responseSn = header809.getSn();
            int messageID = (int) header809.getMessageID();
            //System.out.println("809从联路接入信息");
            log.debug("<<<809从联路接入信息:" + BytesUtil.bytesToHexString(message)+"--"+BytesUtil.bytesToHexString(BytesUtil.int2bytes2(messageID)));

            switch (messageID) {

                /** 前两种情况下 1.直接打包去回 2.我觉得在回之后额外加一个0x05直接去让他断开从链路 */

                case 0x9002:// 从链路连接应答
                    if (reqCache.containsKey(responseSn + "")) {
                        String accessCodeTmp_9002 = String.valueOf(reqCache.remove(responseSn + ""));
                        int businessId_9002 = Integer
                                .valueOf(String.valueOf(accessCodeToBusinessCache.get(accessCodeTmp_9002)));
                        byte[] data = U809.changeSN(escape_msg, responseSn);
                        //System.out.println(BytesUtil.bytesToHexString(data));
                        data = U809.changeCode(escape_msg, Integer.valueOf(accessCodeTmp_9002));
                        data = U809.es(data);
                        //TransferConnection.slaveChannelUpCommand(businessId_9002, BytesUtil.int2bytes2(messageID), data);
                    }
                    break;
                case 0x9006:// 从链路保持应答
                    if (reqCache.containsKey(responseSn + "")) {
                        String accessCodeTmp_9006 = String.valueOf(reqCache.remove(responseSn + ""));
                        int businessId_9006 = Integer
                                .valueOf(String.valueOf(accessCodeToBusinessCache.get(accessCodeTmp_9006)));
                        byte[] data = U809.changeSN(escape_msg, responseSn);
                        data = U809.changeCode(escape_msg, Integer.valueOf(accessCodeTmp_9006));
                        data = U809.es(data);
                        //TransferConnection.slaveChannelUpCommand(businessId_9006, BytesUtil.int2bytes2(messageID), data);
                    }
                    break;
                case 0x9004:// 从链路注销应答
                    if (reqCache.containsKey(responseSn + "")) {
                        String accessCodeTmp_9004 = String.valueOf(reqCache.remove(responseSn + ""));
                        int businessId_9004 = Integer
                                .valueOf(String.valueOf(accessCodeToBusinessCache.get(accessCodeTmp_9004)));
                        byte[] data = U809.changeSN(escape_msg, responseSn);
                        data = U809.changeCode(escape_msg, Integer.valueOf(accessCodeTmp_9004));
                        data = U809.es(data);
                        //TransferConnection.slaveChannelUpCommand(businessId_9004, BytesUtil.int2bytes2(messageID), data);
                    }
                    break;
                case 0x9007:// 从连路断开通知 这个时候 要上一个0x05去通知从链路断开
                    break;
                default:
                    break;
            }
        } catch (ProtocolEscapeExeption protocolEscapeExeption) {
            protocolEscapeExeption.printStackTrace();
        }
    }
}
