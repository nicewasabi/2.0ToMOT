package com.hyxt.DTO.handler;

import cn.com.cnpc.vms.common.exception.ProtocolEscapeExeption;
import cn.com.cnpc.vms.common.util.BytesUtil;
import cn.com.cnpc.vms.protocols.tcp.T809.util.T809_Util;
import com.hyxt.utils.IRedisMessageListener;
import com.hyxt.utils.MessageConstant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author songm
 * @version v1.0
 * @Description 主链路消息处理类（接受来自2.0通过主链路发送的消息）
 * @Date: Create in 18:50 2017/12/12
 * @Modifide By:
 **/
public class MainLinkHandler extends IRedisMessageListener {

    private static final Log log = LogFactory.getLog(MainLinkHandler.class);

    private String ChannelName;

    /**
     * 根据channelName可以确定是哪个平台的链路
     * @param channelName
     */
    public MainLinkHandler(String channelName) {
        ChannelName = channelName;
    }

    /**
     * 监听缓存收到的消息
     * @param channel
     * @param message
     */
    public void onMessage(String channel, String message) {

        // read data
        byte[] data = message.getBytes();
        // [车牌号|车ID] 通过车牌号和车辆颜色 TODO

        // 业务编号

        try {
            //反转义信息
            byte[] data_reverse = T809_Util.reverseEscapeData(data);
            //
            int messageID = (int) T809_Util.getHeader(data_reverse).getMessageID();
            messageID = T809_Util.getMessageID(data_reverse, null);

            log.debug("<<<809主链路接入信息:" + BytesUtil.bytesToHexString(data) + "--" + BytesUtil.bytesToHexString(BytesUtil.int2bytes2(messageID)));

            if (data_reverse[9] == 0x10 && data_reverse[10] == 0x01) {// receive
                // 1001
                // send 1002
                byte[] loginResp = MessageConstant.mainLinkResponseLogin();
                //通过主链路发送登录应答消息（无接口，暂时空）

                // sub channel try to connect and login
                //Connection.subChannelRequestLogin();
                byte[] subReqLogin = MessageConstant.subLinkRequestLogin();
                //通过从链路发送登录请求（无接口，暂时空）

                //TransferConnection.Uplink809Command(12, messageID, data_reverse);
                //通过转发链路发送上行指令（无接口，暂时空）

            } else if (data_reverse[9] == 0x10 && data_reverse[10] == 0x03) {// receive
                // 1003
                //Connection.mainChannelResponseClose();
                byte[] closeResp = MessageConstant.mainLinkResponseClose();
                //通过主链路发送关闭响应，同时关闭从链路（无接口，暂时空）

            } else if (data_reverse[9] == 0x10 && data_reverse[10] == 0x05) {// receive
                // 1005
                // send 1006
                //Connection.mainChannelResponseBeat();
                byte[] beatResp = MessageConstant.mainLinkResponseBeat();
                //通过主链路发送心跳响应（无接口，暂时空）

                //TransferConnection.Uplink809Command(12, messageID, data_reverse);
                //通过转发链路发送上行指令（无接口，暂时空）

            } else {
                if (TransferConnection.authCode.equals("510000")) {
                    if (messageID == 0x1202) {
                        // log.debug("设置速度");
                        int hour = Integer.valueOf(new SimpleDateFormat("HH").format(new Date()));
                        if (6 <= hour && hour < 22) {
                            byte[] a = BytesUtil.int2bytes2(80);
                            data_reverse[77] = a[0];
                            data_reverse[78] = a[1];
                        } else {
                            byte[] a = BytesUtil.int2bytes2(64);
                            data_reverse[77] = a[0];
                            data_reverse[78] = a[1];
                        }
                        System.out.println("1202-----");
                    }
                    // 直接上行809指令

                }
                //TransferConnection.Uplink809Command(12, messageID, data_reverse);
                //通过转发链路发送上行指令（无接口，暂时空）
            }
        } catch (ProtocolEscapeExeption protocolEscapeExeption) {
            protocolEscapeExeption.printStackTrace();
        }
    }
}
