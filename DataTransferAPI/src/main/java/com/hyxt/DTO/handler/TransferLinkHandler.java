package com.hyxt.DTO.handler;

import cn.com.cnpc.vms.common.cache.*;
import cn.com.cnpc.vms.common.exception.ProtocolEscapeExeption;
import cn.com.cnpc.vms.common.util.BytesUtil;
import cn.com.cnpc.vms.protocols.tcp.T809.util.T809_Util;
import com.hyxt.DTO.protocol.*;
import com.hyxt.DTO.protocol.JTBZF_0x82.DataIterm;;
import com.hyxt.utils.IRedisMessageListener;
import com.hyxt.utils.U809;
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

    public LimitedCache cache = LimitedCacheManager.createLimitedCache("timeout", 10000, 120 * 1000, new RemoveLimited() {
        @Override
        public void onRemove(Object key, Object data, String reason) {
            if (LimitedCache.TIMEDELETE.equals(reason)) {

            }
        }
    });

    // 记录流水号 流水号:消息
    //LocalCache reqCache = LocalCacheManager.getCache("_809seq");
    // 平台链路信息 平台id:信息
    //public static LocalCache plateChannelInfoCache = LocalCacheManager.getCache("_plateChannelInfo");
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
            /*LimitedCache cache = TransferConnection.getCache();
            // 如果还没超时 就从计时缓存里删除
            if (cache.containsKey(seqNumber)) {
                cache.remove(seqNumber);
            }*/
            // 通过消息ID来分类判断一下
            switch (messageHeader.getId()) {
                case (byte) 0x20:// 通用应答
                    break;
                case (byte) 0x82:// 平台链路信息
                    byte[] messageBody_0x82 = BytesUtil.cutBytes(8, code.length - 10, code);
                    JTBZF_0x82 body_0x82 = (JTBZF_0x82) ProtocolUtil.getBody((byte) 0x82, messageBody_0x82);

                    List<JTBZF_0x82.DataIterm> listDataIterm = body_0x82.getDataItermList();
                    for (DataIterm dataIterm : listDataIterm) {
                        int platID = dataIterm.getPlatId();
                        int iterm = dataIterm.getItem();
                        if (!((Set<String>) config.get("whileList")).contains(platID+"")) {
                            continue;
                        }
                        //plateChannelInfoCache.put(platID + "", iterm);
                        //这个缓存存放平台主从链路连接状态，舍弃次本地缓存，
                        // 在前台获取平台列表显示主从链路连接断开状态时，老程序中从此缓存会获取不到数据
                        // 解决方案：将数据存入redis或数据库中
                        switch (iterm) {
                            // 主链断开，从链正常
                            case 2:
                                // 这里是验证和dataforword转发服务是否连接，如果没有连接成功，
                                // 这些平台也就不去重连了
                                if (null != config.get("mainLinkChannel")) {
                                    // 如果刚刚被重连过的话，那么就放在小黑屋里放两分钟，两分钟之后在尝试重连，
                                    // 不然重连包太密集
                                    if (!cache.containsKey("PLATID_" + platID)) {
                                        cache.put("PLATID_" + platID, "");
                                        //重连
                                        toLink(platID + "");
                                    }
                                }
                                break;
                            //主从链路都断开
                            case 0:
                                // 这里是验证和dataforword转发服务是否连接，如果没有连接成功，
                                // 这些平台也就不去重连了
                                if (null != config.get("mainLinkChannel")) {
                                    // 如果刚刚被重连过的话，那么就放在小黑屋里放两分钟，两分钟之后在尝试重连，
                                    // 不然重连包太密集
                                    if (!cache.containsKey("PLATID_" + platID)) {
                                        cache.put("PLATID_" + platID, "");
                                        toLink(platID + "");
                                    }
                                }
                                break;
                            default:
                                Set<?> keySet = accessCodeToBusinessCache.keySet();
                                String accessCode = null;
                                for (Object object : keySet) {
                                    if (accessCodeToBusinessCache.get(object).equals(platID + "")) {
                                        accessCode = (String) object;
                                        break;
                                    }
                                }
                                break;
                        }
                    }
                    break;
                case (byte) 0x84:// 下行809指令[直接从链路返回,具体从哪个链路回 根据不同消息ID判断
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
                    //reqCache.put(sn809 + "", GNSSCenterid + "");
                    // 加密码
                    int[] codes = (int[]) config.get(GNSSCenterid + "");
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
                            channelControl(seqNumber, (byte) 1, (byte) 0);
                        }
                        if (messageID == 0x1002 && body_0x84.getBytes_809()[body_0x84.getBytes_809().length - 8] == 0) {

                        }
                        if (messageID == 9008) {

                        }
                    } else if (messageID == 0x9001 || messageID == 0x9003 || messageID == 0x9005) {
                        writeDataBySlaveChannel(body_0x84.getBytes_809());
                    } else if (messageID == 0x9301 || messageID == 0x9401) {
                        // 这种情况下 从从链路回写
                        writeDataBySlaveChannel(body_0x84.getBytes_809());
                    } else {
                        writeDataBySlaveChannel(body_0x84.getBytes_809());
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

    /**
     *单个平台连接（0x03-1001）
     * @param id
     */
    public void toLink(String id) {
        JTBZF_0x03 body_0x03 = new JTBZF_0x03();

        body_0x03.setUid(Integer.valueOf(id));
        body_0x03.setBusiness(BytesUtil.int2bytes2(0x1001));
        byte[] data = U809.create1001(Integer.valueOf(id));
        if (data == null)
            return;
        body_0x03.setBytes_809(data);
        final byte[] message = ProtocolUtil.getMessageByBody((byte) 0x03, body_0x03);
        //调用转发链路通道发送连接消息

        // 发送成功存入计时缓存
        saveLimitedCatch(message);
    }

    /**
     * 将消息存入计时缓存
     * @param message
     */
    void saveLimitedCatch(byte[] message) {
        JTBZFHeader messageHeader = null;
        try {
            messageHeader = ProtocolUtil.getHeader(ProtocolUtil.reverseEscapeData(message));
        } catch (ProtocolEscapeExeption e) {
            e.printStackTrace();
        }
        cache.put(messageHeader.getNum(), messageHeader);
    }

    /**
     *链路控制指令(0x05)
     * @param platId
     * @param masterChannel
     * @param slaveChannel
     */
    void channelControl(int platId, byte masterChannel, byte slaveChannel) {

        JTBZF_0x05 body_0x05 = new JTBZF_0x05();
        body_0x05.setPlatID(platId);
        body_0x05.setMasterChannel(masterChannel);
        body_0x05.setSlaveChannel(slaveChannel);
        final byte[] message = ProtocolUtil.getMessageByBody((byte) 0x05, body_0x05);
        //调用转发链路通道发送消息

    }

    void writeDataBySlaveChannel(byte[] message) {
        //调用从链路通道发送消息

    }
}
