package com.hyxt.DTO.handler;

import cn.com.cnpc.vms.common.cache.LimitedCache;
import cn.com.cnpc.vms.common.cache.LocalCache;
import cn.com.cnpc.vms.common.cache.LocalCacheManager;
import cn.com.cnpc.vms.common.exception.ProtocolEscapeExeption;
import cn.com.cnpc.vms.common.util.BytesUtil;
import cn.com.cnpc.vms.protocols.tcp.T809.util.T809_Util;
import com.hyxt.DTO.protocol.JTBZFHeader;
import com.hyxt.DTO.protocol.JTBZF_0x82;
import com.hyxt.DTO.protocol.JTBZF_0x82.DataIterm;;
import com.hyxt.DTO.protocol.JTBZF_0x84;
import com.hyxt.DTO.protocol.ProtocolUtil;
import com.hyxt.utils.IRedisMessageListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author songm
 * @version v1.0
 * @Description 交通部链路消息处理类（接受来自交通部转发链路的下行消息）
 * @Date: Create in 18:51 2017/12/12
 * @Modifide By:
 **/
public class TransferLinkHandler extends IRedisMessageListener{

    private static final Log log = LogFactory.getLog(TransferLinkHandler.class);

    // 记录流水号 流水号:消息
    LocalCache reqCache = LocalCacheManager.getCache("_809seq");
    // 平台链路信息 平台id:信息
    public static LocalCache plateChannelInfoCache = LocalCacheManager.getCache("_plateChannelInfo");
    // 平台连接标志 接入码key:判断包含
    public static LocalCache upFlag = LocalCacheManager.getCache("upFlag");
    // <接入码,平台ID>
    public static LocalCache accessCodeToBusinessCache = LocalCacheManager.getCache("_accessCodeToBusiness");

    private Map<String,Object> config;

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
            LimitedCache cache = TransferConnection.getCache();
            // 如果还没超时 就从计时缓存里删除
            if (cache.containsKey(seqNumber)) {
                cache.remove(seqNumber);
            }
            // 通过消息ID来分类判断一下
            switch (messageHeader.getId()) {
                case (byte) 0x80:// 认证应答
                    // 应答成功 这个位置就允许上行/下行809,这是一个全局开关
                    TransferConnection.isAuthentication.set(true);
                    // 然后启动心跳，与交通部的心跳
                    TransferConnection.StartUplinkHeartBeat();
                    break;
                case (byte) 0x81:// 心跳应答
                    break;
                case (byte) 0x20:// 通用应答
                    break;
                case (byte) 0x82:// 平台链路信息
                    byte[] messageBody_0x82 = BytesUtil.cutBytes(8, code.length - 10, code);
                    JTBZF_0x82 body_0x82 = (JTBZF_0x82) ProtocolUtil.getBody((byte) 0x82, messageBody_0x82);

                    List<JTBZF_0x82.DataIterm> listDataIterm = body_0x82.getDataItermList();
                    for (DataIterm dataIterm : listDataIterm) {
                        int platID = dataIterm.getPlatId();
                        int iterm = dataIterm.getItem();
                        if (!((Set<String>) LocalCacheManager.getCache("_plateWhiteList").get("whileList")).contains(platID+"")) {
                            continue;
                        }
                        plateChannelInfoCache.put(platID + "", iterm);
                        switch (iterm) {
                            case 2:
                                // 主断开
                                Set<?> keySet = accessCodeToBusinessCache.keySet();
                                String aaa = null;
                                for (Object object : keySet) {
                                    if (accessCodeToBusinessCache.get(object).equals(platID + "")) {
                                        aaa = (String) object;
                                        break;
                                    }
                                }
                                // 如果断开就把该平台发送标志去除
                                upFlag.remove(aaa + "");
                                if (Connection.open) {// 这里是验证和dataforword转发服务是否连接，如果没有连接成功，这些平台也就不去重连了
                                    // 如果刚刚被重连过的话，那么就放在小黑屋里放两分钟，两分钟之后在尝试重连，不然重连包太密集
                                    if (!cache.containsKey("PLATID_" + platID)) {
                                        cache.put("PLATID_" + platID, "");
                                        TransferConnection.toLink(platID + "");
                                    }
                                }
                                break;
                            case 0:
                                Set<?> keySet1 = accessCodeToBusinessCache.keySet();
                                String aaa1 = null;
                                for (Object object : keySet1) {
                                    if (accessCodeToBusinessCache.get(object).equals(platID + "")) {
                                        aaa1 = (String) object;
                                        break;
                                    }
                                }
                                // 如果断开就把该平台发送标志去除
                                upFlag.remove(aaa1 + "");
                                if (Connection.open) {// 这里是验证和dataforword转发服务是否连接，如果没有连接成功，这些平台也就不去重连了
                                    // 如果刚刚被重连过的话，那么就放在小黑屋里放两分钟，两分钟之后在尝试重连，不然重连包太密集
                                    if (!cache.containsKey("PLATID_" + platID)) {
                                        cache.put("PLATID_" + platID, "");
                                        TransferConnection.toLink(platID + "");
                                    }
                                }
                                break;
                            default:
                                Set<?> keySet2 = accessCodeToBusinessCache.keySet();
                                String aaa2 = null;
                                for (Object object : keySet2) {
                                    if (accessCodeToBusinessCache.get(object).equals(platID + "")) {
                                        aaa2 = (String) object;
                                        break;
                                    }
                                }
                                // 如果断开就把该平台发送标志去除
                                upFlag.put(aaa2, "");
                                break;
                        }
                    }
                    break;
                case (byte) 0x84:// 下行809指令[直接从链路返回,具体从哪个链路回 根据不同消息ID判断
                    if (TransferConnection.isAuthentication.get() == false) {
                        // break;
                    }
                    // 得到消息体的byte[]
                    byte[] messageBody_0x84 = BytesUtil.cutBytes(8, code.length - 10, code);
                    JTBZF_0x84 body_0x84 = (JTBZF_0x84) ProtocolUtil.getBody((byte) 0x84, messageBody_0x84);

                    // 完整的下行的809信息
                    byte[] message809 = body_0x84.getBytes_809();

                    // 809这个消息也得转义一下
                    byte[] message809_reverseEscapse = T809_Util.reverseEscapeData(message809);

                    // 直接截取809内部流水号
                    byte[] msg809_sn = BytesUtil.cutBytes(5, 4, message809_reverseEscapse);

                    // 拿到809流水号
                    int sn809 = BytesUtil.bytes2int4(msg809_sn);

                    // 直接截取 接入码
                    byte[] msg_GNSSCenterid = BytesUtil.cutBytes(11, 4, message809_reverseEscapse);

                    // 接入码int
                    int GNSSCenterid = BytesUtil.bytes2int4(msg_GNSSCenterid);
                    // <流水号,接入码>
                    reqCache.put(sn809 + "", GNSSCenterid + "");
                    // 加密码
                    int[] codes = (int[]) Main.accessCodeToM1IA1IC1Cache.get(GNSSCenterid + "");
                    // 获取id
                    int messageID = T809_Util.getMessageID(message809_reverseEscapse, codes);

                    // 如果加密标识为1，标识已加密，下面就是解密
                    if (message809_reverseEscapse[18] == 1) {
                        // 获取加密钥字节
                        byte[] encryptKeyData = BytesUtil.getDWord(19, message809_reverseEscapse);
                        // 截取秘钥
                        int encryptKey = BytesUtil.parseBytesToInt(encryptKeyData);

                        // 解密的数据包
                        byte[] data = U809.en(message809_reverseEscapse, encryptKey, codes);
                        // 赋值
                        body_0x84.setBytes_809(data);

                    }
                    // 分辨一下这个ID，这里的消息需要此程序直接回复，无需进入809，确保809与此程序的链路保持与 此程序和交通部的链路保持相互独立。
                    if (messageID == 0x1002 || messageID == 0x1004 || messageID == 0x1006 || messageID == 0x9007 || messageID == 9008) {

                        // 这个位置 得给人家回一个断开主链路通知
                        if (messageID == 0x1004) {
                            // 哪位是1就断开哪位
                            TransferConnection.channelControl(seqNumber, (byte) 1, (byte) 0);
                            upFlag.remove(GNSSCenterid + "");// 主链路断开响应
                        }
                        if (messageID == 0x1002 && body_0x84.getBytes_809()[body_0x84.getBytes_809().length - 8] == 0) {
                            upFlag.put(GNSSCenterid + "", ""); // 注册成功
                        }
                        if (messageID == 9008) {
                            upFlag.remove(GNSSCenterid + "");// 上级断开通知
                        }
                    } else if (messageID == 0x9001 || messageID == 0x9003 || messageID == 0x9005) {
                        Connection.writeDataBySlaveChannel(body_0x84.getBytes_809());
                    } else if (messageID == 0x9301 || messageID == 0x9401) {
                        //	TransferConnection.pcache.put(sn809 + "", GNSSCenterid + ":" + System.currentTimeMillis());
                        //	System.out.println("!!!"+TransferConnection.pcache);
                        //	SnapshotUtil.createSnapshot(TransferConnection.pcache);
                        // 这种情况下 从从链路回写
                        Connection.writeDataBySlaveChannel(body_0x84.getBytes_809());
                    } else {
                        Connection.writeDataBySlaveChannel(body_0x84.getBytes_809());
                    }
                    log.info(BytesUtil.bytesToHexString(body_0x84.getBytes_809())+"--encode");
                    break;
                default:
                    break;
            }
        } catch (ProtocolEscapeExeption protocolEscapeExeption) {
            protocolEscapeExeption.printStackTrace();
        }
    }
}
